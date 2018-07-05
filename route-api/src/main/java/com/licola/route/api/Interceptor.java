package com.licola.route.api;

/**
 * Created by LiCola on 2018/7/5.
 */
public interface Interceptor {

  RouteResponse intercept(RouteApi route, RouteResponse response);
}
