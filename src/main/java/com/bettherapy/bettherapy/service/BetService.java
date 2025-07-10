package com.bettherapy.bettherapy.service;

import com.bettherapy.bettherapy.model.request.BetRequest;
import com.bettherapy.bettherapy.model.request.PlaceBetRequest;
import com.bettherapy.bettherapy.model.response.BetResponse;
import com.bettherapy.bettherapy.model.entity.Bet;
import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.entity.User;
import com.bettherapy.bettherapy.model.repository.BetOptionRepository;
import com.bettherapy.bettherapy.model.repository.BetRepository;
import com.bettherapy.bettherapy.model.repository.MatchRepository;
import com.bettherapy.bettherapy.model.repository.UserRepository;
import com.bettherapy.bettherapy.model.response.PlaceBetResponse;
import com.bettherapy.bettherapy.util.BetResult;
import com.bettherapy.bettherapy.util.BetStatus;
import com.bettherapy.bettherapy.util.LoggableComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BetService {

    @Autowired
    BetRepository betRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BetOptionRepository betOptionRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    LoggableComponent logger;

    public BetResponse createBet(BetRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        BetOption option = betOptionRepository.findById(request.getBetOptionId()).orElseThrow();
        Match match = matchRepository.findById(request.getMatchId()).orElseThrow();

        Bet bet = Bet.builder()
                .stake(request.getStake())
                .odds(request.getOdds())
                .user(user)
                .betOption(option)
                .match(match)
                .status(BetStatus.PENDING)
                .placedAt(LocalDateTime.now())
                .build();

        logger.info("Bet: " + bet);

        Bet saved = betRepository.save(bet);
        return toDto(saved);
    }

    public List<BetResponse> getAllBets() {
        return betRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public BetResponse getBetById(Long id) {
        return betRepository.findById(id).map(this::toDto).orElseThrow();
    }

    public List<BetResponse> getBetsByUser(Long userId) {
        return betRepository.findByUserIdWithMatchAndOption(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public BetResponse placeBet(BetRequest request) {

        logger.info("Bet request: " + request);
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("No user found for ID: " + request.getUserId()));
        BetOption option = betOptionRepository.findById(request.getBetOptionId()).orElseThrow(() -> new RuntimeException("No bet option found for ID: " + request.getBetOptionId()));
        Match match = matchRepository.findById(request.getMatchId()).orElseThrow(() -> new RuntimeException("No match found for match ID: " + request.getMatchId()));

        if (match.getKickoff().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot place bet — match already started.");
        }

        if (user.getPoints().compareTo(request.getStake()) < 0) {
            throw new IllegalArgumentException("Insufficient balance to place bet.");
        }

        user.setPoints(user.getPoints().subtract(request.getStake()));
        userRepository.save(user);


        BigDecimal payout = request.getStake().multiply(BigDecimal.valueOf(option.getOdds()));

        Bet bet = Bet.builder()
                .user(user)
                .betOption(option)
                .stake(request.getStake())
                .potentialPayout(payout)
                .odds(request.getOdds())
                .status(BetStatus.PENDING)
                .placedAt(LocalDateTime.now())
                .match(match)
                .build();

        Bet saved = betRepository.save(bet);

        BetResponse res = new BetResponse();
        res.setId(saved.getId());
        res.setUserId(user.getId());
        res.setBetOptionId(option.getId());
        res.setStake(saved.getStake());
        res.setPotentialPayout(saved.getPotentialPayout());
        res.setStatus(saved.getStatus());
        res.setOdds(saved.getOdds());
        return res;
    }

    @Transactional
    public void settleBetsForMatch(Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow();
        List<Bet> bets = betRepository.findByMatch_Id(matchId);

        for (Bet bet : bets) {
            boolean won = checkIfBetWon(bet, match);
            if (won) {
                bet.setStatus(BetStatus.WON);
                bet.setPayout(bet.getPotentialPayout());

                User user = bet.getUser();
                user.setPoints(user.getPoints().add(bet.getPotentialPayout()));
                userRepository.save(user);
                //TODO: update point in front end
            } else {
                bet.setStatus(BetStatus.LOST);
                bet.setPayout(BigDecimal.ZERO);
            }
        }

        betRepository.saveAll(bets);
    }

    private boolean checkIfBetWon(Bet bet, Match match) {
        BetResult betResult = bet.getBetOption().getResult();
        Integer homeScore = match.getHomeScore();
        Integer awayScore = match.getAwayScore();

        if (homeScore == null || awayScore == null) {
            return false; // Prevent NullPointerException for incomplete matches
        }

        switch (betResult) {
            case HOME_WIN:
                return homeScore > awayScore;
            case AWAY_WIN:
                return awayScore > homeScore;
            case DRAW:
                return homeScore.equals(awayScore);

            case OVER_2_5:
                return (homeScore + awayScore) > 2;
            case UNDER_2_5:
                return (homeScore + awayScore) < 3;

            case BOTH_TEAMS_TO_SCORE_YES:
                return homeScore > 0 && awayScore > 0;
            case BOTH_TEAMS_TO_SCORE_NO:
                return homeScore == 0 || awayScore == 0;

            case DOUBLE_CHANCE_HOME_DRAW:
                return homeScore >= awayScore;
            case DOUBLE_CHANCE_AWAY_DRAW:
                return awayScore >= homeScore;
            case DOUBLE_CHANCE_HOME_AWAY:
                return !homeScore.equals(awayScore);

            case FIRST_HALF_HOME_WIN:
            case FIRST_HALF_AWAY_WIN:
            case FIRST_HALF_DRAW:
            case HOME_WIN_EXTRA_TIME:
            case AWAY_WIN_EXTRA_TIME:
            case HOME_WIN_PENALTIES:
            case AWAY_WIN_PENALTIES:
                // You need to implement extraTimeScore, penaltyWinner, or firstHalfScore logic for these.
                return false; // Placeholder

            default:
                return false; // Unknown result type
        }
    }


    private BetResponse toDto(Bet bet) {
        BetResponse dto = new BetResponse();
        dto.setId(bet.getId());
        dto.setStake(bet.getStake());
        dto.setOdds(bet.getOdds());
        dto.setPayout(bet.getPayout());
        dto.setStatus(bet.getStatus());
        dto.setPlacedAt(bet.getPlacedAt());
        dto.setUserId(bet.getUser().getId());
        dto.setBetOptionId(bet.getBetOption().getId());
        dto.setMatchId(bet.getMatch().getId());
        dto.setMatch(bet.getMatch());
        dto.setOptionDescription(bet.getBetOption().getDescription());
        dto.setOptionOdds(bet.getBetOption().getOdds());
        dto.setOptionResult(bet.getBetOption().getResult());
        dto.setPotentialPayout(bet.getPotentialPayout());
        return dto;
    }
}

