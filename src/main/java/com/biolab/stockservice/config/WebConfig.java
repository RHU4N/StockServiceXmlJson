package com.biolab.stockservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Retorna JSON por padrão quando o cliente não enviar Accept específico
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:4173",
                        "http://127.0.0.1:4173"
                )
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type", "Vary");
    }
}
