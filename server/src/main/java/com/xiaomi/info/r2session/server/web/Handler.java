package com.xiaomi.info.r2session.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.*;
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

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public Mono<ServerResponse> get(ServerRequest request) {
        String id = request.pathVariable("id");
        String key = request.pathVariable("key");
        return ok().contentType(TEXT_PLAIN).body(
                reactiveStringRedisTemplate.opsForHash().get(id, key).map(String::valueOf), String.class);
    }

    public Mono<ServerResponse> set(ServerRequest request) {
        String id = request.pathVariable("id");
        String key = request.pathVariable("key");
        Optional<String> value = request.queryParam("value");
        return value.map(s -> ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.opsForHash().put(id, key, s), Boolean.class
        )).orElseGet(() -> ServerResponse.badRequest().bodyValue("value can't be null"));
    }

    public Mono<ServerResponse> keys(ServerRequest request) {
        String id = request.pathVariable("id");
        return ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.opsForHash().keys(id)
                        .map(String::valueOf).collectList(), new ParameterizedTypeReference<List<String>>() {});
    }

    public Mono<ServerResponse> exist(ServerRequest request) {
        String id = request.pathVariable("id");
        return ok().contentType(APPLICATION_JSON).body(
                reactiveStringRedisTemplate.hasKey(id), Boolean.class);
    }

    public Mono<ServerResponse> del(ServerRequest request) {
        String id = request.pathVariable("id");
        Map<String, String> variables = request.pathVariables();
        if (variables.containsKey("key")) {
            return ok().contentType(APPLICATION_JSON).body(
                    reactiveStringRedisTemplate.opsForHash().remove(id, variables.get("key")), Boolean.class
            );
        } else {
            return ok().contentType(APPLICATION_JSON).body(
                    reactiveStringRedisTemplate.opsForHash().delete(id), Boolean.class
            );
        }
    }

}
