package com.skynetauth.auth_service.utils;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenProvider;

    /**
     * Create a JwtAuthenticationFilter that uses the provided JwtUtil to validate JWTs and build Authentication objects.
     *
     * @param jwtTokenProvider utility used to verify tokens and obtain Authentication instances from a token
     */
    public JwtAuthenticationFilter(JwtUtil jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Processes the HTTP request by extracting a Bearer JWT from the Authorization header,
     * validating it, and if valid, setting the corresponding Authentication in the security context
     * before continuing the filter chain.
     *
     * @param request     the incoming HTTP request; the Authorization header is inspected for a `Bearer ` token
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue after authentication processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.verifyToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}

