package com.cricketscore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class MatchSquadSetupRequest {

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotEmpty(message = "Squad player selections cannot be empty")
    @Valid
    private List<SquadPlayerItem> players;

    public MatchSquadSetupRequest() {
    }

    public MatchSquadSetupRequest(Long teamId, List<SquadPlayerItem> players) {
        this.teamId = teamId;
        this.players = players;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<SquadPlayerItem> getPlayers() {
        return players;
    }

    public void setPlayers(List<SquadPlayerItem> players) {
        this.players = players;
    }

    public static class SquadPlayerItem {
        @NotNull(message = "Player ID is required")
        private Long playerId;

        private boolean playingXi = true;
        private boolean captain = false;
        private boolean wicketKeeper = false;

        public SquadPlayerItem() {
        }

        public SquadPlayerItem(Long playerId, boolean playingXi, boolean captain, boolean wicketKeeper) {
            this.playerId = playerId;
            this.playingXi = playingXi;
            this.captain = captain;
            this.wicketKeeper = wicketKeeper;
        }

        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
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
}
