# Cricket Score Management Backend

An enterprise-grade, real-time cricket score management system backend built with **Spring Boot 3**, **Spring Data JPA**, **PostgreSQL (Neon DB)**, **Flyway**, **Spring Security (JWT)**, and **Spring WebSocket (STOMP)**.

---

## 1. Architecture Overview

The system follows a strict layered architecture with clean separation of concerns:

```
com.cricketscore
├── config/                  # SecurityConfig, WebSocketConfig, OpenApiConfig, DotenvEnvironmentPostProcessor
├── controller/              # AuthController, TeamController, PlayerController, MatchController, InningsController, StatisticsController
├── dto/
│   ├── request/             # Request payloads with Jakarta Validation
│   └── response/            # Authoritative response contracts & ApiResponse wrapper
├── entity/                  # JPA Entities (Match, Innings, BallEvent, BattingScore, BowlingFigure, Team, Player, etc.)
│   └── enums/               # MatchFormat, MatchStatus, TossDecision, ExtraType, DismissalType, Role, etc.
├── exception/               # GlobalExceptionHandler, InvalidScoringOperationException, ResourceNotFoundException, etc.
├── mapper/                  # Entity to DTO mappers
├── repository/              # Spring Data JPA Repositories with custom aggregation queries
├── scoring/                 # CricketScoringEngine, CricketScoringEngineImpl (domain rules)
├── security/                # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService, SecurityUtils
├── service/                 # Service contracts
│   └── impl/                # Transactional service implementations
└── websocket/               # WebSocketEventPublisher, ScoreUpdateMessage, WebSocketEventType
```

---

## 2. Technology Stack

- **Java**: Java 21+ (built and validated on Java 26.0.2).
- **Framework**: Spring Boot 3.3.5.
- **Persistence**: Spring Data JPA & Hibernate 6.
- **Database**: PostgreSQL (Neon Cloud Serverless Postgres).
- **Database Migrations**: Flyway 10 (PostgreSQL engine).
- **Security**: Spring Security 6 with stateless JWT authentication (`jjwt-0.12.6`) and BCrypt password hashing.
- **Real-Time Communication**: Spring WebSocket (`/ws`) with STOMP message broker (`/topic`).
- **Observability**: Spring Boot Actuator (`/actuator/health`).
- **API Documentation**: Springdoc OpenAPI 2.6.0 (Swagger UI at `/swagger-ui/index.html`).
- **Build & Tests**: Maven Wrapper (`mvnw.cmd` / `mvnw`), JUnit 5, Mockito, Spring Boot Test, H2 (for isolated test profile).

---

## 3. Database Schema & Flyway Migrations

The database is version-controlled via Flyway migrations located in `src/main/resources/db/migration`:

- **`V1__initial_schema.sql`**:
  - `teams`: Team profile, short codes, country, logo URL.
  - `players`: Full name, date of birth, nationality, batting/bowling style, primary role.
  - `squad_memberships`: Team roster and squad memberships.
  - `matches`: Match title, format (T20, ODI, TEST, CUSTOM), max overs, venue, toss winner & decision, status, winner, result.
  - `match_squads`: Playing XI, substitutes, captain, and wicketkeeper flags.
  - `innings`: Innings number, batting/bowling team, runs, wickets, legal balls, target, status, optimistic locking `@Version`.
  - `batting_scores`: Runs, balls faced, fours, sixes, dismissal type, bowler credit, fielder credit, on-strike/at-crease status.
  - `bowling_figures`: Legal balls, overs bowled, maidens, runs conceded, wickets, wides, no-balls, dot balls.
  - `ball_events`: Over number, delivery index, striker, non-striker, bowler, runs, extras, extra type, dismissal, legal flag.
  - Indexes & Unique Constraints: `uk_ball_events_innings_over_delivery` prevents duplicate ball entries.
- **`V2__security_schema.sql`**:
  - `users`: Username, email, BCrypt password, system role (`ROLE_ADMIN`, `ROLE_SCORER`, `ROLE_VIEWER`), enabled flag.

