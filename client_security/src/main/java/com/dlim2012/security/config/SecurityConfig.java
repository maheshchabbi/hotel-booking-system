package com.dlim2012.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

// Updated import to match the actual package of JwtAuthenticationFilter
import com.dlim2012.security.config.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/hotel/image/*",
                                "/api/v1/hotel/public/**",
                                "/api/v1/search/hotel",
                                "/api/v1/search/price",
                                "/api/v1/booking/public/**",
                                "/api/v1/booking/payment/**",
                                "/api/v1/booking/redirect",
                                "/api/v1/hotel/test",
                                "/api/v1/booking/test",
                                "/api/v1/search/test",
                                "/api/v1/booking-management/test",
                                "/api/v1/hotel/test/**",
                                "/api/v1/booking/test/**",
                                "/api/v1/search/test/**",
                                "/api/v1/search-consumer/test/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
