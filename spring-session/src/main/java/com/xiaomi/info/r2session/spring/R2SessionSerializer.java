package com.xiaomi.info.r2session.spring;

import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/23 9:45
 */
abstract class R2SessionSerializer {

    abstract Optional<Object> deSerialize(@Nullable String value);

    abstract Optional<String> serialize(@Nullable Object value);

    public static R2SessionSerializer instance() {
        return new JacksonSerializerAdapter();
    }

    private static class JacksonSerializerAdapter extends R2SessionSerializer {

        private final JacksonSerializer serializer = new JacksonSerializer();

        @Override
        Optional<Object> deSerialize(String value) {
            return Optional.ofNullable(value).map(String::getBytes).map(serializer::deserializeFromByteArray);
        }

        @Override
        Optional<String> serialize(Object value) {
            return Optional.ofNullable(value).map(serializer::serializeToByteArray).map(String::new);
        }
    }
}
