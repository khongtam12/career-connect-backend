package iuh.fit.applicationservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.applicationservice.dto.response.AiSemanticMatchResult;
import iuh.fit.applicationservice.dto.response.CandidateMatchInsight;
import iuh.fit.applicationservice.dto.response.CvDetailClientResponse;
import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CandidateMatchingService {
    private static final String ANALYSIS_VERSION = "candidate-match-v4-hybrid";

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Set<String> STOP_WORDS = Set.of(
            "va", "voi", "cho", "cua", "tren", "duoi", "tai", "mot", "nhung", "cac",
            "the", "can", "yeu", "cau", "kinh", "nghiem", "lam", "viec", "ung", "vien",
            "job", "developer", "engineer", "staff", "nhan", "su", "vi", "tri"
    );
    private static final Map<String, String> TERM_ALIASES = buildTermAliases();

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

        double overallScore = (skillScore * 0.34D)
                + (experienceScore * 0.18D)
                + (educationScore * 0.10D)
                + (keywordScore * 0.10D)
                + (semanticScore * 0.28D);

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

    public String buildAnalysisFingerprint(JobDetailClientResponse job, CvDetailClientResponse cv, String candidateExperienceYear) {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(ANALYSIS_VERSION);
        joiner.add(nullToEmpty(llmMatchAnalysisService.getModel()));
        joiner.add(nullToEmpty(job != null ? job.getJobId() : null));
        joiner.add(nullToEmpty(job != null ? job.getTitle() : null));
        joiner.add(nullToEmpty(job != null ? job.getDescription() : null));
        joiner.add(nullToEmpty(job != null ? job.getCandidateRequirements() : null));
        joiner.add(nullToEmpty(job != null ? job.getExperience() : null));
        joiner.add(nullToEmpty(job != null ? job.getEducation() : null));
        joiner.add(nullToEmpty(job != null ? job.getRequirementTags() : null));
        joiner.add(nullToEmpty(job != null ? job.getSkills() : null));
        joiner.add(nullToEmpty(job != null ? job.getStatus() : null));
        joiner.add(String.valueOf(job != null ? job.getDeadline() : null));
        joiner.add(String.valueOf(job != null ? job.getDeletedAt() : null));
        joiner.add(nullToEmpty(cv != null ? cv.getId() : null));
        joiner.add(nullToEmpty(cv != null ? cv.getFullName() : null));
        joiner.add(nullToEmpty(cv != null ? cv.getJobTitle() : null));
        joiner.add(nullToEmpty(cv != null ? cv.getSummary() : null));
        joiner.add(nullToEmpty(cv != null ? cv.getFileUrl() : null));
        joiner.add(nullToEmpty(candidateExperienceYear));
        joiner.add(joinSkillData(cv));
        joiner.add(joinExperienceData(cv));
        joiner.add(joinEducationData(cv));
        return sha256(joiner.toString());
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
        String requiredEducation = canonicalizeNormalized(normalize(job.getEducation()));
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
        String normalizedCvEducation = canonicalizeNormalized(normalize(cvEducationText));

        if (normalizedCvEducation == null) {
            return 50D;
        }

        if (normalizedCvEducation.contains(requiredEducation)) {
            return 100D;
        }

        if (requiredEducation.contains("bachelor")) {
            return normalizedCvEducation.contains("bachelor") ? 100D : 50D;
        }

        if (requiredEducation.contains("college")) {
            return normalizedCvEducation.contains("college") || normalizedCvEducation.contains("bachelor") ? 100D : 50D;
        }

        if (requiredEducation.contains("master")) {
            return normalizedCvEducation.contains("master") ? 100D : 50D;
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
        cvKeywords.addAll(cvSkills.stream()
                .map(this::normalize)
                .map(this::canonicalizeNormalized)
                .filter(value -> value != null && !value.isBlank())
                .toList());
        cvKeywords.addAll(extractKeywords(cvPdfText));

        if (jobKeywords.isEmpty()) {
            return matchedSkills.isEmpty() ? 60D : 90D;
        }

        long overlap = jobKeywords.stream()
                .map(this::canonicalizeNormalized)
                .filter(value -> value != null && cvKeywords.contains(value))
                .count();
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
            return "Rất phù hợp để ưu tiên shortlist. Kỹ năng khớp tốt và semantic matching cao.";
        }
        if (overallScore >= 65D || semanticScore >= 75D) {
            return "Khá phù hợp. Nên review sâu hơn phần dự án, kinh nghiệm và CV gốc trước khi quyết định.";
        }
        if (missingSkillCount > matchedSkillCount) {
            return "Mức độ phù hợp chưa cao do thiếu nhiều kỹ năng theo JD. Nên cân nhắc cho pipeline dự phòng.";
        }
        if (requiredYears > 0 && candidateYears < requiredYears) {
            return "Kỹ năng có điểm sáng nhưng kinh nghiệm còn thấp hơn yêu cầu. Phù hợp nếu job chấp nhận ứng viên tiềm năng.";
        }
        return "Mức độ phù hợp trung bình. Cần recruiter đọc CV chi tiết để xác nhận thêm.";
    }

    private boolean containsNormalized(List<String> values, String target) {
        String normalizedTarget = canonicalizeNormalized(normalize(target));
        if (normalizedTarget == null) {
            return false;
        }

        for (String value : values) {
            String normalizedValue = canonicalizeNormalized(normalize(value));
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

        Set<String> keywords = new LinkedHashSet<>(Arrays.stream(normalized.split("[^a-z0-9+#.]"))
                .map(String::trim)
                .filter(token -> token.length() >= 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .map(this::canonicalizeNormalized)
                .filter(token -> token != null && !token.isBlank())
                .toList());

        TERM_ALIASES.forEach((phrase, canonical) -> {
            if (normalized.contains(phrase)) {
                keywords.add(canonical);
            }
        });

        return new ArrayList<>(keywords);
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
        left = canonicalizeNormalized(left);
        right = canonicalizeNormalized(right);
        if (left == null || right == null) {
            return false;
        }
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
    private String joinSkillData(CvDetailClientResponse cv) {
        if (cv == null || cv.getSkills() == null) {
            return "";
        }
        return cv.getSkills().stream()
                .map(skill -> nullToEmpty(skill.getName()) + ":" + nullToEmpty(skill.getLevel()))
                .collect(Collectors.joining(","));
    }

    private String joinExperienceData(CvDetailClientResponse cv) {
        if (cv == null || cv.getExperiences() == null) {
            return "";
        }
        return cv.getExperiences().stream()
                .map(exp -> nullToEmpty(exp.getCompany()) + ":" + nullToEmpty(exp.getRole()) + ":" + nullToEmpty(exp.getStartDate()) + ":" + nullToEmpty(exp.getEndDate()) + ":" + nullToEmpty(exp.getDescription()))
                .collect(Collectors.joining(","));
    }

    private String joinEducationData(CvDetailClientResponse cv) {
        if (cv == null || cv.getEducations() == null) {
            return "";
        }
        return cv.getEducations().stream()
                .map(edu -> nullToEmpty(edu.getSchool()) + ":" + nullToEmpty(edu.getMajor()) + ":" + nullToEmpty(edu.getStartDate()) + ":" + nullToEmpty(edu.getEndDate()) + ":" + nullToEmpty(edu.getDescription()))
                .collect(Collectors.joining(","));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String canonicalizeNormalized(String normalized) {
        if (normalized == null || normalized.isBlank()) {
            return null;
        }
        String exact = TERM_ALIASES.get(normalized);
        if (exact != null) {
            return exact;
        }
        for (Map.Entry<String, String> entry : TERM_ALIASES.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return normalized;
    }

    private static Map<String, String> buildTermAliases() {
        Map<String, String> aliases = new LinkedHashMap<>();
        addAlias(aliases, "project management", "project management", "project manager", "pm", "quan ly du an");
        addAlias(aliases, "communication", "communication", "giao tiep", "ky nang giao tiep");
        addAlias(aliases, "teamwork", "teamwork", "lam viec nhom");
        addAlias(aliases, "problem solving", "problem solving", "giai quyet van de");
        addAlias(aliases, "customer service", "customer service", "cham soc khach hang", "dich vu khach hang");
        addAlias(aliases, "sales", "sales", "ban hang", "kinh doanh");
        addAlias(aliases, "marketing", "marketing", "tiep thi");
        addAlias(aliases, "business analysis", "business analysis", "phan tich nghiep vu");
        addAlias(aliases, "product management", "product management", "quan ly san pham");
        addAlias(aliases, "data analysis", "data analysis", "phan tich du lieu");
        addAlias(aliases, "recruitment", "recruitment", "tuyen dung");
        addAlias(aliases, "human resources", "human resources", "nhan su");
        addAlias(aliases, "accounting", "accounting", "ke toan");
        addAlias(aliases, "testing", "testing", "kiem thu", "quality assurance", "quality control");
        addAlias(aliases, "manual testing", "manual testing", "kiem thu thu cong");
        addAlias(aliases, "automation testing", "automation testing", "kiem thu tu dong", "test automation");
        addAlias(aliases, "frontend", "frontend", "front end");
        addAlias(aliases, "backend", "backend", "back end");
        addAlias(aliases, "full stack", "full stack", "fullstack");
        addAlias(aliases, "ui ux", "ui ux", "ui ux design", "ui/ux", "thiet ke ui ux", "thiet ke giao dien");
        addAlias(aliases, "english", "english", "tieng anh");
        addAlias(aliases, "vietnamese", "vietnamese", "tieng viet");
        addAlias(aliases, "leadership", "leadership", "lanh dao");
        addAlias(aliases, "negotiation", "negotiation", "dam phan");
        addAlias(aliases, "presentation", "presentation", "thuyet trinh");
        addAlias(aliases, "bachelor", "bachelor", "cu nhan", "dai hoc", "bachelor degree", "bachelor s degree");
        addAlias(aliases, "college", "college", "cao dang", "associate degree");
        addAlias(aliases, "master", "master", "thac si", "master degree");
        return aliases;
    }

    private static void addAlias(Map<String, String> aliases, String canonical, String... variants) {
        for (String variant : variants) {
            String normalized = normalizeStatic(variant);
            if (normalized != null) {
                aliases.put(normalized, canonical);
            }
        }
    }

    private static String normalizeStatic(String value) {
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

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception ex) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
