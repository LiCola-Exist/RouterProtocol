package com.licola.route.api;

import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/7.
 * 对导航之后的拦截器
 */
public interface RouteInterceptor {

  boolean intercept(RouteMeta meta);
}
