package iuh.fit.paymentservice.dto.response;

import iuh.fit.paymentservice.model.BoxType;
import iuh.fit.paymentservice.model.PackageCategory;
import iuh.fit.paymentservice.model.PackageType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPackageResponseDTO {

    private String packageId;
    private String name;
    private String description;

    private double price;
    private Double oldPrice;

    private int durationDays;
    private int jobPostLimit;

    private PackageCategory category;
    private PackageType type;

    private String badge;
    private String badgeColor;

    private String imageUrl;

    private boolean showDetails;

    // ✅ THAY MỚI
    private List<BoxType> allowedBoxTypes;



    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public int getJobPostLimit() {
        return jobPostLimit;
    }

    public void setJobPostLimit(int jobPostLimit) {
        this.jobPostLimit = jobPostLimit;
    }

    public PackageCategory getCategory() {
        return category;
    }

    public void setCategory(PackageCategory category) {
        this.category = category;
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public List<BoxType> getAllowedBoxTypes() {
        return allowedBoxTypes;
    }

    public void setAllowedBoxTypes(List<BoxType> allowedBoxTypes) {
        this.allowedBoxTypes = allowedBoxTypes;
    }
}