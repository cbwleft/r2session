package com.xiaomi.info.r2session.test.controller;

import com.xiaomi.info.r2session.api.BlockingSessionClient;
import com.xiaomi.info.r2session.spring.R2Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/20 23:04
 */
@Disabled
@ActiveProfiles("redis")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisSessionControllerTest extends MockSessionControllerTest {

    @Autowired
    private BlockingSessionClient client;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    public void shouldPersistObject() {
        R2Session r2Session = new R2Session(client, "test");
        List<String> value = Arrays.asList("1", "2", "3");
        r2Session.setAttribute("key", value);
        Object result = r2Session.getAttribute("key");
        assertThat(value, equalTo(result));
    }

    @Test
    public void shouldExpire() throws InterruptedException {
        R2Session r2Session = new R2Session(client, "testExpire");
        r2Session.setMaxInactiveInterval(Duration.ofSeconds(1));
        r2Session.setLastAccessedTime(Instant.now());
        assertFalse(r2Session.isExpired());
        TimeUnit.SECONDS.sleep(1);
        assertTrue(r2Session.isExpired());
    }
}