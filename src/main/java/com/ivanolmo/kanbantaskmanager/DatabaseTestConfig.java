package com.ivanolmo.kanbantaskmanager;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseTestConfig {
  @Bean
  public CommandLineRunner testConnection(JdbcTemplate jdbcTemplate) {
    return args -> {
      try {
        jdbcTemplate.execute("SELECT 1");
        System.out.println("Database connection: SUCCESS");
      } catch (Exception e) {
        System.out.println("Database connection: FAILED");
        e.printStackTrace();
      }
    };
  }
}
