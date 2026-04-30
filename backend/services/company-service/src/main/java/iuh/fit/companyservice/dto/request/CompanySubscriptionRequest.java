package iuh.fit.companyservice.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CompanySubscriptionRequest {
    private String companyId;
    private String packageId;
    private int jobPostLimit;
    private int durationDays;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public int getJobPostLimit() {
        return jobPostLimit;
    }

    public void setJobPostLimit(int jobPostLimit) {
        this.jobPostLimit = jobPostLimit;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }
}
