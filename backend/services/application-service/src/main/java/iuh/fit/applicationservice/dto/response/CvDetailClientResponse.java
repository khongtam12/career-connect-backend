package iuh.fit.applicationservice.dto.response;

import java.util.List;

public class CvDetailClientResponse {
    private String id;
    private String fullName;
    private String jobTitle;
    private String summary;
    private String fileUrl;
    private List<SkillInfo> skills;
    private List<ExperienceInfo> experiences;
    private List<EducationInfo> educations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public List<SkillInfo> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillInfo> skills) {
        this.skills = skills;
    }

    public List<ExperienceInfo> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceInfo> experiences) {
        this.experiences = experiences;
    }

    public List<EducationInfo> getEducations() {
        return educations;
    }

    public void setEducations(List<EducationInfo> educations) {
        this.educations = educations;
    }

    public static class SkillInfo {
        private String name;
        private String level;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }

    public static class ExperienceInfo {
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class EducationInfo {
        private String school;
        private String major;
        private String startDate;
        private String endDate;
        private String description;

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
