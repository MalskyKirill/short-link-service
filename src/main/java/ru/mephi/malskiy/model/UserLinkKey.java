package ru.mephi.malskiy.model;

import java.util.Objects;
import java.util.UUID;

public class UserLinkKey {
    private final UUID userId;
    private final String baseLink;

    public UserLinkKey(UUID userId, String baseLink) {
        this.userId = userId;
        this.baseLink = baseLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLinkKey that = (UserLinkKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(baseLink, that.baseLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, baseLink);
    }
}
