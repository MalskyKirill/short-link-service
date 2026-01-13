package ru.mephi.malskiy.config;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static java.lang.Long.parseLong;

public class AppConfig {
    private final String domain;
    private final Duration ttl;
    private final int defaultMaxClick;

    public String getDomain() {
        return domain;
    }

    public Duration getTtl() {
        return ttl;
    }

    public int getDefaultMaxClick() {
        return defaultMaxClick;
    }

    public AppConfig(String domain, Duration ttl, int defaultMaxClick) {
        this.domain = domain;
        this.ttl = ttl;
        this.defaultMaxClick = defaultMaxClick;
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

        return new AppConfig(domain, Duration.ofSeconds(ttl), defaultMaxClick);
    }
}
