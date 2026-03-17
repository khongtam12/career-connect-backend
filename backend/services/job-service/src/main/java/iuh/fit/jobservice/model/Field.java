package iuh.fit.jobservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fields")
public class Field {

    @Id
    private String fieldId;
    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    private String name;

    private String description;
}
