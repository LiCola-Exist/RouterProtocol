package com.licola.route.api;

/**
 * Created by LiCola on 2018/7/7.
 * 对导航之后的拦截器
 */
public interface RouteInterceptor {

  boolean onResponse(Chain chain, RouteResponse response);

  boolean onFailure(Chain chain, Throwable throwable);
}
