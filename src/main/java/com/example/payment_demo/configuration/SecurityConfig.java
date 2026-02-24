package com.example.payment_demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF for your API and Webhooks so POST works
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/payments/**", "/api/webhooks/**")
            )
            // 2. Configure permissions
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access static resources
                .requestMatchers("/favicon.ico", "/css/**", "/js/**", "/images/**", "/*.html").permitAll()
                // Allow access to the payment endpoints
                .requestMatchers("/api/payments/**", "/api/webhooks/**").permitAll()
                // Everything else requires a login
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
