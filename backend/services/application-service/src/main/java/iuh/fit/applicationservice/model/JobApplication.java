package iuh.fit.applicationservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    private String id;
    private String jobId;

    private String candidateId;

    private String cvId;

    private String note;

    @Enumerated(EnumType.STRING)
    private StatusApply status;

    private LocalDateTime appliedAt;

    private LocalDateTime updatedAt;
}