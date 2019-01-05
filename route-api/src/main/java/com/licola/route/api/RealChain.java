package com.licola.route.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.exceptions.RouteBadRequestException;
import java.util.List;
import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/23
 */
final class RealChain implements Chain {

  private final Map<String, RouteMeta> routeMap;

  private final List<Interceptor> interceptors;
  private final List<RouteInterceptor> routeInterceptors;

  private int index;

  @NonNull
  private RouteRequest request;

  private RouteResponse response;

  public void setResponse(RouteResponse response) {
    this.response = response;
  }

  @Nullable
  @Override
  public RouteResponse onProcess(RouteRequest request) {

    this.request = request;

    if (response != null) {
      //响应非空 表示成功 分发响应结果
      for (RouteInterceptor routeInterceptor : routeInterceptors) {
        if (routeInterceptor.onResponse(this, response)) {
          break;
        }
      }
      return response;
    }

    if (index < interceptors.size()) {
      Interceptor interceptor = interceptors.get(index++);
      interceptor.intercept(this);
    } else {
      onBreak(new RouteBadRequestException("没有拦截器处理得到RouteResponse"));
    }

    return response;
  }

  @Override
  public void onBreak(final Throwable throwable) {
    for (RouteInterceptor routeInterceptor : routeInterceptors) {
      if (routeInterceptor.onFailure(this, throwable)) {
        return;
      }
    }
  }

  RealChain(Map<String, RouteMeta> routeMap,
      List<Interceptor> interceptors,
      List<RouteInterceptor> routeInterceptors,
      int index
  ) {
    this.routeMap = routeMap;
    this.interceptors = interceptors;
    this.routeInterceptors = routeInterceptors;
    this.index = index;
  }

  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
  }

  @NonNull
  @Override
  public RouteRequest getRequest() {
    return request;
  }

  @NonNull
  @Override
  public Chain clone() {
    return new RealChain(routeMap, interceptors, routeInterceptors, 0);
  }
}
