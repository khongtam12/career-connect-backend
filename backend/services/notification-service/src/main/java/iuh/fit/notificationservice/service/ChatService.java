package iuh.fit.notificationservice.service;

import iuh.fit.notificationservice.model.ChatMessage;
import iuh.fit.notificationservice.model.ChatRoom;
import iuh.fit.notificationservice.repository.ChatMessageRepository;
import iuh.fit.notificationservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Update ChatRoom
        ChatRoom room = chatRoomRepository.findById(message.getRoomId())
                .orElseGet(() -> createRoom(message));
        
        room.setLastMessage(message.getContent());
        room.setLastUpdate(LocalDateTime.now());
        
        // Simple unread count logic (could be improved)
        if (message.getSenderId().equals(room.getCandidateId())) {
            room.setUnreadCountEmployer(room.getUnreadCountEmployer() + 1);
        } else {
            room.setUnreadCountCandidate(room.getUnreadCountCandidate() + 1);
        }
        
        chatRoomRepository.save(room);
        return savedMessage;
    }

    private ChatRoom createRoom(ChatMessage message) {
        // roomID is usually candidateId_companyId
        String[] parts = message.getRoomId().split("_");
        return ChatRoom.builder()
                .id(message.getRoomId())
                .candidateId(parts[0])
                .companyId(parts[1])
                .candidateName(message.getSenderName())
                .companyName(message.getCompanyName())
                .companyLogo(message.getCompanyLogo())
                .lastUpdate(LocalDateTime.now())
                .unreadCountCandidate(0)
                .unreadCountEmployer(0)
                .build();
    }

    public List<ChatMessage> getHistory(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    public List<ChatRoom> getRoomsForCandidate(String candidateId) {
        return chatRoomRepository.findByCandidateIdOrderByLastUpdateDesc(candidateId);
    }

    public List<ChatRoom> getRoomsForCompany(String companyId) {
        return chatRoomRepository.findByCompanyIdOrderByLastUpdateDesc(companyId);
    }

    public void markAsRead(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElse(null);
        if (room != null) {
            if (userId.equals(room.getCandidateId())) {
                room.setUnreadCountCandidate(0);
            } else {
                room.setUnreadCountEmployer(0);
            }
            chatRoomRepository.save(room);
        }
    }
}
