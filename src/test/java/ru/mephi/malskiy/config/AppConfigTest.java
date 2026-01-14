package ru.mephi.malskiy.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AppConfigTest {
    @Test
    void loadsConfigurationFromProperties() {
        AppConfig config = AppConfig.load();

        assertEquals("clck.ru/", config.getDomain());
    }
}
