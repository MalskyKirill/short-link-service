package ru.mephi.malskiy.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static final Path USER_FILE = Path.of(".user_uuid");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(USER_FILE);
    }

    @Test
    void createsAndPersistsUserId() throws IOException {
        Files.deleteIfExists(USER_FILE);
        UserService userService = new UserService();

        var firstId = userService.getOrCreateUserId();
        assertNotNull(firstId);
        assertTrue(Files.exists(USER_FILE));

        UserService secondService = new UserService();
        var secondId = secondService.getOrCreateUserId();

        assertEquals(firstId, secondId);
    }
}
