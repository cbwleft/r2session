package com.xiaomi.info.r2session.test.controller;

import com.xiaomi.info.r2session.api.BlockingSessionClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

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
        Session session = sessionRepository.createSession();
        List<String> value = Arrays.asList("1", "2", "3");
        session.setAttribute("key", value);
        Object result = session.getAttribute("key");
        assertThat(value, equalTo(result));
        sessionRepository.deleteById(session.getId());
    }

    @Test
    public void shouldExpire() throws InterruptedException {
        Session session = sessionRepository.createSession();
        session.setMaxInactiveInterval(Duration.ofSeconds(1));
        session.setLastAccessedTime(Instant.now());
        assertFalse(session.isExpired());
        TimeUnit.SECONDS.sleep(1);
        assertTrue(session.isExpired());
    }

    @Test
    public void shouldGetWhatISet() {
        Session session = sessionRepository.createSession();
        System.out.println(session.getId());
        Instant creationTime = session.getCreationTime();
        System.out.println(creationTime);
        assertNotNull(creationTime);
        session.setMaxInactiveInterval(Duration.ofSeconds(30));
        System.out.println(session.getMaxInactiveInterval());
        assertThat(session.getMaxInactiveInterval(), equalTo(Duration.ofSeconds(30)));
        Instant now = Instant.now();
        session.setLastAccessedTime(now);
        long millis = Duration.between(now, session.getLastAccessedTime()).toMillis();
        System.out.println(millis);
        assertTrue(millis < 1);
    }

}