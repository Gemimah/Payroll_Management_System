package com.erp.employee_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties props;
    private final Clock clock;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.clock = Clock.systemUTC();
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now(clock);
        Instant exp = now.plus(props.expirationMinutes(), ChronoUnit.MINUTES);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("roles", roles)
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(Date.from(Instant.now(clock)));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }
}

