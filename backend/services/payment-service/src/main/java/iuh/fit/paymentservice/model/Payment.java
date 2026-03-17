package iuh.fit.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    private String paymentId;

    private String subscriptionId;

    private double amount;

    private String transactionCode;

    @Enumerated(EnumType.STRING)
    private Method method;

    @Enumerated(EnumType.STRING)
    private StatusPayment status;

    private LocalDateTime paidAt;
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<CompanySubscription> subscriptions;
}
