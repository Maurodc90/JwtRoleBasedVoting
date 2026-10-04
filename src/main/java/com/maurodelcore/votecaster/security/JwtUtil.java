package com.maurodelcore.votecaster.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long TOKEN_VALIDITY_MS = 60 * 60 * 1000; // 1 hour

    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    /**
     * Builds a signed token for a user who has just logged in.
     *
     * @param username the user the token is issued to
     * @return the signed token as a compact string
     */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TOKEN_VALIDITY_MS))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Verifies the signature and returns the whole payload.
     *
     * @param token the token received from the client
     * @return all claims stored in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * @param token the token received from the client
     * @return the username stored in the token's subject
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * @param token    the token received from the client
     * @param username the username the token must belong to
     * @return true if the token belongs to this user and has not expired
     */
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}