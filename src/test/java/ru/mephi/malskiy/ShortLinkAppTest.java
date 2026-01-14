package ru.mephi.malskiy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ShortLinkAppTest {
    private static final Path USER_FILE = Path.of(".user_uuid");
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @AfterEach
    void cleanup() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(USER_FILE);
    }

    @Test
    void exitsWhenUserSelectsZero() throws IOException {
        String output = runAppWithInput("0\n");
        assertTrue(output.contains("Завершаем работу приложения."));
    }

    @Test
    void printsUnknownCommandMessage() throws IOException {
        String output = runAppWithInput("9\n0\n");
        assertTrue(output.contains("Неизвестная команда."));
    }

    @Test
    void createsShortLinkAndShowsUserId() throws IOException {
        String output = runAppWithInput("1\nhttps://example.com\n\n0\n");
        assertTrue(output.contains("Ваш userId:"));
        assertTrue(output.contains("Короткая ссылка:"));
    }

    private String runAppWithInput(String input) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));

        ShortLinkApp.main(new String[0]);
        return out.toString(StandardCharsets.UTF_8);
    }
}
