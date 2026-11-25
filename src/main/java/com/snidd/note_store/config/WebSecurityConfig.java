package com.snidd.note_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Authentication filter in Application filter
 * Configuration class for login.
 * EnableWebSecurity manages to take control over security.
 * SecurityFilterChain -> instead of auto-config by Spring Boot - manually implement what's needed.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Define custom security filter chain
     * Allow everyone to hit "register" endpoint.
     * Demand authorization everywhere else.
     * @return the SecurityFilterChain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // disable CSRF
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        request -> request
                                // Allow everyone to hit "register" & "login" endpoints.
                                // Should not need to be logged in to register & login.
                                .requestMatchers("/register", "/login").permitAll()
                                // Demand authorization everywhere else.
                                .anyRequest().authenticated()
                )
                // Authenticate with basic http form.
//                .formLogin(Customizer.withDefaults())
                // Authenticate with basic http popup.
                .httpBasic(Customizer.withDefaults())
                // Run JWT filter before Spring Security's own UsernamePasswordAuthenticationFilter.
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }


    /**
     * Password hashing with strength 14.
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(14);
    }


    /**
     * ProviderManager iterates through providers to find
     * the appropriate for current login procedure.
     * AuthenticationManager of type (polymorphism) ProviderManager.
     * AuthenticationManager: Takes a request to authenticate a user
     * and delegates the work to AuthenticationProvider (below) to do the
     * actual authentication of the user's credentials.
     * @param authenticationConfiguration configurations for authentication.
     * @return ProviderManager (AuthenticationManager).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * AuthenticationProvider
     * Asked by AuthenticationManager (ProviderManager) to authenticate.
     * Responsible for user authentication with stored user in DB.
     * Authentication using CustomUserDetailsService & CustomUserDetails.
     * DaoAuthenticationProvider is doing the actual validation for username & password.
     * DaoAuthenticationProvider authenticates a user by fetching it from a DB via UserDetailsService.
     * BCryptPasswordEncoder encodes and validates password.
     * @param userDetailsService CustomUserDetailsService
     * @return provider (Authentication object) of DaoAuthenticationProvider + bCryptPasswordEncoder()
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }
}
