package com.internlink.internlink.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.internlink.internlink.service.UserService;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/user/login").permitAll()
                        .requestMatchers("/api/user/session").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/internships/**").permitAll()
                        .requestMatchers("/api/students/register").permitAll()
                        .requestMatchers("/facultySupervisor/register").permitAll()
                        .requestMatchers("/company-supervisors/register").permitAll()
                        .requestMatchers("/api/students").permitAll()
                        .requestMatchers("/api/tasks").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        // Add logging to confirm the UserDetailsService and PasswordEncoder are set
        // correctly
        System.out.println("AuthenticationManager configured with UserDetailsService: " + userDetailsService);
        System.out.println("AuthenticationManager configured with PasswordEncoder: " + passwordEncoder());

        return new ProviderManager(List.of(provider));
    }

    public static String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername(); // Assuming username is userId
        }
        return null;
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }
}
