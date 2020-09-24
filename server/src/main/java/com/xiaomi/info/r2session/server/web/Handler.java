package com.xiaomi.info.r2session.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/21 11:06
 */
@Component
public class Handler {

    public static final String APP_ID = "appId";

    public static final String KEY_TEMPLATE = "r2session:{0}:{1}";

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private String getKey(ServerRequest request) {
        String id = request.pathVariable("id");
        String appId = request.headers().firstHeader(APP_ID);
        if (appId == null) {
            throw new IllegalArgumentException("header appId can't be null");
        }
        return MessageFormat.format(KEY_TEMPLATE, appId, id);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        String hashKey = request.pathVariable("key");
        return ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.opsForHash().get(getKey(request), hashKey).map(String.class::cast), String.class);
    }

    public Mono<ServerResponse> set(ServerRequest request) {
        String hashKey = request.pathVariable("key");
        Optional<String> value = request.queryParam("value");
        return value.map(s -> ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.opsForHash().put(getKey(request), hashKey, s), Boolean.class))
                .orElseGet(() -> badRequest().bodyValue("value can't be null"));
    }

    public Mono<ServerResponse> keys(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.opsForHash().keys(getKey(request))
                        .map(String::valueOf).collectList(), new ParameterizedTypeReference<List<String>>() {});
    }

    public Mono<ServerResponse> exist(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.hasKey(getKey(request)), Boolean.class);
    }

    public Mono<ServerResponse> expire(ServerRequest request) {
        Optional<Duration> ttl = request.queryParam("ttl").map(Long::parseLong).map(Duration::ofSeconds);
        return ttl.map(duration -> ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.expire(getKey(request), duration), Boolean.class))
                .orElseGet(() -> badRequest().bodyValue("ttl can't be null"));
    }

    public Mono<ServerResponse> del(ServerRequest request) {
        String key = getKey(request);
        Map<String, String> variables = request.pathVariables();
        if (variables.containsKey("key")) {
            return ok().contentType(APPLICATION_JSON).body(
                    reactiveStringRedisTemplate.opsForHash().remove(key, variables.get("key")), Boolean.class
            );
        } else {
            return ok().contentType(APPLICATION_JSON).body(
                    reactiveStringRedisTemplate.opsForHash().delete(key), Boolean.class
            );
        }
    }

}
