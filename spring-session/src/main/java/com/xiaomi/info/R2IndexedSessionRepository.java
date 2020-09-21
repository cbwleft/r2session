package com.xiaomi.info;

import com.xiaomi.info.r2session.api.R2SessionClient;
import org.springframework.session.FindByIndexNameSessionRepository;

import java.util.Map;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/18 20:23
 */
public class R2IndexedSessionRepository
        implements FindByIndexNameSessionRepository<R2Session> {

    private final R2SessionClient client;

    public R2IndexedSessionRepository(R2SessionClient client) {
        this.client = client;
    }

    @Override
    public Map<String, R2Session> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        return null;
    }

    @Override
    public R2Session createSession() {
        return new R2Session(client);
    }

    @Override
    public void save(R2Session session) {

    }

    @Override
    public R2Session findById(String id) {
        if (client.exist(id)) {
            return new R2Session(client, id);
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        client.del(id);
    }
}
