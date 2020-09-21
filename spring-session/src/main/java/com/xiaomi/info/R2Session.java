package com.xiaomi.info;

import com.xiaomi.info.r2session.api.R2SessionClient;
import org.springframework.session.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/18 19:33
 */
public class R2Session implements Session {

    private final String id;
    private final R2SessionClient client;

    private enum Keys {

        CREATION_TIME("_creationTime"),
        LAST_ACCESSED_TIME("_lastAccessedTime");
        private final String name;

        Keys(String name) {
            this.name = name;
        }

        static boolean contains(String key) {
            return Arrays.stream(Keys.values()).anyMatch(k -> k.name.equals(key));
        }

    }

    public R2Session(R2SessionClient client){
        id = UUID.randomUUID().toString();
        this.client = client;
        client.set(id, Keys.CREATION_TIME.name, String.valueOf(System.currentTimeMillis()));
    }

    public R2Session(R2SessionClient client, String id) {
        this.id = id;
        this.client = client;
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
        return (T) deSerialize(client.get(id, attributeName));
    }

    @Override
    public Set<String> getAttributeNames() {
        return client.keys(id).stream().filter(k -> !Keys.contains(k)).collect(toSet());
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        client.set(id, attributeName, serialize(attributeValue));
    }

    @Override
    public void removeAttribute(String attributeName) {
        client.del(id, attributeName);
    }

    @Override
    public Instant getCreationTime() {
        return Instant.ofEpochMilli(Long.parseLong(client.get(id, Keys.CREATION_TIME.name)));
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        client.set(id, Keys.LAST_ACCESSED_TIME.name, String.valueOf(lastAccessedTime.toEpochMilli()));
    }

    @Override
    public Instant getLastAccessedTime() {
        return Instant.ofEpochMilli(Long.parseLong(client.get(id, Keys.LAST_ACCESSED_TIME.name)));
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {

    }

    @Override
    public Duration getMaxInactiveInterval() {
        return null;
    }

    @Override
    public boolean isExpired() {
        return client.exist(id);
    }

    private Object deSerialize(String value) {
        return value;
    }

    private String serialize(Object value) {
        return String.valueOf(value);
    }

}
