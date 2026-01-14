package ru.mephi.malskiy.service;

import java.util.*;

public class NotificationServiceImpl implements NotificationService {
    private final Map<UUID, List<String>> store = new HashMap<>();

    @Override
    public void notify(UUID userId, String message) {
        store.computeIfAbsent(userId, id -> new ArrayList<>())
                .add(message); // если пользователь есть в хранилище возвращает его список, если нет пустой
        // arrayList, и добавляет сообщение
    }

    @Override
    public List<String> pullMessage(UUID userId) {
        List<String> list = store.getOrDefault(userId, new ArrayList<>()); // берем список сообщений пользователя
        store.remove(userId); // удаляем пользователя из хранилища
        return list; // возвращаем сообщения
    }
}