---

## 4. Configuration & Environment Variables

Credentials are managed securely through environment variables and a local `.env` file (excluded from Git).

Create a `.env` file in the `backend/` directory based on `.env.example`:

```properties
# Neon DB Connection URI (from Neon Console)
DB_URI=postgresql://<user>:<password>@<neon-hostname>/<database>?sslmode=require

# Or individual Spring Datasource properties:
# SPRING_DATASOURCE_URL=jdbc:postgresql://<neon-hostname>:5432/<database>?sslmode=require
# SPRING_DATASOURCE_USERNAME=<user>
# SPRING_DATASOURCE_PASSWORD=<password>

# Server Port
PORT=8080

# JWT Security
JWT_SECRET=CricketScoreSuperSecretKeyWithMinimum256BitsLengthForHmacSha256Security123456
JWT_EXPIRATION_MS=86400000

# CORS Allowed Origins
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

> **Security Note:** Secrets and full database connection strings are never logged or committed to source control.

---

## 5. Build and Execution Instructions (PowerShell)

### Build and Package (including test suite)
```powershell
.\mvnw.cmd clean package
```

### Run Unit and Integration Tests Only
```powershell
.\mvnw.cmd test
```

### Run Application Locally
```powershell
# Using Maven Spring Boot plugin:
.\mvnw.cmd spring-boot:run

# Or running the packaged fat JAR:
java -jar target/cricket-score-backend-1.0.0-SNAPSHOT.jar
```

---

## 6. Authentication & Roles

The system uses stateless JWT bearer authentication:

| Role | Permissions |
|---|---|
| **ROLE_ADMIN** | Full administrative rights: create/manage matches, squads, toss, teams, players, scoring, undo/corrections. |
| **ROLE_SCORER** | Match scoring operations: record delivery, undo delivery, delivery correction, innings initialization. |
| **ROLE_VIEWER** / **Anonymous** | Read-only access: view matches, live scorecards, historical summaries, teams, player statistics, and WebSocket subscriptions. |

### Registering / Logging In

**POST `/api/v1/auth/register`**
```json
{
  "username": "scorer1",
  "email": "scorer1@example.com",
  "password": "Password123!",
  "role": "SCORER"
}
```

**POST `/api/v1/auth/login`**
```json
{
  "username": "scorer1",
  "password": "Password123!"
}
```
**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "username": "scorer1",
    "role": "ROLE_SCORER",
    "expiresInMs": 86400000
  }
}
```

Include the token in write requests:
`Authorization: Bearer <token>`

---

## 7. REST API Inventory

### Teams
- `GET /api/v1/teams` - List teams with pagination (`page`, `size`, `search`).
- `GET /api/v1/teams/{id}` - Get team details.
- `POST /api/v1/teams` - Create team (`ADMIN`).
- `PUT /api/v1/teams/{id}` - Update team (`ADMIN`).
- `DELETE /api/v1/teams/{id}` - Delete team (`ADMIN`).
- `POST /api/v1/teams/{teamId}/players` - Add player to team roster (`ADMIN`).
- `GET /api/v1/teams/{teamId}/players` - Get squad roster for a team.
- `DELETE /api/v1/teams/{teamId}/players/{playerId}` - Remove player from team roster (`ADMIN`).

### Players
- `GET /api/v1/players` - List players with pagination (`page`, `size`, `query`, `role`).
- `GET /api/v1/players/{id}` - Get player by ID.
- `POST /api/v1/players` - Create player (`ADMIN`).
- `PUT /api/v1/players/{id}` - Update player (`ADMIN`).
- `DELETE /api/v1/players/{id}` - Delete player (`ADMIN`).

