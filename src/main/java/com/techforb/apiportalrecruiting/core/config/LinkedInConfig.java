package com.techforb.apiportalrecruiting.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para LinkedIn OAuth
 */
@Configuration
@Getter
public class LinkedInConfig {
    
    @Value("${linkedin.client.id}")
    private String clientId;
    
    @Value("${linkedin.client.secret}")
    private String clientSecret;
    
    @Value("${linkedin.redirect.uri}")
    private String redirectUri;
    
    @Value("${linkedin.scope}")
    private String scope;
    
}