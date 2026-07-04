package com.lietech.interviewanalyzer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.security.jwt-secret}") String secret,
            @Value("${app.security.jwt-expiration-minutes}") long expirationMinutes
    ) {

        this.signingKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );

        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UserPrincipal principal) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.id())
                .claim(
                        "roles",
                        principal.authorities()
                                .stream()
                                .map(Object::toString)
                                .toList()
                )
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(
                                now.plusSeconds(expirationMinutes * 60)
                        )
                )
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String token) {

        try {
            return parseClaims(token).getSubject();

        } catch (ExpiredJwtException e) {

            return null;

        } catch (JwtException e) {

            return null;
        }
    }

    public boolean isValid(
            String token,
            UserPrincipal principal
    ) {

        try {

            Claims claims = parseClaims(token);

            return claims.getSubject()
                    .equals(principal.getUsername())
                    &&
                    claims.getExpiration().after(new Date());

        } catch (JwtException e) {

            return false;
        }
    }

    private Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}