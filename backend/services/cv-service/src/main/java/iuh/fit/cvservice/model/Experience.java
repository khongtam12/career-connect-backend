package iuh.fit.cvservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cv_experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    @Column(name = "start_date")
    private String start; // ISO month string (yyyy-MM)
    @Column(name = "end_date")
    private String end;   // ISO month string or empty
    @Column(name = "description", columnDefinition = "TEXT")
    private String desc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    @JsonIgnore
    @ToString.Exclude
    private CV cv;
}
