package com.maurodelcore.votecaster.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the vote count of each candidate in memory. Counts are lost when the app restarts.
 */
@Service
public class VotingService {

    private final Map<String, Integer> votes = new HashMap<>();

    /**
     * Creates the service with every candidate at zero votes.
     */
    public VotingService() {
        setCandidates();
    }

    /**
     * Adds one vote for a candidate, only if the candidate exists.
     *
     * @param candidate the candidate's name
     * @return true if the vote was counted, false if no such candidate exists
     */
    public boolean addVote(String candidate) {
        if (!votes.containsKey(candidate)) {
            return false;
        }
        votes.put(candidate, votes.get(candidate) + 1);
        return true;
    }

    /**
     * @return the vote count of every candidate, keyed by candidate name
     */
    public Map<String, Integer> getVotes() {
        return votes;
    }

    /**
     * Sets every candidate's count back to zero (used for testing).
     */
    public void resetVotes() {
        for (String candidate : votes.keySet()) {
            votes.put(candidate, 0);
        }
    }

    /**
     * Registers the candidates, each starting with zero votes.
     */
    private void setCandidates() {
        votes.put("Gultekin", 0);
        votes.put("Ozhan", 0);
        votes.put("Ozer", 0);
    }

}