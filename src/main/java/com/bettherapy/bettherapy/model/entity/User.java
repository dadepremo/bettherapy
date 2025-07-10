package com.bettherapy.bettherapy.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private BigDecimal points = BigDecimal.valueOf(1000);

    private String role = "USER";

    private LocalDateTime createdAt = LocalDateTime.now();
}
