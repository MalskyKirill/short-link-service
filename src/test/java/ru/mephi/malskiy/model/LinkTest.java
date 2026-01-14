package ru.mephi.malskiy.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LinkTest {
    @Test
    void registersClickUntilLimit() {
        Link link = new Link(
                UUID.randomUUID(),
                "clck.ru/abc",
                "https://example.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                2);

        assertTrue(link.tryRegisterClick());
        assertTrue(link.tryRegisterClick());
        assertFalse(link.tryRegisterClick());
        assertEquals(2, link.getClicks());
    }

    @Test
    void detectsExpirationByTimestamp() {
        LocalDateTime created = LocalDateTime.now();
        Link link =
                new Link(UUID.randomUUID(), "clck.ru/abc", "https://example.com", created, created.plusSeconds(5), 1);
        assertFalse(link.isExpired(created));
        assertTrue(link.isExpired(created.plusSeconds(6)));
    }
}
