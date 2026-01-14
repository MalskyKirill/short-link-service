package ru.mephi.malskiy.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.mephi.malskiy.config.AppConfig;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShortLinkServiceImplTest {
    private ShortLinkServiceImpl service;
    private NotificationServiceImpl notificationService;

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.shutdown();
        }
    }

    @Test
    void createsUniqueShortLinksPerUser() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        String baseLink = "https://www.baeldung.com/java-9-http-client";
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();

        String firstShort = service.getShortLink(firstUser, baseLink, 5);
        String secondShort = service.getShortLink(secondUser, baseLink, 5);

        assertNotEquals(firstShort, secondShort);
    }

    @Test
    void returnsSameShortLinkForSameUserAndUrl() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        UUID userId = UUID.randomUUID();
        String baseLink = "https://example.com";

        String firstShort = service.getShortLink(userId, baseLink, 5);
        String secondShort = service.getShortLink(userId, baseLink, 5);

        assertEquals(firstShort, secondShort);
    }

    @Test
    void blocksAfterLimitAndSendsNotification() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        UUID userId = UUID.randomUUID();
        String shortLink = service.getShortLink(userId, "https://example.com", 1);

        assertDoesNotThrow(() -> service.followShortLink(shortLink));
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.followShortLink(shortLink));

        assertTrue(exception.getMessage().contains("Лимит переходов исчерпан"));
        List<String> notifications = notificationService.pullMessage(userId);
        assertEquals(1, notifications.size());
        assertTrue(notifications.getFirst().contains("Лимит переходов исчерпан"));
    }

    @Test
    void rejectsInvalidUrls() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        UUID userId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.getShortLink(userId, "ftp://example.com", 3));

        assertTrue(exception.getMessage().contains("Невалидный URL"));
    }

    @Test
    void preventsDeletingOtherUsersLinks() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        String shortLink = service.getShortLink(owner, "https://example.com", 3);

        assertThrows(SecurityException.class, () -> service.deleteShortLink(other, shortLink));
    }

    @Test
    void updatesMaxClicksWithValidation() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(buildConfig(Duration.ofMinutes(10)), notificationService);

        UUID owner = UUID.randomUUID();
        String shortLink = service.getShortLink(owner, "https://example.com", 2);

        assertDoesNotThrow(() -> service.followShortLink(shortLink));
        IllegalArgumentException invalidLimit = assertThrows(IllegalArgumentException.class,
            () -> service.updateMaxClicks(owner, shortLink, 0));
        assertTrue(invalidLimit.getMessage().contains("newMaxClicks"));

        assertDoesNotThrow(() -> service.followShortLink(shortLink));
        IllegalArgumentException tooLow = assertThrows(IllegalArgumentException.class,
            () -> service.updateMaxClicks(owner, shortLink, 1));
        assertTrue(tooLow.getMessage().contains("Новый лимит меньше"));

        assertDoesNotThrow(() -> service.updateMaxClicks(owner, shortLink, 3));
    }

    private AppConfig buildConfig(Duration ttl) {
        return new AppConfig("clck.ru/", ttl, 10, Duration.ofSeconds(60));
    }
}
