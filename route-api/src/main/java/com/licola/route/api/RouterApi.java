package com.licola.route.api;

import android.app.Application;
import android.support.annotation.NonNull;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.source.ApplicationSource;
import com.licola.route.api.source.Source;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public final class RouterApi implements Api {

  @NonNull
  private Source appSource;
  @NonNull
  private Map<String, RouteMeta> routeMap;
  @NonNull
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  private RouterApi(Builder builder) {
    this.appSource = builder.appSource;
    this.interceptors = Collections.unmodifiableList(builder.interceptors);
    this.routeInterceptors = Collections.unmodifiableList(builder.routeInterceptors);
    this.routeMap = Collections.unmodifiableMap(loadRoute(builder.routeRoots));
  }

  @Override
  public void navigation(String path) {
    RouteRequest request = new RouteRequest.Builder(appSource)
        .routePath(path)
        .build();
    process(request, null);
  }

  @Override
  public void navigation(Interceptor interceptor) {
    RouteRequest request = new RouteRequest.Builder(appSource)
        .build();
    process(request, interceptor);
  }

  @Override
  public void navigation(String path, Interceptor interceptor) {
    RouteRequest request = new RouteRequest.Builder(appSource)
        .routePath(path)
        .build();
    process(request, interceptor);
  }

  @Override
  public void navigation(RouteRequest request) {
    process(request, null);
  }

  @Override
  public void navigation(RouteRequest request, Interceptor interceptor) {
    process(request, interceptor);
  }

  private void process(RouteRequest request, Interceptor interceptor) {

    List<Interceptor> interceptorAll;
    if (interceptor != null) {
      interceptorAll = new ArrayList<>(interceptors.size() + 1);
      interceptorAll.add(interceptor);
      interceptorAll.addAll(interceptors);
    } else {
      interceptorAll = interceptors;
    }

    Chain chain = new RealChain(routeMap, interceptorAll, routeInterceptors, 0);

    chain.onProcess(request);
  }

  @NonNull
  private Map<String, RouteMeta> loadRoute(List<RouteRoot> routeRoots) {

    HashMap<String, RouteMeta> totalMap = new HashMap<>();

    for (RouteRoot routeRoot : routeRoots) {

      List<RouteMeta> metas = routeRoot.load();

      if (Utils.isEmpty(metas)) {
        continue;
      }

      for (RouteMeta meta : metas) {
        String key = meta.getPath();
        RouteMeta oldValue = totalMap.put(key, meta);
        if (oldValue != null) {
          throw new IllegalArgumentException(
              String.format(Locale.CHINA, "path=%s,被%s和%s重复定义", key, meta, oldValue));
        }
      }
    }
    return totalMap;
  }

  public static final class Builder {

    Source appSource;
    List<Interceptor> interceptors = new ArrayList<>();
    List<RouteInterceptor> routeInterceptors = new ArrayList<>();
    List<RouteRoot> routeRoots = new ArrayList<>();

    public Builder(Application application) {
      if (application == null) {
        throw new IllegalArgumentException("application == null");
      }
      this.appSource = new ApplicationSource(application);
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
      //开启插件 会自动扫描路由相关配置代码 插入类似代码
//      addRouteRoot(new com.licola.route.RouteApp$Route());

      //添加实现跳转功能的拦截器
      this.interceptors.add(new PackageInterceptor());
      this.interceptors.add(new MetaInterceptor());

      return new RouterApi(this);
    }
  }
}
