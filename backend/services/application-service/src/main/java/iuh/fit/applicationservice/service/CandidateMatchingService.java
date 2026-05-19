package iuh.fit.applicationservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.applicationservice.dto.response.AiSemanticMatchResult;
import iuh.fit.applicationservice.dto.response.CandidateMatchInsight;
import iuh.fit.applicationservice.dto.response.CvDetailClientResponse;
import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CandidateMatchingService {

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Set<String> STOP_WORDS = Set.of(
            "va", "voi", "cho", "cua", "tren", "duoi", "tai", "mot", "nhung", "cac",
            "the", "can", "yeu", "cau", "kinh", "nghiem", "lam", "viec", "ung", "vien",
            "job", "developer", "engineer", "staff", "nhan", "su", "vi", "tri"
    );

    private final ObjectMapper objectMapper;
    private final PdfTextExtractionService pdfTextExtractionService;
    private final LlmMatchAnalysisService llmMatchAnalysisService;

    public CandidateMatchingService(
            ObjectMapper objectMapper,
            PdfTextExtractionService pdfTextExtractionService,
            LlmMatchAnalysisService llmMatchAnalysisService
    ) {
        this.objectMapper = objectMapper;
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.llmMatchAnalysisService = llmMatchAnalysisService;
    }

    public CandidateMatchInsight match(JobDetailClientResponse job, CvDetailClientResponse cv, String candidateExperienceYear) {
        CandidateMatchInsight insight = new CandidateMatchInsight();

        if (job == null) {
            insight.setRecommendation("Không ther đánh giá vì thiếu thông tin JD.");
            return insight;
        }

        if (cv == null) {
            insight.setRecommendation("Ứng viên tải Cv ngoài hệ thống, chưa đủ dư liệu để AI matching.");
            return insight;
        }

        String cvPdfText = pdfTextExtractionService.extractText(cv.getFileUrl());
        String jobContextText = buildJobContextText(job);
        String cvContextText = buildCvContextText(cv, candidateExperienceYear, cvPdfText);

        List<String> jobSkills = extractJobSkills(job);
        List<String> cvSkills = extractCvSkills(cv, cvPdfText);

        Set<String> matchedSkills = new LinkedHashSet<>();
        Set<String> missingSkills = new LinkedHashSet<>();

        for (String requiredSkill : jobSkills) {
            if (containsNormalized(cvSkills, requiredSkill)) {
                matchedSkills.add(requiredSkill);
            } else {
                missingSkills.add(requiredSkill);
            }
        }

        AiSemanticMatchResult aiResult = llmMatchAnalysisService.analyze(
                job,
                cv,
                candidateExperienceYear,
                jobSkills,
                cvSkills,
                jobContextText,
                cvContextText
        );

        double skillScore = jobSkills.isEmpty()
                ? 80D
                : ((double) matchedSkills.size() / jobSkills.size()) * 100D;

        double requiredYears = parseYears(job.getExperience());
        double candidateYears = parseYears(candidateExperienceYear);
        if (candidateYears <= 0 && cv.getExperiences() != null) {
            candidateYears = cv.getExperiences().size();
        }
        double experienceScore = requiredYears <= 0
                ? (candidateYears > 0 ? 100D : 60D)
                : Math.min(candidateYears / requiredYears, 1D) * 100D;

        double educationScore = calculateEducationScore(job, cv);
        double keywordScore = calculateKeywordScore(job, cv, matchedSkills, cvSkills, cvPdfText);
        double semanticScore = aiResult != null && aiResult.getSemanticScore() != null
                ? aiResult.getSemanticScore()
                : keywordScore;

        double overallScore = (skillScore * 0.40D)
                + (experienceScore * 0.20D)
                + (educationScore * 0.10D)
                + (keywordScore * 0.10D)
                + (semanticScore * 0.20D);

        insight.setMatchScore(round(overallScore));
        insight.setSkillScore(round(skillScore));
        insight.setExperienceScore(round(experienceScore));
        insight.setEducationScore(round(educationScore));
        insight.setKeywordScore(round(keywordScore));
        insight.setSemanticScore(round(semanticScore));
        insight.setMatchedSkills(new ArrayList<>(matchedSkills));
        insight.setMissingSkills(new ArrayList<>(missingSkills));
        insight.setSummary(aiResult != null ? aiResult.getSummary() : null);
        insight.setStrengths(aiResult != null && aiResult.getStrengths() != null ? aiResult.getStrengths() : new ArrayList<>());
        insight.setConcerns(aiResult != null && aiResult.getConcerns() != null ? aiResult.getConcerns() : new ArrayList<>());
        insight.setInterviewFocus(aiResult != null && aiResult.getInterviewFocus() != null ? aiResult.getInterviewFocus() : new ArrayList<>());
        insight.setAnalysisSource(aiResult != null ? aiResult.getAnalysisSource() : "rule-based");
        insight.setLlmModel(aiResult != null ? aiResult.getLlmModel() : null);
        insight.setRecommendation(
                aiResult != null && aiResult.getRecommendation() != null && !aiResult.getRecommendation().isBlank()
                        ? aiResult.getRecommendation()
                        : buildRecommendation(overallScore, semanticScore, matchedSkills.size(), missingSkills.size(), requiredYears, candidateYears)
        );

        return insight;
    }

    private List<String> extractJobSkills(JobDetailClientResponse job) {
        Set<String> result = new LinkedHashSet<>();
        result.addAll(parseJsonArray(job.getSkills()));
        result.addAll(parseCsv(job.getRequirementTags()));

        // Keep the visible skill list focused on explicit skills/tags only.
        // Rich requirement prose is still used for keyword/semantic scoring,
        // but it should not appear as "matched/missing skills" chips.
        if (result.isEmpty()) {
            result.addAll(parseCsv(job.getCandidateRequirements()));
        }

        return result.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .filter(this::isLikelySkill)
                .limit(12)
                .toList();
    }

    private List<String> extractCvSkills(CvDetailClientResponse cv, String cvPdfText) {
        Set<String> result = new LinkedHashSet<>();
        if (cv.getSkills() != null) {
            cv.getSkills().stream()
                    .map(CvDetailClientResponse.SkillInfo::getName)
                    .filter(name -> name != null && !name.isBlank())
                    .forEach(result::add);
        }
        if (cv.getJobTitle() != null) {
            result.addAll(extractKeywords(cv.getJobTitle()));
        }
        if (cv.getSummary() != null) {
            result.addAll(extractKeywords(cv.getSummary()));
        }
        if (cv.getExperiences() != null) {
            cv.getExperiences().forEach(exp -> {
                result.addAll(extractKeywords(exp.getRole()));
                result.addAll(extractKeywords(exp.getDescription()));
            });
        }

        if (result.size() < 3 && cvPdfText != null && !cvPdfText.isBlank()) {
            result.addAll(extractKeywords(cvPdfText).stream().limit(20).toList());
        }

        return result.stream()
                .filter(this::isLikelySkill)
                .toList();
    }

    private double calculateEducationScore(JobDetailClientResponse job, CvDetailClientResponse cv) {
        String requiredEducation = normalize(job.getEducation());
        if (requiredEducation == null) {
            return cv.getEducations() == null || cv.getEducations().isEmpty() ? 60D : 100D;
        }

        if (cv.getEducations() == null || cv.getEducations().isEmpty()) {
            return 0D;
        }

        String cvEducationText = cv.getEducations().stream()
                .map(education -> String.join(" ",
                        valueOrEmpty(education.getSchool()),
                        valueOrEmpty(education.getMajor()),
                        valueOrEmpty(education.getDescription())))
                .collect(Collectors.joining(" "));
        String normalizedCvEducation = normalize(cvEducationText);

        if (normalizedCvEducation == null) {
            return 50D;
        }

        if (normalizedCvEducation.contains(requiredEducation)) {
            return 100D;
        }

        if (requiredEducation.contains("dai hoc") || requiredEducation.contains("cu nhan")) {
            return normalizedCvEducation.contains("dai hoc") || normalizedCvEducation.contains("cu nhan") ? 100D : 50D;
        }

        if (requiredEducation.contains("cao dang")) {
            return normalizedCvEducation.contains("cao dang") || normalizedCvEducation.contains("dai hoc") ? 100D : 50D;
        }

        return 60D;
    }

    private double calculateKeywordScore(
            JobDetailClientResponse job,
            CvDetailClientResponse cv,
            Set<String> matchedSkills,
            List<String> cvSkills,
            String cvPdfText
    ) {
        Set<String> jobKeywords = new LinkedHashSet<>();
        jobKeywords.addAll(extractKeywords(job.getTitle()));
        jobKeywords.addAll(extractKeywords(job.getDescription()));
        jobKeywords.addAll(extractKeywords(job.getCandidateRequirements()));

        Set<String> cvKeywords = new LinkedHashSet<>();
        cvKeywords.addAll(extractKeywords(cv.getJobTitle()));
        cvKeywords.addAll(extractKeywords(cv.getSummary()));
        cvKeywords.addAll(cvSkills.stream().map(this::normalize).filter(value -> value != null && !value.isBlank()).toList());
        cvKeywords.addAll(extractKeywords(cvPdfText));

        if (jobKeywords.isEmpty()) {
            return matchedSkills.isEmpty() ? 60D : 90D;
        }

        long overlap = jobKeywords.stream().filter(cvKeywords::contains).count();
        return Math.min(((double) overlap / jobKeywords.size()) * 100D, 100D);
    }

    private String buildJobContextText(JobDetailClientResponse job) {
        return String.join(" ",
                valueOrEmpty(job.getTitle()),
                valueOrEmpty(job.getDescription()),
                valueOrEmpty(job.getCandidateRequirements()),
                valueOrEmpty(job.getExperience()),
                valueOrEmpty(job.getEducation()),
                valueOrEmpty(job.getRequirementTags()),
                valueOrEmpty(job.getSkills())
        ).trim();
    }

    private String buildCvContextText(CvDetailClientResponse cv, String candidateExperienceYear, String cvPdfText) {
        List<String> sections = new ArrayList<>();
        sections.add(valueOrEmpty(cv.getFullName()));
        sections.add(valueOrEmpty(cv.getJobTitle()));
        sections.add(valueOrEmpty(cv.getSummary()));
        sections.add(valueOrEmpty(candidateExperienceYear));

        if (cv.getSkills() != null) {
            cv.getSkills().stream()
                    .map(CvDetailClientResponse.SkillInfo::getName)
                    .filter(name -> name != null && !name.isBlank())
                    .forEach(sections::add);
        }

        if (cv.getExperiences() != null) {
            cv.getExperiences().forEach(exp -> sections.add(String.join(" ",
                    valueOrEmpty(exp.getCompany()),
                    valueOrEmpty(exp.getRole()),
                    valueOrEmpty(exp.getDescription()))));
        }

        if (cv.getEducations() != null) {
            cv.getEducations().forEach(education -> sections.add(String.join(" ",
                    valueOrEmpty(education.getSchool()),
                    valueOrEmpty(education.getMajor()),
                    valueOrEmpty(education.getDescription()))));
        }

        if (cvPdfText != null && !cvPdfText.isBlank()) {
            sections.add(cvPdfText);
        }

        return sections.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" "));
    }

    private String buildRecommendation(double overallScore, double semanticScore, int matchedSkillCount, int missingSkillCount, double requiredYears, double candidateYears) {
        if (overallScore >= 80D) {
            return "Rat phu hop de uu tien shortlist. Ky nang khop tot va semantic matching cao.";
        }
        if (overallScore >= 65D || semanticScore >= 75D) {
            return "Kha phu hop. Nen review sau hon phan du an, kinh nghiem va CV goc truoc khi quyet dinh.";
        }
        if (missingSkillCount > matchedSkillCount) {
            return "Muc do phu hop chua cao do thieu nhieu ky nang theo JD. Nen can nhac cho pipeline du phong.";
        }
        if (requiredYears > 0 && candidateYears < requiredYears) {
            return "Ky nang co diem sang nhung kinh nghiem con thap hon yeu cau. Phu hop neu job chap nhan ung vien tiem nang.";
        }
        return "Muc do phu hop trung binh. Can recruiter doc CV chi tiet de xac nhan them.";
    }

    private boolean containsNormalized(List<String> values, String target) {
        String normalizedTarget = normalize(target);
        if (normalizedTarget == null) {
            return false;
        }

        for (String value : values) {
            String normalizedValue = normalize(value);
            if (normalizedValue == null) {
                continue;
            }
            if (skillsEquivalent(normalizedValue, normalizedTarget)) {
                return true;
            }
        }
        return false;
    }

    private List<String> parseJsonArray(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> values = objectMapper.readValue(raw, new TypeReference<List<String>>() {});
            return values.stream()
                    .flatMap(value -> splitRichTextContent(value).stream())
                    .map(this::toDisplaySkill)
                    .filter(value -> value != null && !value.isBlank())
                    .filter(this::isLikelySkill)
                    .toList();
        } catch (Exception ignored) {
            return parseCsv(raw);
        }
    }

    private List<String> parseCsv(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return splitRichTextContent(raw).stream()
                .map(this::toDisplaySkill)
                .filter(value -> !value.isBlank())
                .filter(this::isLikelySkill)
                .toList();
    }

    private List<String> extractKeywords(String text) {
        String normalized = normalize(stripHtml(text));
        if (normalized == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(normalized.split("[^a-z0-9+#.]"))
                .map(String::trim)
                .filter(token -> token.length() >= 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .distinct()
                .toList();
    }

    private double parseYears(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0D;
        }
        Matcher matcher = YEAR_PATTERN.matcher(raw.replace(',', '.'));
        if (!matcher.find()) {
            return 0D;
        }
        try {
            return Double.parseDouble(matcher.group(1));
        } catch (NumberFormatException ex) {
            return 0D;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9+#.\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean skillsEquivalent(String left, String right) {
        if (left.equals(right)) {
            return true;
        }

        String compactLeft = left.replace(" ", "");
        String compactRight = right.replace(" ", "");
        if (compactLeft.equals(compactRight)) {
            return true;
        }

        String[] leftTokens = left.split("\\s+");
        String[] rightTokens = right.split("\\s+");
        int minTokens = Math.min(leftTokens.length, rightTokens.length);
        int maxTokens = Math.max(leftTokens.length, rightTokens.length);

        if (minTokens <= 2 && maxTokens <= 3) {
            if ((left.startsWith(right + " ") || left.endsWith(" " + right) || left.contains(" " + right + " "))
                    && right.length() >= 3) {
                return true;
            }
            if ((right.startsWith(left + " ") || right.endsWith(" " + left) || right.contains(" " + left + " "))
                    && left.length() >= 3) {
                return true;
            }
        }

        return false;
    }

    private boolean isLikelySkill(String value) {
        String cleaned = value == null ? null : value.trim();
        if (cleaned == null || cleaned.isBlank()) {
            return false;
        }

        if (cleaned.length() > 40) {
            return false;
        }

        String normalized = normalize(cleaned);
        if (normalized == null) {
            return false;
        }

        String[] tokens = normalized.split("\\s+");
        if (tokens.length > 4) {
            return false;
        }

        return !normalized.matches(".*\\b(co|can|yeu|uu|toi|lam|viec|kinh|nghiem|thoi|gian|duoi|ap|luc)\\b.*");
    }

    private double round(double value) {
        return Math.round(value * 10D) / 10D;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private List<String> splitRichTextContent(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }

        String cleaned = stripHtml(raw)
                .replace("[", " ")
                .replace("]", " ")
                .replace("\"", " ")
                .replace("•", "\n")
                .replaceAll("\\s*/\\s*li\\s*>", "\n");

        return Arrays.stream(cleaned.split("[,;\\n\\r]+"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String stripHtml(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return HTML_TAG_PATTERN.matcher(value)
                .replaceAll(" ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String toDisplaySkill(String value) {
        String cleaned = stripHtml(value)
                .replaceAll("^[-•]+", "")
                .replaceAll("\\s+", " ")
                .trim();
        return cleaned.isBlank() ? null : cleaned;
    }
}
