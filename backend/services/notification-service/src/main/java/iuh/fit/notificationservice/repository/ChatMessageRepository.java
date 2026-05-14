package iuh.fit.notificationservice.repository;

import iuh.fit.notificationservice.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // lay tat ca tin nhan cua 1 phong chat cua ung cu vien
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
}
