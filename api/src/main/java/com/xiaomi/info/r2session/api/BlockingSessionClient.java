package com.xiaomi.info.r2session.api;

import java.time.Duration;
import java.util.Set;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/18 23:56
 */
public interface BlockingSessionClient {

    Set<String> keys(String id);

    boolean exist(String id);

    void del(String id);

    String get(String id, String key);

    void set(String id, String key, String value);

    void del(String id, String key);

    void expire(String id, Duration ttl);

}
