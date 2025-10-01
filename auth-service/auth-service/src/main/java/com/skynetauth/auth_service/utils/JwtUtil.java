package com.skynetauth.auth_service.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.skynetauth.auth_service.exceptions.InvalidJWTTokenException;
import com.skynetauth.auth_service.models.Permission;
import com.skynetauth.auth_service.models.Role;
import com.skynetauth.auth_service.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    /**
     * Creates a signed JWT containing the user's identity and claims.
     *
     * The token includes the user's email as the subject, permissions, roles, userId, issued-at and expiration timestamps,
     * and, if provided, an `impersonatedBy` claim with the administrator's email.
     *
     * @param user the user whose identity and claims will be embedded in the token
     * @param adminEmail the administrator email performing impersonation; when non-null an `impersonatedBy` claim is added
     * @return the compact signed JWT string
     */
    public String generateToken(User user, String adminEmail) {
        Set<String> permissions = user.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        var tokenBuilder =  Jwts.builder()
                .setSubject(user.getEmail())
                .claim("permissions", permissions)
                .claim("userId", user.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512);
        if (adminEmail != null) {
                tokenBuilder.claim("impersonatedBy", adminEmail);
        }
        return tokenBuilder.compact();
    }

    /**
     * Generates an HMAC signing key derived from the configured SECRET_KEY.
     *
     * @return the HMAC signing {@link Key} constructed from the SECRET_KEY bytes
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Extracts the token subject (typically the user's username or email) from the provided JWT.
     *
     * @param token the JWT string to parse
     * @return the subject claim from the token (usually the user's username or email)
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Checks whether the provided JWT can be parsed and its signature validated using the configured signing key.
     *
     * @param token the JWT string to validate
     * @return true if the token is successfully parsed and signature validated, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies the token's signature and checks that its expiration is after the current time.
     *
     * @param token the JWT string to verify
     * @return `true` if the token's signature is valid and its expiration date is after now, `false` otherwise
     * @throws InvalidJWTTokenException if the token cannot be verified
     */
    public boolean verifyToken(String token) throws JWTVerificationException {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token).getExpiresAt().after(new Date());
        } catch (JWTVerificationException e) {
            throw new InvalidJWTTokenException();
        }
    }

    /**
     * Extracts the "permissions" claim from the provided JWT as a list of strings.
     *
     * @param token the JWT compact token string to parse
     * @return the "permissions" claim as a List of strings, or null if the claim is not present
     */
    public List<String> extractPermissions(String token) {
        return extractAllClaims(token).get("permissions", List.class);
    }

    /**
     * Retrieve a specific claim value from a JWT as the requested type.
     *
     * @param token the JWT string to read claims from
     * @param claim the claim name to extract
     * @param type  the expected class of the claim value
     * @param <T>   the return type of the claim value
     * @return the claim value converted to {@code T}, or {@code null} if the claim is not present
     */
    public <T> T extractClaim(String token, String claim, Class<T> type) {
        return extractAllClaims(token).get(claim, type);
    }

    /**
     * Parse and return all claims contained in the provided JWT.
     *
     * @param token the JWT string to parse
     * @return the token's Claims
     * @throws InvalidJWTTokenException if the token cannot be parsed or validated
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build()
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new InvalidJWTTokenException();
        }
    }

    /**
     * Constructs a Spring Security Authentication from a JWT's claims.
     *
     * @param token the JWT string containing subject, roles, and permissions claims
     * @return an Authentication whose principal is the token subject, credentials are null, and authorities contain roles (prefixed with "ROLE_" when missing) and permissions extracted from the token
     */
    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();

        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = new ArrayList<>();
        }

        List<String> permissions = extractPermissions(token);
        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.addAll(roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList());

        authorities.addAll(permissions.stream().map(SimpleGrantedAuthority::new).toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
