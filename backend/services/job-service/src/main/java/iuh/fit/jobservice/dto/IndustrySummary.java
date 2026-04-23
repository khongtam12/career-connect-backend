package iuh.fit.jobservice.dto;

public class IndustrySummary {
    private String industryId;
    private String name;

    public IndustrySummary(String industryId, String name) {
        this.industryId = industryId;
        this.name = name;
    }

    public String getIndustryId() {
        return industryId;
    }

    public String getName() {
        return name;
    }
}
