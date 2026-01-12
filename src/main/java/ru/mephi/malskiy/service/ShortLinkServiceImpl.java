package ru.mephi.malskiy.service;

import ru.mephi.malskiy.model.Link;
import ru.mephi.malskiy.model.UserLinkKey;
import ru.mephi.malskiy.util.LinkUtil;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ShortLinkServiceImpl implements ShortLinkService{
    private static final String DOMAIN = "click.ru/"; // базовый url
    private static final Duration DEFAULT_TTL = Duration.ofHours(24); // time to live ссылки

    private final Map<String, Link> shortLinksMap = new HashMap<>();
    private final Map<UserLinkKey, Link> userLinksMap = new HashMap<>();

    private final NotificationService notificationService;

    public ShortLinkServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Override
    public String getShortLink(UUID userId, String baseLink, int maxClick) {
        // проверяем валидность переданных аргументов
        if (userId == null) throw new IllegalArgumentException("userId не должен быть null");
        if (baseLink == null || baseLink.isBlank()) throw new IllegalArgumentException("baseLink не должен быть пустым");
        if (maxClick <= 0) throw new IllegalArgumentException("maxClick должно быть больше 0");

        // проверяем создавал ли пользователь короткую ссылку с этого url
        UserLinkKey key = new UserLinkKey(userId, baseLink);
        Link existingLink = userLinksMap.get(key);

        if (existingLink != null) { // если создавал
            System.out.println("Ссылка по такому адресу вами была уже создана");
            return existingLink.getShortLink(); // возвращаем короткую ссылку
        }

        int salt = 0; // соль для защиты от коллизий
        String shortLink;

        while (true) { // крутимся в цикле
            String code = generateShortLinkCode(userId, baseLink, salt); // генерируем код
            shortLink = DOMAIN + code; // получаем короткую ссылку

            if (!shortLinksMap.containsKey(shortLink)) break; // если получили уникальное значение вываливаемся
            salt++; // при колизии подсаливаем
        }

        LocalDateTime expiresAt = LocalDateTime.now().plus(DEFAULT_TTL);
        Link link = new Link(userId, shortLink, baseLink, LocalDateTime.now(), expiresAt, maxClick);

        shortLinksMap.put(shortLink, link);
        userLinksMap.put(key, link);

        return shortLink;

    }

    @Override
    public String followShortLink(String shortLink) {
        if (shortLink == null || shortLink.isBlank()) { // проверяем на валидность
            throw new IllegalArgumentException("Короткая ссылка не должена быть пустой");
        }

        Link link = shortLinksMap.get(shortLink); // берем ссылку
        if (link == null) {
            throw new IllegalArgumentException("Ссылка не найдена или удалена: " + shortLink);
        }

        boolean isCanFollow = link.tryRegisterClick(); // проверяем можем ли мы перейти
        if (!isCanFollow) { // если не можем
            notifyLimit(link); // уведомляем пользователя
            throw new IllegalStateException("Лимит переходов исчерпан: " + shortLink);
        }

        return link.getBaseLink();
    }

    @Override
    public List<Link> getUserLinks(UUID userId) {
        List<Link> userLinks = new ArrayList<>(); // создаем списочек

        for (Link link : shortLinksMap.values()) { // бежим по линкам в мапе
            if (link.getUserId().equals(userId)) userLinks.add(link); // если у линки пользователь совпадает с текущем, добавляем в списочек
        }

        userLinks.sort(Comparator.comparing(Link::getCreatedAt).reversed()); // сортируем сначала самые свежие
        return userLinks;
    }

    @Override
    public void deleteShortLink(UUID userId, String shortLink) {
        Link link = shortLinksMap.get(shortLink);
        if (link == null) {
            throw new IllegalArgumentException("Ссылка не найдена или удалена: " + shortLink);
        }

        if (!link.getUserId().equals(userId)) {
            throw new SecurityException("Нельзя удалить ссылку другого пользователя");
        }

        removeLink(link);
    }

    private void removeLink(Link link) {
        shortLinksMap.remove(link.getShortLink());
        userLinksMap.remove(new UserLinkKey(link.getUserId(), link.getBaseLink()));
    }

    private void notifyLimit(Link link) {
        if (!link.isLimitNotified()) { // проверяем что мы еще не писали уведомление по данной ссылке
            link.setLimitNotified(true); // выставляем флаг
            // пишем уведомление пользователю
            notificationService.notify(link.getUserId(),
                "Лимит переходов исчерпан (" + link.getMaxClick() + "): " + link.getShortLink());
        }
    }

    private String generateShortLinkCode(UUID userId, String baseLink, int salt) {
        String comp = userId + "|" + baseLink + "|" + salt; // собираем строку из 3 частей
        UUID name = UUID.nameUUIDFromBytes(comp.getBytes(StandardCharsets.UTF_8)); // создаем UUID из байтов строки
        long mixXor = name.getMostSignificantBits() ^ name.getLeastSignificantBits(); // делаем xor со старшими 64 билами UUID и 64 младшими битами

        String base62 = LinkUtil.toBase62(mixXor); // переводим число в кодировку base62

        if (base62.length() > 8) base62.substring(0, 8); // если код длиннее 8 символов обрезаем

        return base62;
    }
}
