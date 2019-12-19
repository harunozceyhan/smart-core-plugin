package com.smart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
public class ApplicationProperties {
    @Value("${spring.application.name}")
    private String name;
}