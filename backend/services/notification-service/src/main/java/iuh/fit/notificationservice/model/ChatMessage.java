package iuh.fit.notificationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String senderId;
    private String receiverId;
    private String senderName;
    private String companyName;
    private String companyLogo;
    private String content;
    private String type; // TEXT, IMAGE, FILE
    private LocalDateTime timestamp;
    private boolean isRead;
}
