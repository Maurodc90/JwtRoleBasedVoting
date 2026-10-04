package com.maurodelcore.votecaster.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security setup for the voting system: stateless JWT authentication,
 * hardcoded users and role-based access to the endpoints.
 */
@Configuration
public class SecurityConfig {

    /**
     * Hashes passwords with BCrypt. Hashing is one-way, so a stored password cannot be decrypted.
     *
     * @return the password encoder used for all users
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the hardcoded users: one admin and four voters.
     * Hardcoded credentials are for practice only; a real app would load users from a database.
     *
     * @return an in-memory user store
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails mauro = User.withUsername("Mauro")
                .password(passwordEncoder().encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails elcin = User.withUsername("Elcin")
                .password(passwordEncoder().encode("1234"))
                .roles("VOTER")
                .build();

        UserDetails nurten = User.withUsername("Nurten")
                .password(passwordEncoder().encode("1234"))
                .roles("VOTER")
                .build();

        UserDetails yagmur = User.withUsername("Yagmur")
                .password(passwordEncoder().encode("1234"))
                .roles("VOTER")
                .build();

        UserDetails orhan = User.withUsername("Orhan")
                .password(passwordEncoder().encode("1234"))
                .roles("VOTER")
                .build();

        return new InMemoryUserDetailsManager(mauro, elcin, nurten, yagmur, orhan);
    }

    /**
     * Configures the security rules: no sessions, JWT checked on every request,
     * and which role may reach which endpoint.
     *
     * @param http          the security builder provided by Spring
     * @param jwtAuthFilter the filter that reads the token and identifies the user
     * @return the configured filter chain
     * @throws Exception if the configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // No sessions: every request must prove who it is with its token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CSRF relies on the browser sending credentials automatically; a token in a header is not sent that way
                .csrf(csrf -> csrf.disable())

                // -- Access rules --
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/results/**").hasRole("ADMIN")
                        .requestMatchers("/vote/**").hasRole("VOTER")
                        .anyRequest().authenticated())

                // Identify the user from the token before the access rules are checked
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes Spring's authentication manager so the login endpoint can verify credentials.
     *
     * @param configuration Spring's authentication configuration
     * @return the authentication manager
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}