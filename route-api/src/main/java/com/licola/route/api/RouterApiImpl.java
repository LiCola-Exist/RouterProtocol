package com.licola.route.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;
import com.licola.route.annotation.RouteMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouterApiImpl implements Api {

  @NonNull
  private Application application;
  @NonNull
  private Map<String, RouteMeta> routeMap;
  @NonNull
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  RouterApiImpl(Builder builder) {
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

  @Override
  public void navigation(String path) {
    navigation(path, null, RouteRequest.STANDARD_REQUEST_CODE, null);
  }

  @Override
  public void navigation(String path, Activity activity, int requestCode) {
    navigation(path, activity, requestCode, null);

  }

  @Override
  public void navigation(String path, Interceptor interceptor) {
    navigation(path, null, RouteRequest.STANDARD_REQUEST_CODE, interceptor);
  }

  @Override
  public void navigation(String path, Activity activity, int requestCode, Interceptor interceptor) {
    List<Interceptor> interceptorAll = new ArrayList<>();
    if (interceptor != null) {
      interceptorAll.add(interceptor);
    }
    interceptorAll.addAll(interceptors);

    Context context = activity != null ? activity : application;

    RouteRequest request = RouteRequest.create(requestCode, path);
    Chain chain = RealChain.newChain(routeMap, context, interceptorAll, routeInterceptors, request);

    chain.onProcess();
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

    public Builder addRouteRoot(RouteRoot routeRoot) {
      if (routeRoot == null) {
        throw new IllegalArgumentException("routeRout == null");
      }
      this.routeRoots.add(routeRoot);
      return this;
    }

    public Builder openDebugLog() {
      return addRouteInterceptors(new LogRouteInterceptor());
    }

    public Api build() {
      //添加实现跳转功能的拦截器
      this.interceptors.add(new JumpInterceptor());

      if (routeRoots.isEmpty()) {
        throw new IllegalArgumentException("routeRoots can not empty");
      }

      return new RouterApiImpl(this);
    }
  }
}
