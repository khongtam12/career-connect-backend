package iuh.fit.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "package_id")
    private String packageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", referencedColumnName = "packageId", insertable = false, updatable = false)
    @JsonIgnore
    private JobPackage jobPackage;

    private String packageName;
    private double amount;
    private int durationDays;
    private int quantity;
    private int jobPostLimit;
    private String packageCategory;
    private String packageType;
}
