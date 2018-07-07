package com.licola.route.api;

import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/7.
 */
public interface RouteInterceptor {

  boolean intercept(RouteMeta meta);
}
