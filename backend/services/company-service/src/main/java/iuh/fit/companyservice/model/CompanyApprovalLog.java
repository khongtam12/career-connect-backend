package iuh.fit.companyservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "company_approval_logs")
public class CompanyApprovalLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "company_id")
    private String companyId;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus action;

    @Column(name = "performed_by")
    private String performedBy;

    private LocalDateTime timestamp;

    private String note;
}
