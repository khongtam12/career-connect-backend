package iuh.fit.paymentservice.dto.request;

import iuh.fit.paymentservice.model.BoxType;
import iuh.fit.paymentservice.model.PackageCategory;
import iuh.fit.paymentservice.model.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPackageUpdateRequest {
    private String name;
    private String description;
    private Double price;
    private Double oldPrice;
    private Integer durationDays;
    private Integer jobPostLimit;
    private PackageCategory category;
    private PackageType type;
    private String badge;
    private String badgeColor;
    private String imageUrl;
    private Boolean showDetails;
    private Boolean active;
    private List<BoxType> allowedBoxTypes;
}
