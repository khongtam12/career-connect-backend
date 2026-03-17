package iuh.fit.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "employers")
public class Employer {

    @Id
    private String userId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String position;

    private String companyId;
}