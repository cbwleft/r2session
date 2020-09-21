package com.xiaomi.info.r2session.test.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/20 23:04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockSessionControllerTest {

    private WebClient webClient;

    @BeforeEach
    private void setUp(@LocalServerPort int port) {
        webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    public void shouldReturnSameSessionId() {
        ClientResponse response = webClient.get().uri("/id").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        ClientResponse response2 = webClient.get().uri("/id")
                .cookies(toConsumer(response.cookies())).exchange().block();
        assertNotNull(response2);
        ResponseEntity<String> entity2 = response2.toEntity(String.class).block();
        assertTrue(response2.cookies().isEmpty());
        System.out.println(entity2);
        assertNotNull(entity2);
        assertEquals(entity.getBody(), entity2.getBody());
    }

    @Test
    public void shouldNotReturnSameSessionId() {
        ClientResponse response = webClient.get().uri("/id").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        ClientResponse response2 = webClient.get().uri("/id").exchange().block();
        assertNotNull(response2);
        ResponseEntity<String> entity2 = response2.toEntity(String.class).block();
        System.out.println(entity2);
        assertNotNull(entity2);
        assertNotEquals(entity.getBody(), entity2.getBody());
    }

    @Test
    public void shouldReturnNewSessionId() {
        ClientResponse response = webClient.get().uri("/id").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        ClientResponse invalidate = webClient.get().uri("/invalidate")
                .cookies(toConsumer(response.cookies())).exchange().block();
        System.out.println(Objects.requireNonNull(invalidate).toEntity(String.class).block());
        ClientResponse response2 = webClient.get().uri("/id")
                .cookies(toConsumer(response.cookies())).exchange().block();
        assertNotNull(response2);
        ResponseEntity<String> entity2 = response2.toEntity(String.class).block();
        System.out.println(entity2);
        assertNotNull(entity2);
        assertNotEquals(entity.getBody(), entity2.getBody());
    }

    @Test
    public void shouldBeNew() {
        ClientResponse response = webClient.get().uri("/isNew").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        assertTrue(Boolean.parseBoolean(entity.getBody()));
    }

    @Test
    public void shouldNotBeNew() {
        ClientResponse response = webClient.get().uri("/isNew").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        assertTrue(Boolean.parseBoolean(entity.getBody()));
        ClientResponse response2 = webClient.get().uri("/isNew")
                .cookies(toConsumer(response.cookies())).exchange().block();
        assertNotNull(response2);
        ResponseEntity<String> entity2 = response.toEntity(String.class).block();
        assertNotNull(entity2);
        System.out.println(entity2);
        assertFalse(Boolean.parseBoolean(entity2.getBody()));
    }

    @Test
    public void shouldReturnAttributeNames() {
        ClientResponse response = webClient.put().uri("/attribute/key1/value1").exchange().block();
        assertNotNull(response);
        ResponseEntity<String> entity = response.toEntity(String.class).block();
        assertNotNull(entity);
        System.out.println(entity);
        ClientResponse response2 = webClient.get().uri("/attributeNames")
                .cookies(toConsumer(response.cookies())).exchange().block();
        assertNotNull(response2);
        ResponseEntity<List<String>> entity2 = response2.toEntity(new ParameterizedTypeReference<List<String>>() {
        }).block();
        System.out.println(entity2);
        assertArrayEquals(new String[]{"key1"}, entity2.getBody().toArray(new String[0]));
    }

    private Consumer<MultiValueMap<String, String>> toConsumer(MultiValueMap<String, ResponseCookie> cookies) {
        return c -> cookies.toSingleValueMap().forEach((k, v) -> c.add(k, v.getValue()));
    }
}