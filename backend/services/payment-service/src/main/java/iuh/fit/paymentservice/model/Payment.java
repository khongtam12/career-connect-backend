package iuh.fit.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private String paymentId;

    private String companyId;

    @ManyToOne
    @JoinColumn(name = "packageId")
    private JobPackage jobPackage;
    private double amount;
    private int durationDays;
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    private Method method;

    @Enumerated(EnumType.STRING)
    private StatusPayment status;

    private LocalDateTime paidAt;
}
