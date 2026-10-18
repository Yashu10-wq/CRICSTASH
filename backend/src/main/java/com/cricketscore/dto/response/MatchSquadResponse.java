package com.cricketscore.dto.response;

public class MatchSquadResponse {
    private Long id;
    private Long matchId;
    private Long teamId;
    private String teamName;
    private PlayerResponse player;
    private boolean playingXi;
    private boolean captain;
    private boolean wicketKeeper;

    public MatchSquadResponse() {
    }

    public MatchSquadResponse(Long id, Long matchId, Long teamId, String teamName,
                              PlayerResponse player, boolean playingXi, boolean captain, boolean wicketKeeper) {
        this.id = id;
        this.matchId = matchId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.player = player;
        this.playingXi = playingXi;
        this.captain = captain;
        this.wicketKeeper = wicketKeeper;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public PlayerResponse getPlayer() {
        return player;
    }

    public void setPlayer(PlayerResponse player) {
        this.player = player;
    }

    public boolean isPlayingXi() {
        return playingXi;
    }

    public void setPlayingXi(boolean playingXi) {
        this.playingXi = playingXi;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    public boolean isWicketKeeper() {
        return wicketKeeper;
    }

    public void setWicketKeeper(boolean wicketKeeper) {
        this.wicketKeeper = wicketKeeper;
    }
}
