package com.xiaomi.info.r2session.test.mock;

import com.xiaomi.info.r2session.api.BlockingSessionClient;

import java.util.*;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/19 14:48
 */
public class MockBlockingSessionClient implements BlockingSessionClient {

    private Map<String, Map<String, String>> sessions = new HashMap<>();

    private Optional<Map<String, String>> getSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public String get(String id, String key) {
        return getSession(id).orElse(Collections.emptyMap()).get(key);
    }

    @Override
    public Set<String> keys(String id) {
        return getSession(id).orElse(Collections.emptyMap()).keySet();
    }

    @Override
    public void set(String id, String key, String value) {
        Optional<Map<String, String>> session = getSession(id);
        if (session.isEmpty()) {
            sessions.put(id, new HashMap<>(Map.of(key, value)));
        } else {
            session.get().put(key, value);
        }
    }

    @Override
    public void del(String id, String key) {
        getSession(id).ifPresent(session -> session.remove(key));
    }

    @Override
    public boolean exist(String id) {
        return getSession(id).isPresent();
    }

    @Override
    public void del(String id) {
        sessions.remove(id);
    }
}
