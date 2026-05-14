package iuh.fit.notificationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class
ChatRoom {
    @Id
    private String id; // candidateId_companyId
    private String candidateId;
    private String companyId;
    private String candidateName;
    private String companyName;
    private String companyLogo;
    private String lastMessage;
    private LocalDateTime lastUpdate;
    private int unreadCountCandidate;
    private int unreadCountEmployer;
}
