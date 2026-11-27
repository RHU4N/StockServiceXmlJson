package com.biolab.stockservice.config;

import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfig(@Value("${CORS_ALLOWED_ORIGINS:}") String productionOrigins) {
        allowedOrigins = Stream.concat(
                        Stream.of("http://localhost:5173", "http://127.0.0.1:5173",
                                "http://localhost:4173", "http://127.0.0.1:4173"),
                        Stream.of(productionOrigins.split(",")).map(String::trim)
                                .filter(origin -> !origin.isEmpty()))
                .distinct().toArray(String[]::new);
    }

    // APRESENTACAO 06 (extra) — JSON e o formato padrao quando nao ha preferencia especifica.
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Retorna JSON por padrão quando o cliente não enviar Accept específico
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type", "Vary");
    }
}
