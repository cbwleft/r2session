package com.xiaomi.info.r2session.test;

import com.xiaomi.info.R2IndexedSessionRepository;
import com.xiaomi.info.R2Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/19 13:52
 */
@EnableSpringHttpSession
@SpringBootApplication
public class Application {

    @Bean
    public SessionRepository<R2Session> sessionRepository() {
        return new R2IndexedSessionRepository(new MockR2SessionClient());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}