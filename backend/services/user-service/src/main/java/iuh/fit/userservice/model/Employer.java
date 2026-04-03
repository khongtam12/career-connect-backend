package iuh.fit.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "employers")
public class Employer {

    @Id
    private String employerId;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String avatar;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    @Enumerated(EnumType.STRING)
    private Status status;

    private String position;

    private String companyId;
}