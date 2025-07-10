package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.model.entity.Team;
import lombok.Data;

@Data
public class StandingResponse {
    private Team team;
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    public StandingResponse(Team team) {
        this.team = team;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void addWin() {
        wins++;
        points += 3;
    }

    public void addDraw() {
        draws++;
        points += 1;
    }

    public void addLoss() {
        losses++;
    }

    public void addGoalsFor(int g) {
        goalsFor += g;
    }

    public void addGoalsAgainst(int g) {
        goalsAgainst += g;
    }
}

