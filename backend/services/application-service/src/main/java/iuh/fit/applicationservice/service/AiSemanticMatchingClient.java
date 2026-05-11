package iuh.fit.applicationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

@Service
public class AiSemanticMatchingClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public AiSemanticMatchingClient(
            ObjectMapper objectMapper,
            @Value("${ai.matching.service.url:http://localhost:8090}") String baseUrl
    ) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public AiSemanticMatchResult match(
            JobDetailClientResponse job,
            CvDetailClientResponse cv,
            String candidateExperienceYear,
            List<String> jobSkills,
            List<String> cvSkills
    ) {
        try {
            ObjectNode jobNode = objectMapper.createObjectNode();
            jobNode.put("title", valueOrEmpty(job.getTitle()));
            jobNode.put("description", valueOrEmpty(job.getDescription()));
            jobNode.put("candidateRequirements", valueOrEmpty(job.getCandidateRequirements()));
            jobNode.put("experience", valueOrEmpty(job.getExperience()));
            jobNode.put("education", valueOrEmpty(job.getEducation()));
            jobNode.set("skills", objectMapper.valueToTree(jobSkills));

            ObjectNode cvNode = objectMapper.createObjectNode();
            cvNode.put("fullName", valueOrEmpty(cv.getFullName()));
            cvNode.put("jobTitle", valueOrEmpty(cv.getJobTitle()));
            cvNode.put("summary", valueOrEmpty(cv.getSummary()));
            cvNode.set("skills", objectMapper.valueToTree(cvSkills));

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("candidateExperienceYear", candidateExperienceYear == null ? "" : candidateExperienceYear);
            requestBody.set("job", jobNode);
            requestBody.set("cv", cvNode);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/match"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }
            return objectMapper.readValue(response.body(), AiSemanticMatchResult.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
