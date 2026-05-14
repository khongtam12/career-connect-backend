package iuh.fit.notificationservice.controller;

import iuh.fit.notificationservice.model.ChatMessage;
import iuh.fit.notificationservice.model.ChatRoom;
import iuh.fit.notificationservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // --- WebSocket ---
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        ChatMessage saved = chatService.saveMessage(message);
        
        // Gửi tới người nhận qua topic cá nhân
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getReceiverId(), saved);
        
        // Gửi ngược lại cho người gửi để xác nhận
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getSenderId(), saved);
    }

    // --- REST ---
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getHistory(roomId));
    }
    //candidate b1
    @GetMapping("/rooms/candidate/{candidateId}")
    public ResponseEntity<List<ChatRoom>> getRoomsForCandidate(@PathVariable String candidateId) {
        return ResponseEntity.ok(chatService.getRoomsForCandidate(candidateId));
    }

    @GetMapping("/rooms/company/{companyId}")
    public ResponseEntity<List<ChatRoom>> getRoomsForCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(chatService.getRoomsForCompany(companyId));
    }

    @PostMapping("/read/{roomId}/{userId}")
    public ResponseEntity<Void> markAsRead(@PathVariable String roomId, @PathVariable String userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
}
