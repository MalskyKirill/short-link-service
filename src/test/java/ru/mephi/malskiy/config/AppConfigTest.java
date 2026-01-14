package ru.mephi.malskiy.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {
    @Test
    void loadsConfigurationFromProperties() {
        AppConfig config = AppConfig.load();

        assertEquals("clck.ru/", config.getDomain());
    }
}
