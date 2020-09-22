package com.xiaomi.info.r2session.test.controller;

import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/19 15:10
 */
@RestController
public class SessionController {

    @GetMapping("/")
    public Object index(HttpSession s) {
        return Session.of(s.getId(), s.getCreationTime(), s.getLastAccessedTime(), s.isNew());
    }

    @GetMapping("/id")
    public String id(HttpSession session) {
        return session.getId();
    }

    @GetMapping("/invalidate")
    public String invalidate(HttpSession session) {
        session.invalidate();
        return session.getId();
    }

    @GetMapping("/isNew")
    public boolean isNew(HttpSession session) {
        return session.isNew();
    }

    @PutMapping("/attribute/{key}")
    public String setAttribute(@PathVariable String key,
                               @RequestParam String value,
                               HttpSession session) {
        session.setAttribute(key, value);
        return session.getId();
    }

    @GetMapping("/attribute/{key}")
    public Object getAttribute(@PathVariable String key,
                               HttpSession session) {
        return session.getAttribute(key);
    }

    @GetMapping("/attributeNames")
    public Object getAttributeNames(HttpSession session) {
        return Collections.list(session.getAttributeNames());
    }

    @Value(staticConstructor = "of")
    private static class Session {
        String id;
        long creationTime;
        long lastAccessedTime;
        boolean isNew;
    }
}
