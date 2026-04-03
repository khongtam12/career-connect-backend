package iuh.fit.companyservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companysubscriptions")
public class CompanySubscription {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    private String packageId;
    private int jobPostLimit;

    private int jobPostedCount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private StatusPackage status;

}
