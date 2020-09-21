package com.xiaomi.info.r2session.test.config;

import com.xiaomi.info.r2session.api.R2SessionWebClient;
import com.xiaomi.info.r2session.spring.R2IndexedSessionRepository;
import com.xiaomi.info.r2session.spring.R2Session;
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

    @Bean
    public SessionRepository<R2Session> sessionRepository() {
        return new R2IndexedSessionRepository(
                new R2SessionWebClient("http://localhost:8080").blockingClient());
    }
}
