package com.smart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Configuration
public class ApplicationProperties {
    
    @Value("${spring.application.name}")
    private String name;

    @Value("${security.token.token-url}")
    private String tokenUrl;

    private String signingKey;

}