package com.cricketscore.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "cricketScoreDotEnvProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Skip .env loading if running under the 'test' profile
        for (String profile : environment.getActiveProfiles()) {
            if ("test".equalsIgnoreCase(profile)) {
                return;
            }
        }

        Map<String, Object> envProperties = new HashMap<>();

        // Locate .env in current dir, backend/, or user dir
        List<Path> candidatePaths = Arrays.asList(
                Paths.get(".env"),
                Paths.get("backend", ".env"),
                Paths.get("../backend", ".env"),
                Paths.get(System.getProperty("user.dir"), ".env"),
                Paths.get(System.getProperty("user.dir"), "backend", ".env")
        );

        for (Path path : candidatePaths) {
            if (Files.isRegularFile(path)) {
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (String line : lines) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            // strip quotes if present
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            envProperties.put(key, value);
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        // Check if DB_URI is supplied in system env or .env
        String dbUriStr = System.getenv("DB_URI");
        if (dbUriStr == null || dbUriStr.isBlank()) {
            Object fromDotEnv = envProperties.get("DB_URI");
            if (fromDotEnv != null) {
                dbUriStr = fromDotEnv.toString();
            }
        }

        if (dbUriStr != null && !dbUriStr.isBlank()) {
            parseAndApplyDbUri(dbUriStr, envProperties);
        }

        // Also normalize SPRING_DATASOURCE_* to spring.datasource.* and vice-versa
        if (envProperties.containsKey("SPRING_DATASOURCE_URL") && !envProperties.containsKey("spring.datasource.url")) {
            envProperties.put("spring.datasource.url", envProperties.get("SPRING_DATASOURCE_URL"));
        }
        if (envProperties.containsKey("SPRING_DATASOURCE_USERNAME") && !envProperties.containsKey("spring.datasource.username")) {
            envProperties.put("spring.datasource.username", envProperties.get("SPRING_DATASOURCE_USERNAME"));
        }
        if (envProperties.containsKey("SPRING_DATASOURCE_PASSWORD") && !envProperties.containsKey("spring.datasource.password")) {
            envProperties.put("spring.datasource.password", envProperties.get("SPRING_DATASOURCE_PASSWORD"));
        }

        if (!envProperties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, envProperties));
        }
    }

    private void parseAndApplyDbUri(String dbUriStr, Map<String, Object> targetMap) {
        try {
            // Standard PostgreSQL URI: postgresql://username:password@hostname:port/database?params
            String cleanUri = dbUriStr.trim();
            if (cleanUri.startsWith("postgres://")) {
                cleanUri = "postgresql://" + cleanUri.substring("postgres://".length());
            }

            URI uri = URI.create(cleanUri);
            String userInfo = uri.getUserInfo();
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String query = uri.getQuery();

            String username = null;
            String password = null;
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                password = parts[1];
            } else if (userInfo != null) {
                username = userInfo;
            }

            String database = (path != null && path.length() > 1) ? path.substring(1) : "neondb";
            StringBuilder jdbcUrl = new StringBuilder();
            jdbcUrl.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(database);

            if (query != null && !query.isBlank()) {
                jdbcUrl.append("?").append(query);
            } else {
                jdbcUrl.append("?sslmode=require");
            }

            targetMap.put("spring.datasource.url", jdbcUrl.toString());
            targetMap.put("SPRING_DATASOURCE_URL", jdbcUrl.toString());
            if (username != null) {
                targetMap.put("spring.datasource.username", username);
                targetMap.put("SPRING_DATASOURCE_USERNAME", username);
            }
            if (password != null) {
                targetMap.put("spring.datasource.password", password);
                targetMap.put("SPRING_DATASOURCE_PASSWORD", password);
            }
        } catch (Exception ignored) {
        }
    }
}
