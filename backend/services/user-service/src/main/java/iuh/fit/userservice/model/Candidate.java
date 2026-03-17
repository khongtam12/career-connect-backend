package iuh.fit.userservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "candidates")
public class Candidate{
   @Id
    private String candidateId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate dateOfBirth;
    private String address;
    private int experienceYear;
    private String currentJobTitle;
    private double expectedSalary;
    @OneToMany(mappedBy = "candidate")
    @ToString.Exclude
    @JsonManagedReference
    private List<CV> cvs;

}