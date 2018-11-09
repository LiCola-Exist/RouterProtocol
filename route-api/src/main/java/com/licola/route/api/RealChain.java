package com.licola.route.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.exceptions.RouteEmptyResponseException;
import java.util.List;
import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/23
 */
class RealChain implements Chain {

  private Map<String, RouteMeta> routeMap;

  private Context context;
  private Fragment fragment;
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  @NonNull
  private RouteRequest request;
  @Nullable
  private RouteResponse response;

  @Override
  public RouteResponse onProcess() {
    return onProcess(null);
  }

  private int index = 0;

  @Override
  public RouteResponse onProcess(RouteResponse newResponse) {

    response = newResponse != null ? newResponse : response;

    if (response == null) {
      if (index < interceptors.size()) {
        interceptors.get(index++).intercept(this);
      } else {
        onBreak(new RouteEmptyResponseException("没有拦截器处理得到RouteResponse"));
      }
    } else {
      if (routeInterceptors != null && !routeInterceptors.isEmpty()) {
        for (RouteInterceptor routeInterceptor : routeInterceptors) {
          if (routeInterceptor.onResponse(this, response)) {
            break;
          }
        }
      }
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

  static Chain newChain(Map<String, RouteMeta> routeMap,
      Context context,
      Fragment fragment,
      List<Interceptor> interceptors,
      List<RouteInterceptor> routeInterceptors,
      RouteRequest request
  ) {
    if (request == null) {
      throw new IllegalArgumentException("request==null");
    }
    return new RealChain(routeMap, context, fragment,interceptors, routeInterceptors, request);
  }

  private RealChain(Map<String, RouteMeta> routeMap,
      Context context,
      Fragment fragment,
      List<Interceptor> interceptors,
      List<RouteInterceptor> routeInterceptors,
      @NonNull RouteRequest request) {
    this.routeMap = routeMap;
    this.context = context;
    this.fragment=fragment;
    this.interceptors = interceptors;
    this.routeInterceptors = routeInterceptors;
    this.request = request;
  }

  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
  }

  public Context getContext() {
    return context;
  }

  @Nullable
  public Fragment getFragment() {
    return fragment;
  }

  @NonNull
  @Override
  public RouteRequest getRequest() {
    return request;
  }

  @Nullable
  @Override
  public RouteResponse getResponse() {
    return response;
  }

  @NonNull
  @Override
  public Chain clone() {
    return newChain(routeMap, context,fragment, interceptors, routeInterceptors, request);
  }
}
