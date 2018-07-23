package com.licola.route.api;

/**
 * Created by LiCola on 2018/7/7.
 * 对导航之后的拦截器
 */
public interface RouteInterceptor {

  boolean intercept(Chain chain, RouteResponse response);

  boolean intercept(Chain chain, Throwable throwable);
}
