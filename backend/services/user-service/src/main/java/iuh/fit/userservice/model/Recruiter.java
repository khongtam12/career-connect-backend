package iuh.fit.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="recruiters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {
    @Id
    private String id;
    
    private String username;
    private String email;
    private String companyId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.RECRUITER;

    @Enumerated(EnumType.STRING)
    private RecruiterStatus status = RecruiterStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
