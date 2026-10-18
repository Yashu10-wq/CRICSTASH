package com.cricketscore.dto.response;

public class PlayerStatisticsResponse {
    private Long playerId;
    private String playerName;

    // Batting stats
    private long matchesPlayed;
    private long inningsBatted;
    private long totalRuns;
    private long ballsFaced;
    private int highestScore;
    private double battingAverage;
    private double battingStrikeRate;
    private long fours;
    private long sixes;

    // Bowling stats
    private long inningsBowled;
    private long legalBallsBowled;
    private String oversBowled;
    private long runsConceded;
    private long wicketsTaken;
    private long maidens;
    private double bowlingEconomy;
    private double bowlingAverage;
    private double bowlingStrikeRate;

    public PlayerStatisticsResponse() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(long matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public long getInningsBatted() {
        return inningsBatted;
    }

    public void setInningsBatted(long inningsBatted) {
        this.inningsBatted = inningsBatted;
    }

    public long getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(long totalRuns) {
        this.totalRuns = totalRuns;
    }

    public long getBallsFaced() {
        return ballsFaced;
    }

    public void setBallsFaced(long ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public double getBattingAverage() {
        return battingAverage;
    }

    public void setBattingAverage(double battingAverage) {
        this.battingAverage = battingAverage;
    }

    public double getBattingStrikeRate() {
        return battingStrikeRate;
    }

    public void setBattingStrikeRate(double battingStrikeRate) {
        this.battingStrikeRate = battingStrikeRate;
    }

    public long getFours() {
        return fours;
    }

    public void setFours(long fours) {
        this.fours = fours;
    }

    public long getSixes() {
        return sixes;
    }

    public void setSixes(long sixes) {
        this.sixes = sixes;
    }

    public long getInningsBowled() {
        return inningsBowled;
    }

    public void setInningsBowled(long inningsBowled) {
        this.inningsBowled = inningsBowled;
    }

    public long getLegalBallsBowled() {
        return legalBallsBowled;
    }

    public void setLegalBallsBowled(long legalBallsBowled) {
        this.legalBallsBowled = legalBallsBowled;
    }

    public String getOversBowled() {
        return oversBowled;
    }

    public void setOversBowled(String oversBowled) {
        this.oversBowled = oversBowled;
    }

    public long getRunsConceded() {
        return runsConceded;
    }

    public void setRunsConceded(long runsConceded) {
        this.runsConceded = runsConceded;
    }

    public long getWicketsTaken() {
        return wicketsTaken;
    }

    public void setWicketsTaken(long wicketsTaken) {
        this.wicketsTaken = wicketsTaken;
    }

    public long getMaidens() {
        return maidens;
    }

    public void setMaidens(long maidens) {
        this.maidens = maidens;
    }

    public double getBowlingEconomy() {
        return bowlingEconomy;
    }

    public void setBowlingEconomy(double bowlingEconomy) {
        this.bowlingEconomy = bowlingEconomy;
    }

    public double getBowlingAverage() {
        return bowlingAverage;
    }

    public void setBowlingAverage(double bowlingAverage) {
        this.bowlingAverage = bowlingAverage;
    }

    public double getBowlingStrikeRate() {
        return bowlingStrikeRate;
    }

    public void setBowlingStrikeRate(double bowlingStrikeRate) {
        this.bowlingStrikeRate = bowlingStrikeRate;
    }
}
