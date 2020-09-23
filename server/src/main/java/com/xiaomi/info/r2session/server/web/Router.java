package com.xiaomi.info.r2session.server.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Copyright (c) 2020 XiaoMi Inc.All Rights Reserved.
 * Description:
 *
 * @author cuibowen@xiaomi.com
 * Date:2020/9/21 15:40
 */
@Configuration
public class Router {

    @Bean
    public RouterFunction<ServerResponse> route(Handler handler) {
        return RouterFunctions
                .route()
                .GET("/get/{id}/{key}", accept(APPLICATION_JSON), handler::get)
                .GET("/set/{id}/{key}", accept(APPLICATION_JSON), handler::set)
                .GET("/del/{id}/{key}", accept(APPLICATION_JSON), handler::del)
                .GET("/del/{id}", accept(APPLICATION_JSON), handler::del)
                .GET("/keys/{id}", accept(APPLICATION_JSON), handler::keys)
                .GET("/exist/{id}", accept(APPLICATION_JSON), handler::exist)
                .GET("/expire/{id}", accept(APPLICATION_JSON), handler::expire)
                .build();
    }

}
