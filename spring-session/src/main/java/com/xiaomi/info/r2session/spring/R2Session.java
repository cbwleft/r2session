package com.xiaomi.info.r2session.spring;

import com.xiaomi.info.r2session.api.BlockingSessionClient;
import org.springframework.lang.Nullable;
import org.springframework.session.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toSet;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/18 19:33
 */
public class R2Session implements Session {

    private final String id;

    private final BlockingSessionClient client;

    private final R2SessionSerializer serializer = R2SessionSerializer.instance();

    public static final Duration DEFAULT_INACTIVE_INTERVAL = Duration.ofSeconds(1800);

    private enum Key {

        CREATION_TIME("_creationTime"),
        LAST_ACCESSED_TIME("_lastAccessedTime"),
        MAX_INACTIVE_INTERVAL("_maxInactiveInterval");
        private final String name;

        Key(String name) {
            this.name = name;
        }

        static boolean contains(String key) {
            return Arrays.stream(Key.values()).anyMatch(k -> k.name.equals(key));
        }

    }

    public R2Session(BlockingSessionClient client) {
        id = UUID.randomUUID().toString();
        this.client = client;
        set(Key.CREATION_TIME, String.valueOf(System.currentTimeMillis()));
        set(Key.MAX_INACTIVE_INTERVAL, String.valueOf(DEFAULT_INACTIVE_INTERVAL.getSeconds()));
    }

    public R2Session(BlockingSessionClient client, String id) {
        this.id = id;
        this.client = client;
    }

    private final ConcurrentHashMap<Key, String> cache = new ConcurrentHashMap<>();

    private void set(Key key, String value) {
        cache.computeIfAbsent(key, (k) -> {
            client.set(id, k.name, value);
            return value;
        });
    }

    private String get(Key key) {
        return cache.computeIfAbsent(key, (k) -> client.get(id, key.name));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String changeSessionId() {
        return id;
    }

    @Override
    public <T> T getAttribute(String attributeName) {
        return (T) serializer.deSerialize(client.get(id, attributeName)).orElse(null);
    }

    @Override
    public Set<String> getAttributeNames() {
        return client.keys(id).stream().filter(k -> !Key.contains(k)).collect(toSet());
    }

    @Override
    public void setAttribute(String attributeName, @Nullable Object attributeValue) {
        if (attributeValue == null) {
            removeAttribute(attributeName);
        } else {
            client.set(id, attributeName, serializer.serialize(attributeValue).get());
        }
    }

    @Override
    public void removeAttribute(String attributeName) {
        client.del(id, attributeName);
    }

    @Override
    public Instant getCreationTime() {
        return Instant.ofEpochMilli(Long.parseLong(client.get(id, Key.CREATION_TIME.name)));
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        set(Key.LAST_ACCESSED_TIME, String.valueOf(lastAccessedTime.toEpochMilli()));
        client.expire(id, getMaxInactiveInterval());
    }

    @Override
    public Instant getLastAccessedTime() {
        return Instant.ofEpochMilli(Long.parseLong(get(Key.LAST_ACCESSED_TIME)));
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {
        set(Key.MAX_INACTIVE_INTERVAL, String.valueOf(interval.getSeconds()));
    }

    @Override
    public Duration getMaxInactiveInterval() {
        return Optional.ofNullable(get(Key.MAX_INACTIVE_INTERVAL))
                .map(Long::parseLong).map(Duration::ofSeconds).orElse(DEFAULT_INACTIVE_INTERVAL);
    }

    @Override
    public boolean isExpired() {
        return client.exist(id);
    }

}
