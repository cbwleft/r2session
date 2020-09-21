package com.xiaomi.info.r2session.api;

import java.util.Set;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/18 23:56
 */
public interface R2SessionClient {

    String get(String id, String key);

    Set<String> keys(String id);

    void set(String id, String key, String value);

    void del(String id, String key);

    boolean exist(String id);

    void del(String id);
}
