package ru.mephi.malskiy.service;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void notify(UUID userId, String message);

    List<String> pullMessage(UUID userId);
}
