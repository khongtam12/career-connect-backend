package iuh.fit.jobservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    private String jobId;

    private String companyId;

    private String title;

    private String description;

    private String requirement;

    private String benefit;

    private double salaryMin;

    private double salaryMax;

    private String location;

    private int experienceRequired;

    private LocalDate deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int numberOfApplications;

    @Enumerated(EnumType.STRING)
    private StatusJob status;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private String industryId;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Favourite> favourites;
}