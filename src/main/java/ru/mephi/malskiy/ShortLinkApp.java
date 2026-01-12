package ru.mephi.malskiy;

import ru.mephi.malskiy.service.ShortLinkService;
import ru.mephi.malskiy.service.ShortLinkServiceImpl;
import ru.mephi.malskiy.service.UserService;

import java.util.Scanner;
import java.util.UUID;

public class ShortLinkApp {
    public static void main(String[] args) {
        ShortLinkService shortLinkService = new ShortLinkServiceImpl();
        UserService userService = new UserService();

        UUID userId = null;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("""
                    1) Создать короткую ссылку
                    0) Выход
                    """);

            String cmd = scanner.nextLine().trim();

            try {
                switch (cmd) {
                    case "1" -> {
                        if (userId == null) {
                            userId = userService.getOrCreateUserId();
                            System.out.println("Ваш userId: " + userId);
                        }

                        System.out.print("Введите URL: ");
                        String baseUrl = scanner.nextLine().trim();

                        System.out.print("Введите лимит переходов: ");
                        int maxClick = Integer.parseInt(scanner.nextLine().trim());

                        String shortUrl = shortLinkService.getShortLink(userId, baseUrl, maxClick);
                        System.out.println("Короткая ссылка: " + shortUrl);
                        System.out.println("Время жизни ссылки 24 часа");
                    }
                    case "0" -> {
                        System.out.println("Завершаем работу приложения.");
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }

            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }

        }
    }
}
