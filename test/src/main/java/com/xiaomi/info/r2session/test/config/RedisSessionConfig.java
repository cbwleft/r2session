package com.xiaomi.info.r2session.test.config;

import com.xiaomi.info.r2session.api.BlockingSessionClient;
import com.xiaomi.info.r2session.api.R2SessionWebClient;
import com.xiaomi.info.r2session.spring.R2IndexedSessionRepository;
import com.xiaomi.info.r2session.spring.R2Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.SessionRepository;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/21 21:10
 */
@Configuration
@Profile("redis")
public class RedisSessionConfig {

    @Value("${r2session.base-url}")
    private String baseUrl;

    @Bean
    public BlockingSessionClient blockingSessionClient() {
        return new R2SessionWebClient(baseUrl).blockingClient();
    }

    @Bean
    public SessionRepository<R2Session> sessionRepository(BlockingSessionClient client) {
        return new R2IndexedSessionRepository(client);
    }
}
