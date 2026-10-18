package com.cricketscore.dto.response;

import java.util.List;

public class MatchSummaryResponse {
    private MatchResponse match;
    private List<ScorecardResponse> inningsScorecards;
    private String matchResultSummary;

    public MatchSummaryResponse() {
    }

    public MatchSummaryResponse(MatchResponse match, List<ScorecardResponse> inningsScorecards, String matchResultSummary) {
        this.match = match;
        this.inningsScorecards = inningsScorecards;
        this.matchResultSummary = matchResultSummary;
    }

    public MatchResponse getMatch() {
        return match;
    }

    public void setMatch(MatchResponse match) {
        this.match = match;
    }

    public List<ScorecardResponse> getInningsScorecards() {
        return inningsScorecards;
    }

    public void setInningsScorecards(List<ScorecardResponse> inningsScorecards) {
        this.inningsScorecards = inningsScorecards;
    }

    public String getMatchResultSummary() {
        return matchResultSummary;
    }

    public void setMatchResultSummary(String matchResultSummary) {
        this.matchResultSummary = matchResultSummary;
    }
}
