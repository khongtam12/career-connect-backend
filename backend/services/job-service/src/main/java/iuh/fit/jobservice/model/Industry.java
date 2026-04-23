package iuh.fit.jobservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "industrys")
public class Industry {

    @Id
    private String industryId;
    private String name;
    private String description;

    public Industry() {}

    public Industry(String industryId, String name, String description) {
        this.industryId = industryId;
        this.name = name;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}