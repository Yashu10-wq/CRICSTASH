-- ====================================================================
-- V1__initial_schema.sql
-- Enterprise Cricket Score Management System - Core Database Schema
-- ====================================================================

-- 1. Teams Table
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    short_name VARCHAR(10) NOT NULL,
    country VARCHAR(100),
    logo_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_teams_name ON teams(name);

-- 2. Players Table
CREATE TABLE IF NOT EXISTS players (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    nationality VARCHAR(100),
    batting_style VARCHAR(50),
    bowling_style VARCHAR(50),
    primary_role VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_players_full_name ON players(full_name);
CREATE INDEX IF NOT EXISTS idx_players_role ON players(primary_role);

-- 3. Team Squad / Squad Membership
CREATE TABLE IF NOT EXISTS squad_memberships (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    jersey_number INT,
    joined_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_team_player UNIQUE (team_id, player_id)
);

CREATE INDEX IF NOT EXISTS idx_squad_team_id ON squad_memberships(team_id);
CREATE INDEX IF NOT EXISTS idx_squad_player_id ON squad_memberships(player_id);

-- 4. Matches Table
CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    format VARCHAR(50) NOT NULL,
    max_overs_per_innings INT,
    competition VARCHAR(100),
    venue VARCHAR(150),
    start_time TIMESTAMPTZ NOT NULL,
    home_team_id BIGINT NOT NULL REFERENCES teams(id),
    away_team_id BIGINT NOT NULL REFERENCES teams(id),
    toss_winner_team_id BIGINT REFERENCES teams(id),
    toss_decision VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    current_innings_number INT DEFAULT 1,
    winner_team_id BIGINT REFERENCES teams(id),
    result_type VARCHAR(50),
    result_summary VARCHAR(255),
    player_of_the_match_id BIGINT REFERENCES players(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_matches_status ON matches(status);
CREATE INDEX IF NOT EXISTS idx_matches_start_time ON matches(start_time);
CREATE INDEX IF NOT EXISTS idx_matches_teams ON matches(home_team_id, away_team_id);

-- 5. Match Squads (Playing XI & Reserves)
CREATE TABLE IF NOT EXISTS match_squads (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    is_playing_xi BOOLEAN NOT NULL DEFAULT TRUE,
    is_captain BOOLEAN NOT NULL DEFAULT FALSE,
    is_wicket_keeper BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_match_player UNIQUE (match_id, player_id)
);

CREATE INDEX IF NOT EXISTS idx_match_squad_match_team ON match_squads(match_id, team_id);

-- 6. Innings Table
CREATE TABLE IF NOT EXISTS innings (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    innings_number INT NOT NULL,
    batting_team_id BIGINT NOT NULL REFERENCES teams(id),
    bowling_team_id BIGINT NOT NULL REFERENCES teams(id),
    total_runs INT NOT NULL DEFAULT 0,
    wickets INT NOT NULL DEFAULT 0,
    legal_balls_bowled INT NOT NULL DEFAULT 0,
    byes INT NOT NULL DEFAULT 0,
    leg_byes INT NOT NULL DEFAULT 0,
    wides INT NOT NULL DEFAULT 0,
    no_balls INT NOT NULL DEFAULT 0,
    penalty_runs INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    target_runs INT,
    current_striker_id BIGINT REFERENCES players(id),
    current_non_striker_id BIGINT REFERENCES players(id),
    current_bowler_id BIGINT REFERENCES players(id),
    last_bowler_id BIGINT REFERENCES players(id),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_match_innings_number UNIQUE (match_id, innings_number)
);

CREATE INDEX IF NOT EXISTS idx_innings_match_id ON innings(match_id);

-- 7. Batting Scores
CREATE TABLE IF NOT EXISTS batting_scores (
    id BIGSERIAL PRIMARY KEY,
    innings_id BIGINT NOT NULL REFERENCES innings(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id),
    runs INT NOT NULL DEFAULT 0,
    balls_faced INT NOT NULL DEFAULT 0,
    fours INT NOT NULL DEFAULT 0,
    sixes INT NOT NULL DEFAULT 0,
    dismissal_type VARCHAR(50) DEFAULT 'NOT_OUT',
    dismissed_by_bowler_id BIGINT REFERENCES players(id),
    assisted_by_player_id BIGINT REFERENCES players(id),
    batting_position INT NOT NULL,
    is_on_strike BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_innings_batter UNIQUE (innings_id, player_id)
);

CREATE INDEX IF NOT EXISTS idx_batting_innings ON batting_scores(innings_id);

-- 8. Bowling Figures
CREATE TABLE IF NOT EXISTS bowling_figures (
    id BIGSERIAL PRIMARY KEY,
    innings_id BIGINT NOT NULL REFERENCES innings(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id),
    legal_balls_bowled INT NOT NULL DEFAULT 0,
    runs_conceded INT NOT NULL DEFAULT 0,
    wickets INT NOT NULL DEFAULT 0,
    maidens INT NOT NULL DEFAULT 0,
    wides INT NOT NULL DEFAULT 0,
    no_balls INT NOT NULL DEFAULT 0,
    dots INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_innings_bowler UNIQUE (innings_id, player_id)
);

CREATE INDEX IF NOT EXISTS idx_bowling_innings ON bowling_figures(innings_id);

-- 9. Ball Events (Audit trail of every delivery)
CREATE TABLE IF NOT EXISTS ball_events (
    id BIGSERIAL PRIMARY KEY,
    innings_id BIGINT NOT NULL REFERENCES innings(id) ON DELETE CASCADE,
    over_number INT NOT NULL,
    delivery_index INT NOT NULL,
    event_sequence BIGINT NOT NULL,
    striker_id BIGINT NOT NULL REFERENCES players(id),
    non_striker_id BIGINT NOT NULL REFERENCES players(id),
    bowler_id BIGINT NOT NULL REFERENCES players(id),
    batter_runs INT NOT NULL DEFAULT 0,
    extra_runs INT NOT NULL DEFAULT 0,
    total_runs INT NOT NULL DEFAULT 0,
    extra_type VARCHAR(50) NOT NULL DEFAULT 'NONE',
    is_legal_delivery BOOLEAN NOT NULL DEFAULT TRUE,
    wicket_type VARCHAR(50),
    dismissed_player_id BIGINT REFERENCES players(id),
    fielder_id BIGINT REFERENCES players(id),
    commentary VARCHAR(500),
    is_undone BOOLEAN NOT NULL DEFAULT FALSE,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_innings_sequence UNIQUE (innings_id, event_sequence)
);

CREATE INDEX IF NOT EXISTS idx_ball_events_innings_seq ON ball_events(innings_id, event_sequence);
CREATE INDEX IF NOT EXISTS idx_ball_events_over ON ball_events(innings_id, over_number);