### Matches
- `POST /api/v1/matches` - Schedule a match (`ADMIN`).
- `GET /api/v1/matches` - List matches with status filtering (`page`, `size`, `status`).
- `GET /api/v1/matches/{id}` - Get match details.
- `PUT /api/v1/matches/{id}` - Update match details (`ADMIN`).
- `POST /api/v1/matches/{id}/cancel` - Cancel a match (`ADMIN`).
- `POST /api/v1/matches/{id}/squads` - Configure match squads (Playing XI, captain, wicketkeeper) (`ADMIN`).
- `POST /api/v1/matches/{id}/toss` - Record toss winner and decision (`ADMIN`).
- `POST /api/v1/matches/{id}/start` - Validate lineups, toss, and start match (`ADMIN`).

### Innings & Scoring (High Priority Engine)
- `POST /api/v1/matches/{id}/innings` - Initialize match innings (`ADMIN`, `SCORER`).
- `POST /api/v1/innings/{inningsId}/deliveries` - Record delivery ball-by-ball (`ADMIN`, `SCORER`).
- `GET /api/v1/innings/{inningsId}` - Get innings state.
- `GET /api/v1/innings/{inningsId}/deliveries` - Get ball event stream.
- `GET /api/v1/innings/{inningsId}/scorecard` - Authoritative live scorecard.
- `GET /api/v1/matches/{id}/scorecard` - Scorecard for all innings in match.
- `POST /api/v1/innings/{inningsId}/undo-last-delivery` - Roll back last ball with scorecard recalculation (`ADMIN`, `SCORER`).
- `PUT /api/v1/innings/{inningsId}/deliveries/{deliverySequence}/correct` - Auditable delivery correction with full state recalculation (`ADMIN`, `SCORER`).

### Statistics & Summaries
- `GET /api/v1/players/{id}/statistics` - Player career batting & bowling figures (averages, strike rates, economy).
- `GET /api/v1/teams/{id}/statistics` - Team record (wins, losses, win percentage).
- `GET /api/v1/matches/{id}/summary` - Complete match summary and result breakdown.

### Operations & Documentation
- `GET /actuator/health` - Health check.
- `GET /swagger-ui/index.html` - Interactive Swagger UI documentation.
- `GET /v3/api-docs` - OpenAPI 3 specification JSON.

---

## 8. Cricket Scoring Engine Rules

The engine (`CricketScoringEngineImpl`) implements realistic cricket scoring rules:

1. **Batter Runs & Boundaries**:
   - Scores `0`, `1`, `2`, `3`, `4` (boundary), and `6` (six).
   - Fours and sixes increment batter statistics and total runs.
   - Odd runs rotate strike immediately.
2. **Extras**:
   - **Wide (`WIDE`)**: Concedes 1 penalty run + any additional runs scored. Does not count as a legal ball. Debited to bowler. Strike only changes if additional completed runs are odd.
   - **No Ball (`NO_BALL`)**: Concedes 1 penalty run + batter runs or extras. Does not count as a legal ball. Debited to bowler. Batter gets credit for runs scored off the bat.
   - **Bye (`BYE`)**: Batter runs not off the bat; credited to team extras, not debited to bowler figures. Strike rotates on odd runs. Legal delivery.
   - **Leg Bye (`LEG_BYE`)**: Credited to team extras, not debited to bowler. Strike rotates on odd runs. Legal delivery.
3. **Dismissals & Bowler Credit**:
   - Bowler receives credit for: `BOWLED`, `CAUGHT`, `LBW`, `STUMPED`, `HIT_WICKET`.
   - Bowler does **NOT** receive credit for: `RUN_OUT`, `OBSTRUCTING_FIELD`, `RETIRED_OUT`, `TIMED_OUT`.
   - Fall of wickets tracked sequentially.
   - Batter dismissal records bowler and assisting fielder.
4. **Over Completion**:
   - Completed when legal balls reach `6` (illegal balls like wides/no-balls do not advance the ball count).
   - Maiden over calculation: an over with 6 legal balls and 0 runs conceded by bowler increments maidens.
   - Automatic strike rotation at over completion.
   - Bowler cannot bowl consecutive overs (enforced at service level).
