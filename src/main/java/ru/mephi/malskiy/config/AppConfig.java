package ru.mephi.malskiy.config;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static java.lang.Long.parseLong;

public class AppConfig {
    private final String domain;
    private final Duration ttl;
    private final int defaultMaxClick;
    private final Duration cleanupInterval;

    public String getDomain() {
        return domain;
    }

    public Duration getTtl() {
        return ttl;
    }

    public int getDefaultMaxClick() {
        return defaultMaxClick;
    }

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public AppConfig(String domain, Duration ttl, int defaultMaxClick, Duration cleanupInterval) {
        this.domain = domain;
        this.ttl = ttl;
        this.defaultMaxClick = defaultMaxClick;
        this.cleanupInterval = cleanupInterval;
    }

    public static AppConfig load() {
        Properties prop = new Properties();

        try (InputStream read = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")){
            prop.load(read);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        String domain = prop.getProperty("domain");
        long ttl = parseLong(prop.getProperty("ttlSeconds"));
        int defaultMaxClick = Integer.parseInt(prop.getProperty("defaultMaxClick"));
        long cleanupInterval = parseLong(prop.getProperty("cleanupSeconds"));

        return new AppConfig(domain, Duration.ofSeconds(ttl), defaultMaxClick, Duration.ofSeconds(cleanupInterval));
    }
}
