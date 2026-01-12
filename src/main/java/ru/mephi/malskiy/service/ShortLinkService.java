package ru.mephi.malskiy.service;

import java.util.UUID;

public interface ShortLinkService {
    String getShortLink(UUID userId, String baseLink, int maxClick);
}
