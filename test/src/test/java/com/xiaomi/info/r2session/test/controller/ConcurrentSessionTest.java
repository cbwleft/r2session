package com.xiaomi.info.r2session.test.controller;

import com.xiaomi.info.r2session.api.R2SessionClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/25 16:38
 */
@Disabled
@ActiveProfiles("redis")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcurrentSessionTest {

    @Autowired
    private R2SessionClient client;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    public void testCreateSession() throws InterruptedException {
        int total = 10000;
        int threads = 100;
        long expectedSPS = 700;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Instant begin = Instant.now();
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j< total / threads; j++) {
                    Session session = sessionRepository.createSession();
                }
                latch.countDown();
            });
        }
        latch.await();
        Duration duration = Duration.between(begin, Instant.now());
        long sessionPerSecond = 10000 / duration.toSeconds();
        System.out.println(sessionPerSecond);
        assertThat(sessionPerSecond, greaterThan(expectedSPS));
    }

    @Test
    public void testSetAndGet() throws InterruptedException {
        int sessions = 100;
        int threads = 100;
        int expectedQPS = 1000;
        AtomicInteger operations = new AtomicInteger();
        List<Session> sessionList = IntStream.range(0, sessions)
                .mapToObj((i) -> sessionRepository.createSession()).collect(toList());
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        sessionList.forEach(session -> executorService.submit(() -> {
            while (true) {
                session.setLastAccessedTime(Instant.now());
                operations.incrementAndGet();
                session.setAttribute("test", "value");
                operations.incrementAndGet();
                session.getAttribute("test");
                operations.incrementAndGet();
                session.removeAttribute("test");
                operations.incrementAndGet();
                session.isExpired();
                operations.incrementAndGet();
            }
        }));
        TimeUnit.SECONDS.sleep(10);
        executorService.shutdownNow();
        int qps = operations.get() / 10;
        System.out.println(qps);
        assertThat(qps, greaterThan(expectedQPS));
    }

    @Test
    public void testSet() throws InterruptedException {
        int operations = 10000;
        int expectedQPS = 1000;
        Instant begin = Instant.now();
        CountDownLatch latch = new CountDownLatch(operations);
        IntStream.range(0, operations).parallel().forEach((i) -> {
            Mono<Boolean> set = client.set("testSet", "key", "testValue");
            set.subscribe((b) -> latch.countDown());
        });
        latch.await();
        Duration duration = Duration.between(begin, Instant.now());
        int qps = (int) (operations * 1000 / duration.toMillis());
        System.out.println(qps);
        assertThat(qps, greaterThan(expectedQPS));
    }

}
