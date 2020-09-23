package com.xiaomi.info.r2session.spring;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import java.io.*;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/22 17:53
 */
public class JacksonSerializer implements Serializer<Object>, Deserializer<Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonSerializer() {
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, Object.class);
    }

    @Override
    public Object deserializeFromByteArray(byte[] serialized) {
        try {
            return deserialize(new ByteArrayInputStream(serialized));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        mapper.writeValue(outputStream, object);
    }

    @Override
    public byte[] serializeToByteArray(Object object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        try {
            serialize(object, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toByteArray();
    }

}
