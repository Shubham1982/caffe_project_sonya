package org.example.caffe.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseSetupConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSetupConfig.class);

    @Bean
    public CommandLineRunner setupDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                log.info("Starting automatic database setup for PostgreSQL Trigram search...");

                // 1. Enable the pg_trgm extension if not already present
                // Note: Requieres superuser or database owner privileges
                jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm");

                // 2. Create the GIN index for the products table on the product_name column
                // Using IF NOT EXISTS to avoid errors on subsequent runs
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_products_trgm ON products USING gin (product_name gin_trgm_ops)");

                log.info("Database setup completed successfully. PostgreSQL Trigram search is ready.");
            } catch (Exception e) {
                log.error("Failed to complete automatic database setup: {}. " +
                        "Pleas ensure your database user has the necessary permissions to create extensions and indexes.", e.getMessage());
                // We don't throw the exception to avoid blocking application startup, 
                // but the search functionality might be slow without the index.
            }
        };
    }
}
