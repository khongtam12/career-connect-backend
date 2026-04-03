package iuh.fit.paymentservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "jobpackages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPackage {

    @Id
    private String packageId;

    private String name;

    private double price;

    private int durationDays;

    private int jobPostLimit;

    private String description;
    @OneToMany(mappedBy = "jobPackage")
    private List<Payment> payments;
}
