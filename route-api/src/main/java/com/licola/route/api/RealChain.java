package com.licola.route.api;

import android.content.Context;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.exceptions.RouteEmptyResponseException;
import java.util.List;
import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class RealChain implements Chain {

  private Map<String, RouteMeta> routeMap;

  private Context context;
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  private RouteRequest request;
  private RouteResponse response;

  @Override
  public void onProcess() {
    onProcess(null);
  }

  private int index = 0;

  @Override
  public void onProcess(RouteResponse newResponse) {

    response = newResponse != null ? newResponse : response;

    if (response == null) {
      if (index < interceptors.size()) {
        interceptors.get(index++).intercept(this);
      } else {
        onBreak(new RouteEmptyResponseException("没有拦截器处理得到RouteResponse"));
      }
    } else {
      for (RouteInterceptor routeInterceptor : routeInterceptors) {
        if (routeInterceptor.intercept(this, response)) {
          return;
        }
      }
    }

  }

  @Override
  public void onBreak(final Throwable throwable) {
    for (RouteInterceptor routeInterceptor : routeInterceptors) {
      if (routeInterceptor.intercept(this, throwable)) {
        return;
      }
    }
  }

  public static Chain newChain(Map<String, RouteMeta> routeMap, Context context,
      List<Interceptor> interceptors,
      List<RouteInterceptor> routeInterceptors,
      RouteRequest request
  ) {
    return new RealChain(routeMap, context, interceptors, routeInterceptors, request);
  }

  RealChain(Map<String, RouteMeta> routeMap, Context context,
      List<Interceptor> interceptors,
      List<RouteInterceptor> routeInterceptors,
      RouteRequest request) {
    this.routeMap = routeMap;
    this.context = context;
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

  @Override
  public RouteRequest getRequest() {
    return request;
  }

  @Override
  public RouteResponse getResponse() {
    return response;
  }

  @Override
  public Chain clone() {
    return newChain(routeMap, context, interceptors, routeInterceptors, request);
  }
}
