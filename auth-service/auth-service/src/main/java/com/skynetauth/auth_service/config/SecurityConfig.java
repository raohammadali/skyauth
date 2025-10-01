package com.skynetauth.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.skynetauth.auth_service.utils.JwtAuthenticationFilter;
import com.skynetauth.auth_service.utils.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtUtil jwtTokenProvider;

    /**
     * Create a SecurityConfig that configures application security and integrates JWT support.
     *
     * @param jwtTokenProvider utility used to create, validate, and parse JWTs for authentication handling
     */
    public SecurityConfig(JwtUtil jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    /**
     * Configure and build the application's SecurityFilterChain with JWT authentication, stateless sessions, and request authorization rules.
     *
     * The chain:
     * - enables default CORS and disables CSRF,
     * - uses RestAuthenticationEntryPoint for authentication failures,
     * - permits unauthenticated access to specified public and Swagger-related endpoints (including GET /api/auth/**),
     * - requires authentication for all other requests,
     * - enforces stateless session management, and
     * - installs a JwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter.
     *
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring HTTP security
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new RestAuthenticationEntryPoint())

                )
                .authorizeHttpRequests((auth) ->
                        auth.requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**",
                                        "/api/auth/**",
                                        "/api/roles/**", 
                                        "/public_resource",
                                        "/api/distribution/**",
                                        "/api/permissions/**",
                                        "/error"
                                ).permitAll()
                                .anyRequest().authenticated()

                )
                .sessionManagement(s -> s
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Creates an AuthenticationManager backed by a DaoAuthenticationProvider.
     *
     * <p>The provider is configured to load user details from the supplied UserDetailsService
     * and to verify passwords with the supplied PasswordEncoder.</p>
     *
     * @return an AuthenticationManager that authenticates using the provided UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }
    /**
     * Provides a BCrypt-based PasswordEncoder for encoding and verifying user passwords.
     *
     * @return a PasswordEncoder that uses the BCrypt hashing algorithm
     */
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}