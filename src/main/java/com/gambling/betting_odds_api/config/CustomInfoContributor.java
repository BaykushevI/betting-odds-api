package com.gambling.betting_odds_api.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
 * Custom Info Contributor for Actuator /info endpoint
 * 
 * This class adds custom information about the application that will be displayed
 * when accessing http://localhost:8080/actuator/info
 * 
 * Use cases:
 * - Display application version and build information
 * - Show team contact information
 * - Display current configuration profile (dev/prod)
 * - Show uptime or startup time
 * 
 * This is useful for:
 * - DevOps teams to quickly identify which version is deployed
 * - Monitoring dashboards
 * - Troubleshooting (knowing exact version/build)
 */
@Component
public class CustomInfoContributor implements InfoContributor {

    private final LocalDateTime startupTime;

    public CustomInfoContributor() {
        // Record application startup time
        this.startupTime = LocalDateTime.now();
    }

    @Override
    public void contribute(Info.Builder builder) {
        // Application information
        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("name", "Betting Odds API");
        appInfo.put("description", "REST API for managing betting odds for sports matches.");
        appInfo.put("version", "1.0.0");
        appInfo.put("buildTime", startupTime.toString());

        // Team information
        Map<String, Object> teamInfo = new HashMap<>();
        teamInfo.put("Developer", "Iliyan Baykushev");
        teamInfo.put("email", "i.baykoushev@outlook.com");
        teamInfo.put("github", "https://github.com/BaykushevI/betting-odds-api");

        // Feautures information
        Map<String, Object> features = new HashMap<>();
        features.put("pagination", "enabled");
        features.put("sorting", "enabled");
        features.put("validation", "enabled");
        features.put("swaggerDocumentation", "enabled");
        features.put("actuatorEndpoints", "enabled");

        // Technology stack
        Map<String, Object> techStack = new HashMap<>();
        techStack.put("framework", "Spring Boot 3.5.6");
        techStack.put("language", "Java 23");
        techStack.put("database", "PostgreSQL 18");
        techStack.put("buildTool", "Maven");

        // Add all info to the builder
        builder.withDetail("application", appInfo)
               .withDetail("team", teamInfo)
               .withDetail("features", features)
               .withDetail("technologyStack", techStack);

    }
}
