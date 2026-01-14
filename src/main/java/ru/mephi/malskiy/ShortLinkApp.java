package ru.mephi.malskiy;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import ru.mephi.malskiy.config.AppConfig;
import ru.mephi.malskiy.model.Link;
import ru.mephi.malskiy.service.*;

public class ShortLinkApp {
    public static void main(String[] args) {
        AppConfig config = AppConfig.load();
        NotificationService notificationService = new NotificationServiceImpl();
        ShortLinkService shortLinkService = new ShortLinkServiceImpl(config, notificationService);
        UserService userService = new UserService();

        UUID userId = null;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("Выберите действие:");
            System.out.print(
                    """
                    1) Создать короткую ссылку
                    2) Перейти по короткой ссылке
                    3) Показать уведомления
                    4) Показать мои ссылки
                    5) Удалить ссылку
                    6) Сменить пользователя (ввести UUID вручную)
                    7) Показать текущий UUID
                    8) Изменить лимит переходов по моей ссылке
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

                        System.out.print("Введите лимит переходов (Enter для " + config.getDefaultMaxClick() + "): ");
                        String maxClickRaw = scanner.nextLine().trim();
                        int maxClick =
                                maxClickRaw.isEmpty() ? config.getDefaultMaxClick() : Integer.parseInt(maxClickRaw);

                        String shortUrl = shortLinkService.getShortLink(userId, baseUrl, maxClick);
                        System.out.println("Короткая ссылка: " + shortUrl);
                        long ttlMinutes = config.getTtl().toMinutes();
                        if (ttlMinutes < 60) {
                            System.out.println("Время жизни ссылки " + ttlMinutes + " мин.");
                        } else {
                            long hours = ttlMinutes / 60;
                            long minutes = ttlMinutes % 60;
                            if (minutes == 0) {
                                System.out.println("Время жизни ссылки " + hours + " ч.");
                            } else {
                                System.out.println("Время жизни ссылки " + hours + " ч " + minutes + " мин.");
                            }
                        }
                    }
                    case "2" -> {
                        System.out.print("Введите короткую ссылку: ");
                        String shortLink = scanner.nextLine().trim();

                        String baseUrl = shortLinkService.followShortLink(shortLink);
                        System.out.println("Открываю в браузере: " + baseUrl);

                        Desktop.getDesktop().browse(new URI(baseUrl));
                    }
                    case "3" -> {
                        if (requireUser(userId)) break;
                        var notes = notificationService.pullMessage(userId);
                        if (notes.isEmpty()) {
                            System.out.println("Уведомлений нет.");
                        } else {
                            notes.forEach(n -> System.out.println("- " + n));
                        }
                    }
                    case "4" -> {
                        if (requireUser(userId)) break;
                        List<Link> links = shortLinkService.getUserLinks(userId);
                        if (links.isEmpty()) {
                            System.out.println("У вас нет ссылок.");
                        } else {
                            links.forEach(System.out::println);
                        }
                    }
                    case "5" -> {
                        if (requireUser(userId)) break;
                        System.out.print("Введите короткую ссылку для удаления: ");
                        String shortLink = scanner.nextLine().trim();
                        shortLinkService.deleteShortLink(userId, shortLink);
                        System.out.println("Ссылка удалена.");
                    }
                    case "6" -> {
                        System.out.print("Введите UUID (или пусто, чтобы остаться текущим): ");
                        String s = scanner.nextLine().trim();
                        System.out.println(s);
                        if (!s.isEmpty()) {
                            userId = UUID.fromString(s);
                            System.out.println("Текущий пользователь: " + userId);
                        }
                    }
                    case "7" -> {
                        if (userId == null) System.out.println("UUID ещё не создан.");
                        else System.out.println("Текущий UUID: " + userId);
                    }
                    case "8" -> {
                        if (requireUser(userId)) break;

                        System.out.print("Введите короткую ссылку: ");
                        String shortLink = scanner.nextLine().trim();

                        System.out.print("Введите новый лимит: ");
                        int newLimit = Integer.parseInt(scanner.nextLine().trim());

                        shortLinkService.updateMaxClicks(userId, shortLink, newLimit);
                        System.out.println("Лимит обновлён.");
                    }
                    case "0" -> {
                        System.out.println("Завершаем работу приложения.");
                        shortLinkService.shutdown();
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }

            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static boolean requireUser(UUID userId) {
        if (userId == null) {
            System.out.println("Сначала создайте короткую ссылку, чтобы получить UUID или введите его ручками.");
            return true;
        }
        return false;
    }
}
