package iuh.fit.companyservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
public class Company {
    @Id
    private String companyId;

    private String name;

    private String logo;

    private String website;

    private String email;

    private String phone;

    private String address;

    private String description;

    private int companySize;

    private int foundedYear;
    @Enumerated(EnumType.STRING)
    private StatusCompany statusCompany;
    private LocalDateTime createdAt;
}