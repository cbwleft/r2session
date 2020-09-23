package com.xiaomi.info.r2session.spring;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/22 18:07
 */
class JacksonSerializerTest {

    private final JacksonSerializer jacksonSerializer = new JacksonSerializer();

    @Test
    public void shouldSerializeAsJson() {
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("testString", "1");
        testObject.put("testInt", 2);
        testObject.put("testDate", new Date());
        testObject.put("testArray", new String[]{"1"});
        String typedJson = new String(jacksonSerializer.serializeToByteArray(testObject));
        System.out.println(typedJson);
        DocumentContext jsonPath = JsonPath.parse(typedJson);
        assertEquals("1", jsonPath.read("$.testString"));
        assertEquals(2, jsonPath.<Integer>read("$.testInt"));
    }

    @Test
    public void shouldSerializeString() {
        String testString = "testString";
        String typedJson = new String(jacksonSerializer.serializeToByteArray(testString));
        System.out.println(typedJson);
        assertEquals("\"testString\"", typedJson);
    }

    @Test
    public void shouldDeserializeAsOriginalObject() {
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("testString", "1");
        testObject.put("testInt", 2);
        testObject.put("testDate", new Date());
        byte[] bytes = jacksonSerializer.serializeToByteArray(testObject);
        Object result = jacksonSerializer.deserializeFromByteArray(bytes);
        System.out.println(result);
        assertThat(result, equalTo(testObject));
    }

    @Test
    public void shouldDeserializeAsOriginalList() {
        List<String> list = Arrays.asList("1", "2", "3");
        byte[] bytes = jacksonSerializer.serializeToByteArray(list);
        Object result = jacksonSerializer.deserializeFromByteArray(bytes);
        System.out.println(result);
        assertThat(result, equalTo(list));
    }

}