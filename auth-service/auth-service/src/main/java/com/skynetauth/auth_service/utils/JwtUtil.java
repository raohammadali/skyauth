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

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyToken(String token) throws JWTVerificationException {
        try {
            Algorithm algorithm = Algorithm.HMAC512(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token).getExpiresAt().after(new Date());
        } catch (JWTVerificationException e) {
            throw new InvalidJWTTokenException();
        }
    }

    public List<String> extractPermissions(String token) {
        return extractAllClaims(token).get("permissions", List.class);
    }

    public <T> T extractClaim(String token, String claim, Class<T> type) {
        return extractAllClaims(token).get(claim, type);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build()
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new InvalidJWTTokenException();
        }
    }

    // Build Authentication object from token
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
