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

    private String description;

    private double price;

    private Double oldPrice;

    private int durationDays;

    private int jobPostLimit;

    @Enumerated(EnumType.STRING)
    private PackageCategory category;

    @Enumerated(EnumType.STRING)
    private PackageType type;

    private String badge;
    private String badgeColor;

    private String imageUrl;

    private boolean showDetails;

    private boolean isActive;


    @ElementCollection(targetClass = BoxType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "jobpackage_box_types",
            joinColumns = @JoinColumn(name = "package_id")
    )
    @Column(name = "box_type")
    private List<BoxType> allowedBoxTypes;

    @OneToMany(mappedBy = "jobPackage")
    private List<Payment> payments;
}