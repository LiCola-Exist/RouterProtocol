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

  private Application application;
  @NonNull
  private Map<String, RouteMeta> routeMap;
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  RouteApi(Builder builder) {
    this.application = builder.application;
    this.interceptors = Collections.unmodifiableList(builder.interceptors);
    this.routeInterceptors = Collections.unmodifiableList(builder.routeInterceptors);

    HashMap<String, RouteMeta> map = new HashMap<>();
    for (RouteRoot routeRoot : builder.routeRoots) {
      routeRoot.load(map);
    }
    this.routeMap = Collections.unmodifiableMap(map);
  }

  @RouteCode.Code
  public int navigation(String route, String target) {
    return navigation(route, target, null);
  }

  @RouteCode.Code
  public int navigation(String module, String target, Interceptor interceptor) {

    RouteResponse response = RouteResponse.buildProcess(module, target);

    //随方法注入的拦截器 优先
    if (interceptor != null) {
      response = interceptor.intercept(RouteApi.this, response);
      if (!checkContinueDelivery(response)) {
        return response.getCode();
      }
    }

    //依次遍历拦截器
    for (Interceptor item : interceptors) {
      response = item.intercept(RouteApi.this, response);
      if (!checkContinueDelivery(response)) {
        return response.getCode();
      }
    }

    if (!routeInterceptors.isEmpty()) {
      for (RouteInterceptor routeInterceptor : routeInterceptors) {
        if (!routeInterceptor.intercept(response.getRouteMeta())) {
          break;
        }
      }
    }

    return response.getCode();
  }

  private static boolean checkContinueDelivery(RouteResponse response) {
    return response.getCode() != RouteCode.CODE_FAILED;
  }

  @NonNull
  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
  }

  public Application getApplication() {
    return application;
  }

  public static final class Builder {

    Application application;
    List<Interceptor> interceptors = new ArrayList<>();
    List<RouteInterceptor> routeInterceptors = new ArrayList<>();
    List<RouteRoot> routeRoots = new ArrayList<>();

    public Builder(Application application) {
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
      this.interceptors.add(new JumpInterceptor());

      if (routeRoots.isEmpty()) {
        throw new IllegalArgumentException("routeRoots can not empty");
      }

      return new RouteApi(this);
    }
  }
}
