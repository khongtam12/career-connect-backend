package iuh.fit.jobservice.controller;

import iuh.fit.jobservice.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/job/chat")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> simpleChat(@RequestParam(value = "request") String request,
                                   @RequestParam(value = "chatId", required = false, defaultValue = "default-chat") String chatId,
                                   @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
                                   @RequestParam(value = "userId", required = false) String userIdParam) {
        String finalUserId = (userIdHeader != null && !userIdHeader.isBlank()) ? userIdHeader : userIdParam;
        return aiService.chatWithTool(request, chatId, finalUserId);
    }
}
