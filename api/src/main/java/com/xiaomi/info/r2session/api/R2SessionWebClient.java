package com.xiaomi.info.r2session.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/21 18:47
 */
public class R2SessionWebClient implements R2SessionClient {

    private final String appId;

    private final WebClient webClient;

    public static final String APP_ID = "appId";

    /**
     *
     * @param baseUrl r2session Server地址
     * @param appId 小写字母和数字组成，下划线分隔多个单词
     */
    public R2SessionWebClient(String baseUrl, String appId) {
        if (!Objects.requireNonNull(appId).matches("^[a-z][a-z0-9_]*$")) {
            throw new IllegalArgumentException("appId只能由小写字母、数字、下划线组成");
        }
        this.appId = appId;
        webClient = WebClient.create(baseUrl);
    }

    @Override
    public Mono<String> get(String id, String key) {
        return webClient.get().uri("/get/{id}/{key}", id, key)
                .header(APP_ID, appId).retrieve().bodyToMono(String.class);
    }

    @Override
    public Mono<Boolean> set(String id, String key, String value) {
        return webClient.get().uri("/set/{id}/{key}?value={value}", id, key, value)
                .header(APP_ID, appId).retrieve().bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Set<String>> keys(String id) {
        return webClient.get().uri("/keys/{id}", id)
                .header(APP_ID, appId).retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                });
    }

    @Override
    public Mono<Boolean> del(String id) {
        return webClient.get().uri("/del/{id}", id)
                .header(APP_ID, appId).retrieve().bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Boolean> del(String id, String key) {
        return webClient.get().uri("/del/{id}/{key}", id, key)
                .header(APP_ID, appId).retrieve().bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Boolean> exist(String id) {
        return webClient.get().uri("/exist/{id}", id)
                .header(APP_ID, appId).retrieve().bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Boolean> expire(String id, Duration ttl) {
        return webClient.get().uri("/expire/{id}?ttl={ttl}", id, ttl.getSeconds())
                .header(APP_ID, appId).retrieve().bodyToMono(Boolean.class);
    }

    public BlockingSessionClient blockingClient() {
        return new BlockingSessionClientImpl(this);
    }

    private static class BlockingSessionClientImpl implements BlockingSessionClient {

        private final R2SessionClient client;

        private BlockingSessionClientImpl(R2SessionClient client) {
            this.client = client;
        }

        @Override
        public Set<String> keys(String id) {
            return client.keys(id).block();
        }

        @Override
        public boolean exist(String id) {
            return client.exist(id).block();
        }

        @Override
        public void del(String id) {
            client.del(id).block();
        }

        @Override
        public String get(String id, String key) {
            return client.get(id, key).block();
        }

        @Override
        public void set(String id, String key, String value) {
            client.set(id, key, value).block();
        }

        @Override
        public void del(String id, String key) {
            client.del(id, key).block();
        }

        @Override
        public void expire(String id, Duration ttl) {
            client.expire(id, ttl).block();
        }
    }

}
