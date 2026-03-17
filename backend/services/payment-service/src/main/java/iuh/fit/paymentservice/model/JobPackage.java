package iuh.fit.paymentservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobpackages")
public class JobPackage {
    @Id
    private String packageId;

    private String name;

    private double price;

    private int durationDays;

    private int jobPostLimit;

    private String description;
    @OneToMany(mappedBy = "jobPackage", cascade = CascadeType.ALL)
    private List<CompanySubscription> subscriptions;
}
