package com.xiaomi.info.r2session.spring;

import com.xiaomi.info.r2session.api.BlockingSessionClient;
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

    private final BlockingSessionClient client;

    public R2IndexedSessionRepository(BlockingSessionClient client) {
        this.client = client;
    }

    @Override
    public Map<String, R2Session> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R2Session createSession() {
        return new R2Session(client);
    }

    /**
     * Some implementations may choose to save as the Session is updated by
     * returning a Session that immediately persists any changes.
     * In this case, this method may not actually do anything.
     */
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
