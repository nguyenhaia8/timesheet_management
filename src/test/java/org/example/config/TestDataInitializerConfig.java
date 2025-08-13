package org.example.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test", "integration", "ci"})
public class TestDataInitializerConfig {

    @Bean
    public CommandLineRunner dataInitializer() {
        return args -> {
            // Empty implementation for testing - no data initialization
            System.out.println("Test environment: Skipping data initialization");
        };
    }
}
