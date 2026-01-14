package ru.mephi.malskiy.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.mephi.malskiy.config.AppConfig;
import ru.mephi.malskiy.model.Link;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShortLinkServiceImplIntegrationTest {
    private ShortLinkServiceImpl service;
    private NotificationService notificationService;

    @AfterEach
    void tearDown() {
        if (service != null) {
            service.shutdown();
        }
    }

    @Test
    void expiresLinksByTtlAndNotifies() throws InterruptedException {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(new AppConfig(
            "clck.ru/", Duration.ofMillis(5), 10, Duration.ofSeconds(60)), notificationService
        );

        UUID userId = UUID.randomUUID();
        String shortLink = service.getShortLink(userId, "https://example.com", 2);

        Thread.sleep(10);

        List<Link> links = service.getUserLinks(userId);
        assertTrue(links.isEmpty());

        List<String> notifications = notificationService.pullMessage(userId);
        assertEquals(1, notifications.size());
        assertTrue(notifications.getFirst().contains(shortLink));
        assertTrue(notifications.getFirst().contains("протухла"));
    }

    @Test
    void scenarioCreateFollowLimitAndList() {
        notificationService = new NotificationServiceImpl();
        service = new ShortLinkServiceImpl(new AppConfig("clck.ru/", Duration.ofMinutes(5), 10, Duration.ofSeconds(60)),
            notificationService);

        UUID userId = UUID.randomUUID();
        String shortLink = service.getShortLink(userId, "https://www.baeldung.com/java-9-http-client", 2);

        assertEquals("https://www.baeldung.com/java-9-http-client", service.followShortLink(shortLink));
        assertEquals("https://www.baeldung.com/java-9-http-client", service.followShortLink(shortLink));

        assertThrows(IllegalStateException.class, () -> service.followShortLink(shortLink));

        List<Link> links = service.getUserLinks(userId);
        assertEquals(1, links.size());
        assertEquals(shortLink, links.getFirst().getShortLink());
    }
}
