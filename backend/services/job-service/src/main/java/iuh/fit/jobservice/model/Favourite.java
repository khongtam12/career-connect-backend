package iuh.fit.jobservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "favourites")
public class Favourite {
    @Id
    private String favouriteId;
    private LocalDateTime createdAt;
    private String candidateId;

    @ManyToOne

    @JoinColumn(name = "jobId")
    @ToString.Exclude
    private Job job;


}
