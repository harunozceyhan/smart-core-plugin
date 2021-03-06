package com.smart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Configuration
public class ApplicationProperties {

    public static String signingKey;

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.application.admin-role}")
    private String adminRole;

    @Value("${security.token.realm-url}")
    private String realmUrl;

}