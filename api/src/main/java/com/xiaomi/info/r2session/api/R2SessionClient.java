package com.xiaomi.info.r2session.api;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/21 19:51
 */
public interface R2SessionClient {

    Mono<String> get(String id, String key);

    Mono<Boolean> set(String id, String key, String value);

    Mono<Boolean> del(String id, String key);

    Mono<Boolean> del(String id);

    Mono<Set<String>> keys(String id);

    Mono<Boolean> exist(String id);

    Mono<Boolean> expire(String id, Duration ttl);
}
