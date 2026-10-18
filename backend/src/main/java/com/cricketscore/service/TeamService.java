package com.cricketscore.service;

import com.cricketscore.dto.request.SquadMembershipRequest;
import com.cricketscore.dto.request.TeamCreateRequest;
import com.cricketscore.dto.response.PlayerResponse;
import com.cricketscore.dto.response.SquadMembershipResponse;
import com.cricketscore.dto.response.TeamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamService {
    TeamResponse createTeam(TeamCreateRequest request);
    Page<TeamResponse> getAllTeams(String query, Pageable pageable);
    TeamResponse getTeamById(Long id);
    TeamResponse updateTeam(Long id, TeamCreateRequest request);
    void deleteTeam(Long id);

    SquadMembershipResponse addPlayerToSquad(Long teamId, SquadMembershipRequest request);
    List<SquadMembershipResponse> getTeamSquad(Long teamId);
    void removePlayerFromSquad(Long teamId, Long playerId);
}