5. **Innings Completion & Chases**:
   - Innings ends when `10` wickets fall (all-out) or configured maximum overs are reached.
   - In 2nd innings (chase): match automatically completes when batting team surpasses the target (`runs >= targetRuns`). Result is set to `WIN_BY_WICKETS`.
   - If overs expire or wickets exhausted before reaching target: result is set to `WIN_BY_RUNS`.
   - If scores are identical at innings end: result is set to `TIE`.
6. **Delivery Correction & Undo**:
   - `/undo-last-delivery`: Atomically reverses the last ball event and reconstructs batting and bowling figures.
   - `/correct`: Explicitly modifies a historical ball event and performs complete recalculation across all derived batting, bowling, and innings aggregates.

---

## 9. Real-Time WebSockets

- **Connection Endpoint**: `ws://<host>:8080/ws` (SockJS fallback available).
- **Topic Subscription**: `/topic/match/{matchId}`.
- **Event Types Broadcast**:
  - `DELIVERY_SCORED`: Sent on every accepted ball event.
  - `WICKET_FALL`: Sent when a wicket occurs.
  - `OVER_COMPLETED`: Sent at the end of each over.
  - `INNINGS_COMPLETED`: Sent on all-out, overs complete, or successful chase.
  - `MATCH_COMPLETED`: Sent with final match result summary.
  - `DELIVERY_CORRECTED`: Sent when an undo or delivery correction is performed.
- **Payload Guarantee**: WebSocket messages are dispatched only after the underlying database transaction commits using `TransactionSynchronizationManager.afterCommit()`.

### Sample Payload
```json
{
  "eventType": "DELIVERY_SCORED",
  "matchId": 1,
  "inningsId": 1,
  "inningsNumber": 1,
  "totalRuns": 14,
  "wickets": 0,
  "overs": "1.4",
  "legalBalls": 10,
  "currentRunRate": 8.4,
  "targetRuns": null,
  "requiredRunRate": null,
  "strikerName": "Virat Kohli",
  "nonStrikerName": "Rohit Sharma",
  "bowlerName": "Pat Cummins",
  "lastBallDescription": "Pat Cummins to Virat Kohli, 4 runs (FOUR)",
  "matchStatus": "LIVE",
  "timestamp": "2026-10-17T07:40:37.470Z"
}
```

---

## 10. Automated Test Results

The test suite consists of **33 automated tests** across scoring, match workflow, authentication, security access, and delivery simulation.

### Test Execution Command
```powershell
.\mvnw.cmd test
```

### Verified Test Summary
- `com.cricketscore.scoring.CricketScoringEngineTest` (12 tests):
  - Dot balls, singles, boundaries (4s & 6s).
  - Strike rotation on odd runs.
  - Strike change at end of over.
  - Wide & No-Ball penalty and non-consumption of legal balls.
  - Byes & Leg-Byes.
  - Bowled & Caught wicket bowler credits.
  - Run-out bowler credit exclusion.
  - Maiden over calculation.
  - Innings completion (all out and max overs).
  - Target calculation and successful chase.
  - Delivery rollback (undo).
  - Delivery correction and historical scorecard recalculation.
- `com.cricketscore.scoring.ScoringIntegrationTest` (1 test): Full multi-over match simulation with fall of wickets and over transitions.
- `com.cricketscore.service.MatchServiceTest` (4 tests): Scheduling, lineup validation, toss decisions, state transitions.
- `com.cricketscore.service.PlayerServiceTest` (4 tests): Player creation, lookups, pagination, not-found handling.
- `com.cricketscore.service.StatisticsServiceTest` (3 tests): Career statistics, batting/bowling calculations, match summaries.
- `com.cricketscore.controller.AuthControllerIntegrationTest` (2 tests): User registration, duplicate user rejection, login and JWT issuance.
- `com.cricketscore.controller.TeamControllerIntegrationTest` (3 tests): Team listing, admin creation, unauthenticated access rejection.
- `com.cricketscore.security.SecurityAccessIntegrationTest` (4 tests): Public read access, 401 Unauthorized rejection, 403 Forbidden role rejection, Admin 201 Created acceptance.

**Results:** `Tests run: 33, Failures: 0, Errors: 0, Skipped: 0` (100% Passing).
