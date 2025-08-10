package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "https://timesheet-hzo0vjn9n-nguyenhais-projects-02a5a8a4.vercel.app",
                    "https://*.vercel.app",
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "http://localhost:8080",
                    "*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "Content-Length")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins including Vercel
        configuration.addAllowedOrigin("https://timesheet-hzo0vjn9n-nguyenhais-projects-02a5a8a4.vercel.app");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:3001");
        configuration.addAllowedOrigin("http://localhost:8080");
        
        // Also allow all origins for development flexibility
        configuration.addAllowedOriginPattern("*");
        
        // Allow all methods
        configuration.addAllowedMethod("*");
        
        // Allow all headers
        configuration.addAllowedHeader("*");
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Set max age
        configuration.setMaxAge(3600L);
        
        // Add exposed headers
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("Content-Length");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 