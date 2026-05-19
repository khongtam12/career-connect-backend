package iuh.fit.applicationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import iuh.fit.applicationservice.dto.response.AiSemanticMatchResult;
import iuh.fit.applicationservice.dto.response.CvDetailClientResponse;
import iuh.fit.applicationservice.dto.response.JobDetailClientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LlmMatchAnalysisService {

    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";
    private static final int MAX_PROMPT_TEXT_LENGTH = 6000;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String openAiApiKey;
    private final String openRouterApiKey;
    private final String configuredBaseUrl;
    private final String model;
    private final String appUrl;
    private final String appName;

    public LlmMatchAnalysisService(
            ObjectMapper objectMapper,
            @Value("${OPENAI_API_KEY:}") String openAiApiKey,
            @Value("${OPENROUTER_API_KEY:}") String openRouterApiKey,
            @Value("${OPENAI_BASE_URL:}") String configuredBaseUrl,
            @Value("${OPENAI_MODEL:gpt-4.1-mini}") String model,
            @Value("${FRONTEND_URL:http://localhost:5173}") String appUrl,
            @Value("${spring.application.name:application-service}") String appName
    ) {
        this.objectMapper = objectMapper;
        this.openAiApiKey = openAiApiKey == null ? "" : openAiApiKey.trim();
        this.openRouterApiKey = openRouterApiKey == null ? "" : openRouterApiKey.trim();
        this.configuredBaseUrl = configuredBaseUrl == null ? "" : configuredBaseUrl.trim();
        this.model = model;
        this.appUrl = appUrl;
        this.appName = appName;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }
//
    public AiSemanticMatchResult analyze(
            JobDetailClientResponse job,
            CvDetailClientResponse cv,
            String candidateExperienceYear,
            List<String> jobSkills,
            List<String> cvSkills,
            String jobContextText,
            String cvContextText
    ) {
        String apiKey = resolveApiKey();
        if (apiKey.isBlank()) {
            return null;
        }

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("model", model);
            payload.put("temperature", 0.2);
            payload.put("max_tokens", 900);

            ArrayNode messages = payload.putArray("messages");
            messages.addObject()
                    .put("role", "system")
                    .put("content", """
                            You are an ATS matching analyst.
                            Return valid JSON only.
                            Use the explicit jobSkills list as the source of truth for matchedSkills and missingSkills.
                            Keep matchedSkills and missingSkills concise and limited to explicit skills/tags.
                            Scores must be numbers from 0 to 100.
                            Always write summary, recommendation, strengths, concerns, and interviewFocus in natural Vietnamese with full diacritics.
                            You may analyze Vietnamese or English CV content, but the final writing must be in Vietnamese.
                            Keep the writing concise and recruiter-friendly.
                            """);
            messages.addObject()
                    .put("role", "user")
                    .put("content", buildUserPrompt(job, cv, candidateExperienceYear, jobSkills, cvSkills, jobContextText, cvContextText));

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(resolveBaseUrl() + "/chat/completions"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey);

            if (isUsingOpenRouter()) {
                requestBuilder.header("HTTP-Referer", appUrl);
                requestBuilder.header("X-Title", appName);
            }

            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            if (contentNode.isMissingNode() || contentNode.isNull() || contentNode.asText().isBlank()) {
                return null;
            }

            JsonNode analysisNode = extractJsonNode(contentNode.asText());
            if (analysisNode == null || !analysisNode.isObject()) {
                return null;
            }

            AiSemanticMatchResult result = new AiSemanticMatchResult();
            result.setSemanticScore(numberOrNull(analysisNode.get("semanticScore")));
            result.setMatchedSkills(stringList(analysisNode.get("matchedSkills")));
            result.setMissingSkills(stringList(analysisNode.get("missingSkills")));
            result.setRecommendation(textOrNull(analysisNode.get("recommendation")));
            result.setSummary(textOrNull(analysisNode.get("summary")));
            result.setStrengths(stringList(analysisNode.get("strengths")));
            result.setConcerns(stringList(analysisNode.get("concerns")));
            result.setInterviewFocus(stringList(analysisNode.get("interviewFocus")));
            result.setLlmModel(model);
            result.setAnalysisSource(isUsingOpenRouter() ? "llm-openrouter" : "llm-openai");
            return result;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String buildUserPrompt(
            JobDetailClientResponse job,
            CvDetailClientResponse cv,
            String candidateExperienceYear,
            List<String> jobSkills,
            List<String> cvSkills,
            String jobContextText,
            String cvContextText
    ) {
        return """
                Analyze how well this candidate matches the job.
                Return JSON with this shape only:
                {
                  "semanticScore": 0,
                  "matchedSkills": [],
                  "missingSkills": [],
                  "summary": "",
                  "strengths": [],
                  "concerns": [],
                  "interviewFocus": [],
                  "recommendation": ""
                }

                Rules:
                - semanticScore is 0-100.
                - matchedSkills and missingSkills must come only from the explicit jobSkills list below.
                - strengths, concerns, interviewFocus should each have 2-4 concise bullet-style strings.
                - summary and recommendation should each be 1-2 short sentences.
                - All natural-language output must be in Vietnamese with full diacritics.

                Job title: %s
                Job requirements: %s
                Job experience requirement: %s
                Job education requirement: %s
                Job skills: %s
                Job context text: %s

                Candidate name: %s
                Candidate target title: %s
                Candidate stated experience: %s
                Candidate extracted skills: %s
                Candidate context text: %s
                """.formatted(
                safe(job.getTitle()),
                safe(job.getCandidateRequirements()),
                safe(job.getExperience()),
                safe(job.getEducation()),
                jobSkills,
                truncate(jobContextText),
                safe(cv.getFullName()),
                safe(cv.getJobTitle()),
                safe(candidateExperienceYear),
                cvSkills,
                truncate(cvContextText)
        );
    }

    private JsonNode extractJsonNode(String rawContent) {
        try {
            return objectMapper.readTree(rawContent);
        } catch (Exception ignored) {
            int start = rawContent.indexOf('{');
            int end = rawContent.lastIndexOf('}');
            if (start >= 0 && end > start) {
                try {
                    return objectMapper.readTree(rawContent.substring(start, end + 1));
                } catch (Exception ignoredAgain) {
                    return null;
                }
            }
            return null;
        }
    }

    private List<String> stringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return values;
        }
        node.forEach(item -> {
            String value = textOrNull(item);
            if (value != null && !value.isBlank()) {
                values.add(value.trim());
            }
        });
        return values;
    }

    private Double numberOrNull(JsonNode node) {
        if (node == null || !node.isNumber()) {
            return null;
        }
        return Math.max(0D, Math.min(100D, node.asDouble()));
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String resolveApiKey() {
        return !openAiApiKey.isBlank() ? openAiApiKey : openRouterApiKey;
    }

    private String resolveBaseUrl() {
        if (!configuredBaseUrl.isBlank()) {
            return configuredBaseUrl;
        }
        return isUsingOpenRouter() ? OPENROUTER_BASE_URL : "https://api.openai.com/v1";
    }

    private boolean isUsingOpenRouter() {
        return openAiApiKey.isBlank() && !openRouterApiKey.isBlank();
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replaceAll("\\s+", " ").trim();
        return cleaned.length() > MAX_PROMPT_TEXT_LENGTH
                ? cleaned.substring(0, MAX_PROMPT_TEXT_LENGTH)
                : cleaned;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
