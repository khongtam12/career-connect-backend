package iuh.fit.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentItem {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    private String packageId;
    private String packageName;
    private double amount;
    private int durationDays;
    private int quantity;
    private int jobPostLimit;
    private String packageCategory;
    private String packageType;
}
