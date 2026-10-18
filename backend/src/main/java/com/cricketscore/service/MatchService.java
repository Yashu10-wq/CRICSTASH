package com.cricketscore.service;

import com.cricketscore.dto.request.MatchCreateRequest;
import com.cricketscore.dto.request.MatchSquadSetupRequest;
import com.cricketscore.dto.request.StartMatchRequest;
import com.cricketscore.dto.request.TossRequest;
import com.cricketscore.dto.response.MatchResponse;
import com.cricketscore.dto.response.MatchSquadResponse;
import com.cricketscore.entity.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchService {
    MatchResponse createMatch(MatchCreateRequest request);
    Page<MatchResponse> getAllMatches(MatchStatus status, Long teamId, Pageable pageable);
    MatchResponse getMatchById(Long id);
    MatchResponse updateMatch(Long id, MatchCreateRequest request);
    MatchResponse cancelMatch(Long id);

    List<MatchSquadResponse> setupMatchSquad(Long matchId, MatchSquadSetupRequest request);
    List<MatchSquadResponse> getMatchSquad(Long matchId, Long teamId);

    MatchResponse recordToss(Long matchId, TossRequest request);
    MatchResponse startMatch(Long matchId, StartMatchRequest request);
}
