package com.cricketscore.service.impl;

import com.cricketscore.dto.request.SquadMembershipRequest;
import com.cricketscore.dto.request.TeamCreateRequest;
import com.cricketscore.dto.response.SquadMembershipResponse;
import com.cricketscore.dto.response.TeamResponse;
import com.cricketscore.entity.Player;
import com.cricketscore.entity.SquadMembership;
import com.cricketscore.entity.Team;
import com.cricketscore.exception.DuplicateResourceException;
import com.cricketscore.exception.ResourceNotFoundException;
import com.cricketscore.mapper.TeamMapper;
import com.cricketscore.repository.PlayerRepository;
import com.cricketscore.repository.SquadMembershipRepository;
import com.cricketscore.repository.TeamRepository;
import com.cricketscore.service.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final SquadMembershipRepository squadMembershipRepository;
    private final TeamMapper teamMapper;

    public TeamServiceImpl(TeamRepository teamRepository,
                           PlayerRepository playerRepository,
                           SquadMembershipRepository squadMembershipRepository,
                           TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.squadMembershipRepository = squadMembershipRepository;
        this.teamMapper = teamMapper;
    }

    @Override
    @Transactional
    public TeamResponse createTeam(TeamCreateRequest request) {
        if (teamRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Team with name '" + request.getName() + "' already exists.");
        }
        Team team = new Team(request.getName(), request.getShortName(), request.getCountry(), request.getLogoUrl());
        team = teamRepository.save(team);
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamResponse> getAllTeams(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return teamRepository.findByNameContainingIgnoreCaseOrCountryContainingIgnoreCase(query, query, pageable)
                    .map(teamMapper::toResponse);
        }
        return teamRepository.findAll(pageable).map(teamMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(Long id, TeamCreateRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        if (!team.getName().equalsIgnoreCase(request.getName()) && teamRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Team with name '" + request.getName() + "' already exists.");
        }

        team.setName(request.getName());
        team.setShortName(request.getShortName());
        team.setCountry(request.getCountry());
        team.setLogoUrl(request.getLogoUrl());

        team = teamRepository.save(team);
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with id: " + id);
        }
        teamRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SquadMembershipResponse addPlayerToSquad(Long teamId, SquadMembershipRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + request.getPlayerId()));

        if (squadMembershipRepository.existsByTeamIdAndPlayerId(teamId, request.getPlayerId())) {
            throw new DuplicateResourceException("Player already belongs to team squad.");
        }

        SquadMembership membership = new SquadMembership(team, player, request.getJerseyNumber(), request.getJoinedDate());
        membership = squadMembershipRepository.save(membership);

        return new SquadMembershipResponse(
                membership.getId(),
                team.getId(),
                team.getName(),
                player.getId(),
                player.getFullName(),
                membership.getJerseyNumber(),
                membership.getJoinedDate(),
                membership.isActive()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SquadMembershipResponse> getTeamSquad(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with id: " + teamId);
        }
        return squadMembershipRepository.findActiveWithPlayerDetails(teamId).stream()
                .map(m -> new SquadMembershipResponse(
                        m.getId(),
                        m.getTeam().getId(),
                        m.getTeam().getName(),
                        m.getPlayer().getId(),
                        m.getPlayer().getFullName(),
                        m.getJerseyNumber(),
                        m.getJoinedDate(),
                        m.isActive()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void removePlayerFromSquad(Long teamId, Long playerId) {
        SquadMembership membership = squadMembershipRepository.findByTeamIdAndPlayerId(teamId, playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player is not in squad of team id: " + teamId));
        squadMembershipRepository.delete(membership);
    }
}
