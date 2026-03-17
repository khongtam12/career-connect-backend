package iuh.fit.userservice.model;

import iuh.fit.userservice.model.User;
import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    private String userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}