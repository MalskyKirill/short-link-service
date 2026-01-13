package ru.mephi.malskiy.service;

import ru.mephi.malskiy.model.Link;

import java.util.List;
import java.util.UUID;

public interface ShortLinkService {
    String getShortLink(UUID userId, String baseLink, int maxClick);

    String followShortLink(String shortLink);

    List<Link> getUserLinks(UUID userId);

    void deleteShortLink(UUID userId, String shortLink);

    void updateMaxClicks(UUID userId, String shortLink, int newLimit);
    void shutdown();
}
