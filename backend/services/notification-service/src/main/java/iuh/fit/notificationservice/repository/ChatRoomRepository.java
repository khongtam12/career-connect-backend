package iuh.fit.notificationservice.repository;

import iuh.fit.notificationservice.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // lay danh cac cuoc tro chuyen cua 1 cung cu vien
    List<ChatRoom> findByCandidateIdOrderByLastUpdateDesc(String candidateId);
    // lay tat ca cac cuoc tro chuyen cua 1 cong ty voi cac ung cu vien
    List<ChatRoom> findByCompanyIdOrderByLastUpdateDesc(String companyId);
    Optional<ChatRoom> findByCandidateIdAndCompanyId(String candidateId, String companyId);
}
