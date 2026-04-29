package org.example.caffe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins — required for ngrok tunnels & mobile app builds
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow all standard HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers (including Authorization, Content-Type, etc.)
        config.setAllowedHeaders(List.of("*"));

        // Expose headers the Flutter app may need to read
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // Allow credentials (cookies / auth headers)
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour to reduce OPTIONS round-trips
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);   // apply to ALL endpoints

        return new CorsFilter(source);
    }
}
