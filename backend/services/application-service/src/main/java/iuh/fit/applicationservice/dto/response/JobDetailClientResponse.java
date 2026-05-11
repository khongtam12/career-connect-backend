package iuh.fit.applicationservice.dto.response;

public class JobDetailClientResponse {
    private String jobId;
    private String title;
    private String description;
    private String candidateRequirements;
    private String experience;
    private String education;
    private String requirementTags;
    private String skills;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCandidateRequirements() {
        return candidateRequirements;
    }

    public void setCandidateRequirements(String candidateRequirements) {
        this.candidateRequirements = candidateRequirements;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getRequirementTags() {
        return requirementTags;
    }

    public void setRequirementTags(String requirementTags) {
        this.requirementTags = requirementTags;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
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
