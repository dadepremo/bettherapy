package com.bettherapy.bettherapy.model.entity;


import com.bettherapy.bettherapy.util.BetStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal stake; // amount the user bets

    private BigDecimal odds; // fixed at bet time

    private BigDecimal payout; // calculated only if won

    private BigDecimal potentialPayout; // <--- Add this field

    @Enumerated(EnumType.STRING)
    private BetStatus status = BetStatus.PENDING;

    private LocalDateTime placedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "bet_option_id")
    private BetOption betOption;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;
}
