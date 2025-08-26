package com.project.security;

public class SecurityConstants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "ecommerce-app";
    public static final String TOKEN_AUDIENCE = "ecommerce-app-users";
    
    // Public endpoints that don't require authentication
    public static final String[] PUBLIC_URLS = {
        "/api/auth/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/webjars/**",
        "/actuator/**"
    };
}
