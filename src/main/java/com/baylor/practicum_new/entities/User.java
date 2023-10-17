package com.baylor.practicum_new.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(length = 100)
    private String password;

}
