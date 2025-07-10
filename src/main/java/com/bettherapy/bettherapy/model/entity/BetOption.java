package com.bettherapy.bettherapy.model.entity;


import com.bettherapy.bettherapy.util.BetResult;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bet_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BetResult result;

    private Double odds;

    private String description;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;
}
