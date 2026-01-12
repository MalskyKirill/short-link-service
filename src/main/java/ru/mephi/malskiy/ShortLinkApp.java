package ru.mephi.malskiy;

import ru.mephi.malskiy.model.Link;
import ru.mephi.malskiy.service.*;


import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ShortLinkApp {
    public static void main(String[] args) {
        NotificationService notificationService = new NotificationServiceImpl();
        ShortLinkService shortLinkService = new ShortLinkServiceImpl(notificationService);
        UserService userService = new UserService();

        UUID userId = null;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("""
                    1) Создать короткую ссылку
                    2) Перейти по короткой ссылке
                    3) Показать уведомления
                    4) Показать мои ссылки
                    5) Удалить ссылку
                    0) Выход
                    """);
            System.out.print("-> ");

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
                    case "2" -> {
                        System.out.print("Введите короткую ссылку: ");
                        String shortLink = scanner.nextLine().trim();

                        String baseUrl = shortLinkService.followShortLink(shortLink);
                        System.out.println("Открываю в браузере: " + baseUrl);

                        Desktop.getDesktop().browse(new URI(baseUrl));
                    }
                    case "3" -> {
                        var notes = notificationService.pullMessage(userId);
                        if (notes.isEmpty()) {
                            System.out.println("Уведомлений нет.");
                        } else {
                            notes.forEach(n -> System.out.println("- " + n));
                        }
                    }
                    case "4" -> {
                        List<Link> links = shortLinkService.getUserLinks(userId);
                        if (links.isEmpty()) {
                            System.out.println("У вас нет ссылок.");
                        } else {
                            links.forEach(System.out::println);
                        }
                    }
                    case "5" -> {
                        System.out.print("Введите короткую ссылку для удаления: ");
                        String shortLink = scanner.nextLine().trim();
                        shortLinkService.deleteShortLink(userId, shortLink);
                        System.out.println("Ссылка удалена.");
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
