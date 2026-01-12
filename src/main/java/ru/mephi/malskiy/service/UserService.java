package ru.mephi.malskiy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class UserService {
    private static final Path FILE = Paths.get(".user_uuid");
    private UUID userId;

    public UUID getOrCreateUserId() throws IOException { // пытаемся получить пользователя
        if (userId != null) return userId;

        if (Files.exists(FILE)) { // если есть файл
            try {
                userId = UUID.fromString(Files.readString(FILE).trim()); // пытаемся прочитать из файла
                return userId;
            } catch (IOException ex) {
                throw new IOException("Не удалось прочитать id юзера из файла");
            }
        }

        userId = UUID.randomUUID(); // либо создаем
        try {
            Files.writeString(FILE, userId.toString(), StandardOpenOption.CREATE); // пытаемся записать в файл
        } catch (IOException ex) {
            throw new IOException("Не удалось записать id юзера в файл");
        }

        return userId;
    }
}
