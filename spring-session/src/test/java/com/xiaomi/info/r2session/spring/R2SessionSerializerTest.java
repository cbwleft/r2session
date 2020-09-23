package com.xiaomi.info.r2session.spring;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/23 10:45
 */
class R2SessionSerializerTest {

    @Test
    public void shouldReturnEmpty() {
        assertThat(R2SessionSerializer.instance().deSerialize(null), equalTo(Optional.empty()));
        assertThat(R2SessionSerializer.instance().serialize(null), equalTo(Optional.empty()));
    }

    @Test
    public void shouldNotReturnEmpty() {
        assertTrue(R2SessionSerializer.instance().serialize(new byte[0]).isPresent());
        assertTrue(R2SessionSerializer.instance().deSerialize("0").isPresent());
    }

}