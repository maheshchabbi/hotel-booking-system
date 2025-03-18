package com.dlim2012.security.config;

import com.dlim2012.security.dto.PublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@EnableConfigurationProperties(PublicKey.class)
@RequiredArgsConstructor
public class AuthenticationConfig {
    
    private final PublicKey publicKey;
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey.publicKey()).build();
    }
}
