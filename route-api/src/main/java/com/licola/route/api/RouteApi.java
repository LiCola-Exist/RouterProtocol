package com.licola.route.api;

import android.app.Application;
import android.support.annotation.NonNull;
import com.licola.route.annotation.RouteMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteApi {

  @NonNull
  private Application application;
  @NonNull
  private Map<String, RouteMeta> routeMap;
  @NonNull
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  RouteApi(Builder builder) {
    this.application = builder.application;
    this.interceptors = Collections.unmodifiableList(builder.interceptors);
    this.routeInterceptors = Collections.unmodifiableList(builder.routeInterceptors);
    this.routeMap = Collections.unmodifiableMap(loadRoute(builder.routeRoots));
  }

  @NonNull
  private HashMap<String, RouteMeta> loadRoute(List<RouteRoot> routeRoots) {
    HashMap<String, RouteMeta> map = new HashMap<>();
    for (RouteRoot routeRoot : routeRoots) {
      routeRoot.load(map);
    }
    return map;
  }

  @RouteCode.Code
  public int navigation(String route, String target) {
    return navigation(route, target, null);
  }

  @RouteCode.Code
  public int navigation(String module, String target, Interceptor interceptor) {

    //构造最开始的路由响应
    RouteResponse response = RouteResponse.buildProcess(module, target);

    List<Interceptor> interceptorAll = new ArrayList<>();
    if (interceptor != null) {
      interceptorAll.add(interceptor);
    }
    interceptorAll.addAll(interceptors);

    //依次遍历拦截器
    for (Interceptor item : interceptorAll) {
      int code = item.intercept(RouteApi.this, response);
      if (checkBreakDelivery(code)) {
        break;
      }
    }

    //依次遍历路由拦截器
    if (!routeInterceptors.isEmpty()) {
      for (RouteInterceptor routeInterceptor : routeInterceptors) {
        if (routeInterceptor.intercept(response)) {
          break;
        }
      }
    }

    return response.getCode();
  }

  /**
   * 判断路由响应能够继续传递
   *
   * @return true：继续传递 false：当前响应无法继续传递
   */
  private static boolean checkBreakDelivery(@RouteCode.Code int code) {
    return code == RouteCode.CODE_FAILED || code == RouteCode.CODE_ERROR;
  }

  @NonNull
  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
  }

  @NonNull
  public Application getApplication() {
    return application;
  }

  public static final class Builder {

    Application application;
    List<Interceptor> interceptors = new ArrayList<>();
    List<RouteInterceptor> routeInterceptors = new ArrayList<>();
    List<RouteRoot> routeRoots = new ArrayList<>();

    public Builder(Application application) {
      if (application == null) {
        throw new IllegalArgumentException("application == null");
      }
      this.application = application;
    }

    public Builder addInterceptors(Interceptor interceptor) {
      if (interceptor == null) {
        throw new IllegalArgumentException("interceptor == null");
      }
      this.interceptors.add(interceptor);
      return this;
    }

    public Builder addRouteInterceptors(RouteInterceptor routeInterceptor) {
      if (routeInterceptor == null) {
        throw new IllegalArgumentException("routeInterceptor == null");
      }
      this.routeInterceptors.add(routeInterceptor);
      return this;
    }

    public Builder addRouteRoots(RouteRoot routeRoot) {
      if (routeRoot == null) {
        throw new IllegalArgumentException("routeRout == null");
      }
      this.routeRoots.add(routeRoot);
      return this;
    }

    public RouteApi build() {
      //添加实现跳转功能的拦截器
      this.interceptors.add(new JumpInterceptor());

      if (routeRoots.isEmpty()) {
        throw new IllegalArgumentException("routeRoots can not empty");
      }

      return new RouteApi(this);
    }
  }
}
