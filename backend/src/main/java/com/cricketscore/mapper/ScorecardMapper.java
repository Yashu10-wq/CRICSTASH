package com.cricketscore.mapper;

import com.cricketscore.dto.response.BallEventResponse;
import com.cricketscore.dto.response.BattingScoreResponse;
import com.cricketscore.dto.response.BowlingFigureResponse;
import com.cricketscore.entity.BallEvent;
import com.cricketscore.entity.BattingScore;
import com.cricketscore.entity.BowlingFigure;
import org.springframework.stereotype.Component;

@Component
public class ScorecardMapper {

    public BattingScoreResponse toBattingResponse(BattingScore bs) {
        if (bs == null) return null;
        BattingScoreResponse response = new BattingScoreResponse();
        response.setId(bs.getId());
        response.setPlayerId(bs.getPlayer() != null ? bs.getPlayer().getId() : null);
        response.setPlayerName(bs.getPlayer() != null ? bs.getPlayer().getFullName() : null);
        response.setRuns(bs.getRuns());
        response.setBallsFaced(bs.getBallsFaced());
        response.setFours(bs.getFours());
        response.setSixes(bs.getSixes());
        response.setDismissalType(bs.getDismissalType());
        response.setDismissedByBowlerName(bs.getDismissedByBowler() != null ? bs.getDismissedByBowler().getFullName() : null);
        response.setAssistedByPlayerName(bs.getAssistedByPlayer() != null ? bs.getAssistedByPlayer().getFullName() : null);
        response.setBattingPosition(bs.getBattingPosition());
        response.setOnStrike(bs.isOnStrike());
        response.setStrikeRate(bs.getStrikeRate());
        return response;
    }

    public BowlingFigureResponse toBowlingResponse(BowlingFigure bf) {
        if (bf == null) return null;
        BowlingFigureResponse response = new BowlingFigureResponse();
        response.setId(bf.getId());
        response.setPlayerId(bf.getPlayer() != null ? bf.getPlayer().getId() : null);
        response.setPlayerName(bf.getPlayer() != null ? bf.getPlayer().getFullName() : null);
        response.setLegalBallsBowled(bf.getLegalBallsBowled());
        response.setOvers(bf.getOversFormatted());
        response.setRunsConceded(bf.getRunsConceded());
        response.setWickets(bf.getWickets());
        response.setMaidens(bf.getMaidens());
        response.setWides(bf.getWides());
        response.setNoBalls(bf.getNoBalls());
        response.setDots(bf.getDots());
        response.setEconomyRate(bf.getEconomyRate());
        return response;
    }

    public BallEventResponse toBallEventResponse(BallEvent be) {
        if (be == null) return null;
        BallEventResponse response = new BallEventResponse();
        response.setId(be.getId());
        response.setInningsId(be.getInnings() != null ? be.getInnings().getId() : null);
        response.setOverNumber(be.getOverNumber());
        response.setDeliveryIndex(be.getDeliveryIndex());
        response.setEventSequence(be.getEventSequence());
        response.setOverDeliveryString(be.getOverNumber() + "." + be.getDeliveryIndex());
        response.setStrikerId(be.getStriker() != null ? be.getStriker().getId() : null);
        response.setStrikerName(be.getStriker() != null ? be.getStriker().getFullName() : null);
        response.setNonStrikerId(be.getNonStriker() != null ? be.getNonStriker().getId() : null);
        response.setNonStrikerName(be.getNonStriker() != null ? be.getNonStriker().getFullName() : null);
        response.setBowlerId(be.getBowler() != null ? be.getBowler().getId() : null);
        response.setBowlerName(be.getBowler() != null ? be.getBowler().getFullName() : null);
        response.setBatterRuns(be.getBatterRuns());
        response.setExtraRuns(be.getExtraRuns());
        response.setTotalRuns(be.getTotalRuns());
        response.setExtraType(be.getExtraType());
        response.setLegalDelivery(be.isLegalDelivery());
        response.setWicketType(be.getWicketType());
        response.setDismissedPlayerId(be.getDismissedPlayer() != null ? be.getDismissedPlayer().getId() : null);
        response.setDismissedPlayerName(be.getDismissedPlayer() != null ? be.getDismissedPlayer().getFullName() : null);
        response.setFielderId(be.getFielder() != null ? be.getFielder().getId() : null);
        response.setFielderName(be.getFielder() != null ? be.getFielder().getFullName() : null);
        response.setCommentary(be.getCommentary());
        response.setUndone(be.isUndone());
        response.setRecordedAt(be.getRecordedAt());
        return response;
    }
}
