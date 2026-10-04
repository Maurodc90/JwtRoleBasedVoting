package com.maurodelcore.votecaster.controller;

import com.maurodelcore.votecaster.security.JwtUtil;
import com.maurodelcore.votecaster.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST endpoints for the voting system: login, casting a vote and reading
 * or resetting the results. Access rules (voters vs. admin) are defined in
 * the security configuration.
 */
@RestController
public class VotingController {

    private final VotingService votingService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * @param votingService         holds the vote counts
     * @param authenticationManager verifies usernames and passwords at login
     * @param jwtUtil               creates the token returned after a successful login
     */
    public VotingController(VotingService votingService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.votingService = votingService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @return a welcome message
     */
    @GetMapping("/")
    public String welcome() {
        return "Welcome to the Voting System!";
    }

    /**
     * Returns the current vote count of every candidate. Admin only.
     *
     * @return a map of candidate name to number of votes
     */
    @GetMapping("/results")
    public ResponseEntity<Map<String, Integer>> getResults() {
        return ResponseEntity.ok(votingService.getVotes());
    }

    /**
     * Sets every candidate's vote count back to zero. Admin only.
     *
     * @return a confirmation message
     */
    @PostMapping("results/reset")
    public String resetVotes() {
        votingService.resetVotes();
        return "Vote reset";
    }

    /**
     * Adds one vote for a candidate. Voters only.
     *
     * @param name the candidate's name, sent as the plain-text request body
     * @return 200 with {@code true} if the vote was counted, 404 if the candidate does not exist
     */
    @PostMapping("vote")
    public ResponseEntity<Boolean> castVote(@RequestBody String name) {
        if (!votingService.addVote(name)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Checks the credentials and, if they are valid, issues a JWT.
     * Wrong credentials make {@code authenticate} throw, so no token is returned.
     *
     * @param username the user's name
     * @param password the user's plain-text password, hashed and compared by Spring Security
     * @return the signed JWT
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return ResponseEntity.ok(jwtUtil.generateToken(username));
    }

}