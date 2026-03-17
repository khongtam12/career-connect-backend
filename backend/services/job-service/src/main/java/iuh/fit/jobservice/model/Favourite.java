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
@IdClass(Favourite.FavouriId.class)
public class Favourite {
    private LocalDateTime createdAt;
  @Id
    private String candidateId;
  @Id
    @ManyToOne
    @JoinColumn(name = "jobId")
    @ToString.Exclude
    private Job job;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavouriId implements Serializable{
        private String candidateId;
        private String job;
    }

}
