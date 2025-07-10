package com.bettherapy.bettherapy.util;

public enum BetResult {
    HOME_WIN,     // Home team wins the match in regulation time
    AWAY_WIN,     // Away team wins the match in regulation time
    DRAW,         // Match ends in a draw after regulation time

    HOME_WIN_EXTRA_TIME, // Home team wins in extra time (after 90 mins + ET)
    AWAY_WIN_EXTRA_TIME, // Away team wins in extra time

    HOME_WIN_PENALTIES,  // Home team wins after penalty shootout
    AWAY_WIN_PENALTIES,  // Away team wins after penalty shootout

    OVER_2_5,     // Total goals in the match are over 2.5
    UNDER_2_5,    // Total goals in the match are under 2.5

    BOTH_TEAMS_TO_SCORE_YES, // Both teams scored at least one goal
    BOTH_TEAMS_TO_SCORE_NO,  // At least one team did not score

    DOUBLE_CHANCE_HOME_DRAW, // Home win or draw (1X)
    DOUBLE_CHANCE_AWAY_DRAW, // Away win or draw (X2)
    DOUBLE_CHANCE_HOME_AWAY, // Home or away win (12), no draw

    FIRST_HALF_HOME_WIN,  // Home team was leading at half-time
    FIRST_HALF_AWAY_WIN,  // Away team was leading at half-time
    FIRST_HALF_DRAW       // Score was tied at half-time
}
