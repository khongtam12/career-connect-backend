package iuh.fit.cvservice.service;

import iuh.fit.cvservice.dto.AIReviewResponse;
import iuh.fit.cvservice.model.CV;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;
    
    @org.springframework.beans.factory.annotation.Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public AIReviewResponse reviewCV(CV cv) {
        log.info("Generating AI review. API Key starts with: {}", (apiKey != null && apiKey.length() > 5) ? apiKey.substring(0, 5) : "NULL/EMPTY");
        log.info("Generating AI review for CV of user: {}", cv.getUserId());

        var outputParser = new BeanOutputParser<>(AIReviewResponse.class);

        String userPrompt = """
                Bạn là chuyên gia HR cao cấp. Hãy phân tích CV này thật nhanh và súc tích.
                BẮT BUỘC PHẢN HỒI BẰNG TIẾNG VIỆT.
                
                Thông tin: {jobTitle}, {summary}, Skills: {skills}, Exp: {experiences}.
                
                Chỉ tập trung vào:
                1. Điểm số (0-100)
                2. Ưu điểm chính (ngắn gọn)
                3. Điểm cần sửa gấp (ngắn gọn)
                
                Phản hồi JSON:
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                "fullName", cv.getFullName(),
                "jobTitle", cv.getJobTitle(),
                "summary", cv.getSummary() != null ? cv.getSummary() : "Chưa có",
                "skills", cv.getSkills() != null ? cv.getSkills().toString() : "Chưa có",
                "experiences", cv.getExperiences() != null ? cv.getExperiences().toString() : "Chưa có",
                "projects", cv.getProjects() != null ? cv.getProjects().toString() : "Chưa có",
                "format", outputParser.getFormat()
        ));

        try {
            ChatResponse response = chatClient.call(prompt);
            String content = response.getResult().getOutput().getContent();
            log.info("Raw AI Response: {}", content);

            // Clean up markdown if present
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                content = content.substring(0, content.lastIndexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                content = content.substring(0, content.lastIndexOf("```"));
            }
            content = content.trim();

            return outputParser.parse(content);
        } catch (Exception e) {
            log.error("Error calling AI Service: ", e.getMessage());
            throw new RuntimeException("AI Service failed: " + e.getMessage(), e);
        }
    }
}
