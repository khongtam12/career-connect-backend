package iuh.fit.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companysubscriptions")
@IdClass(CompanySubscription.subscriptionId.class)
public class CompanySubscription {


    private String companyId;
    @Id
    @ManyToOne
    @JoinColumn(name = "packageId")
    @ToString.Exclude
    private JobPackage jobPackage;
    @Id
    @ManyToOne
    @JoinColumn(name = "paymentId")
    @ToString.Exclude
    private Payment payment;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private StatusPackage status;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class subscriptionId implements Serializable {
        private Payment payment;
        private JobPackage jobPackage;
    }
}
