package iuh.fit.applicationservice.dto.response;

public class JobDetailClientResponse {
    private String jobId;
    private String title;
    private IndustryInfo industryDTO;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public IndustryInfo getIndustryDTO() {
        return industryDTO;
    }

    public void setIndustryDTO(IndustryInfo industryDTO) {
        this.industryDTO = industryDTO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class IndustryInfo {
        private String industryId;
        private String name;

        public String getIndustryId() {
            return industryId;
        }

        public void setIndustryId(String industryId) {
            this.industryId = industryId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
