package iuh.fit.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cvs")
public class CV {
    @Id
    private String cvId;

    private String title;

    private String fileUrl;

    private String skills;

    private String education;

    private String experience;

    private boolean isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "candidateId")
    private Candidate candidate;
}
