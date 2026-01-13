package ru.mephi.malskiy.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Link {
    private final UUID userId;
    private final String shortLink;
    private final String baseLink;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private int maxClick;
    private int clicks;

    public void setMaxClick(int maxClick) {
        this.maxClick = maxClick;
    }

    private boolean limitNotified = false;

    public Link(UUID userId, String shortLink, String baseLink, LocalDateTime createdAt, LocalDateTime expiresAt, int maxClick) {
        this.userId = userId;
        this.shortLink = shortLink;
        this.baseLink = baseLink;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.maxClick = maxClick;
        this.clicks = 0;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getShortLink() {
        return shortLink;
    }

    public String getBaseLink() {
        return baseLink;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public int getMaxClick() {
        return maxClick;
    }

    public int getClicks() {
        return clicks;
    }

    public boolean isExpired(LocalDateTime time) { // проверка протухла ли ссылка
        return time.isAfter(expiresAt);
    }

    public boolean tryRegisterClick() { // регистрируем переход по ссылке
        if (clicks >= maxClick) return false;

        clicks++;
        return true;
    }

    public boolean isLimitNotified() { // достигнут лимит перехода
        return limitNotified;
    }

    public void setLimitNotified(boolean limitNotified) {
        this.limitNotified = limitNotified;
    }

    @Override
    public String toString() {
        return "Link{" +
            "userId=" + userId +
            ", shortLink='" + shortLink + '\'' +
            ", baseLink='" + baseLink + '\'' +
            ", createdAt=" + createdAt +
            ", expiresAt=" + expiresAt +
            ", maxClick=" + maxClick +
            ", clicks=" + clicks +
            ", limitNotified=" + limitNotified +
            '}';
    }
}
