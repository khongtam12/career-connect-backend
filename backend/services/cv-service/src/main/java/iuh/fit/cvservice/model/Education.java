package iuh.fit.cvservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cv_educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school;
    private String major;
    @Column(name = "start_date")
    private String start; // ISO month (yyyy-MM)
    @Column(name = "end_date")
    private String end;   // ISO month or empty
    @Column(name = "description", columnDefinition = "TEXT")
    private String desc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    @JsonIgnore
    @ToString.Exclude
    private CV cv;
}
