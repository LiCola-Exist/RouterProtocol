package com.licola.route.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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
public class RouterApiImpl implements Router, Api {

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
    navigation(path, null, RouteResponse.STANDARD_REQUEST_CODE, null);
  }

  @Override
  public void navigation(String path, Activity activity, int requestCode) {
    navigation(path, activity, requestCode, null);

  }

  @Override
  public void navigation(String path, Interceptor interceptor) {
    navigation(path, null, RouteResponse.STANDARD_REQUEST_CODE, interceptor);
  }

  @Override
  public void navigation(String path, Activity activity, int requestCode, Interceptor interceptor) {
    List<Interceptor> interceptorAll = new ArrayList<>();
    if (interceptor != null) {
      interceptorAll.add(interceptor);
    }
    interceptorAll.addAll(interceptors);

    Context context = activity != null ? activity : application;

    //构造路由响应
    RouteResponse response = new RouteResponse(this, context, requestCode, interceptorAll, path);

    //开始
    response.onProcess();
  }


  /**
   * 判断路由响应能够继续传递
   *
   * @return true：继续传递 false：当前响应无法继续传递
   */
  private static boolean checkDelivery(@RouteCode.Code int code) {
    return code == RouteCode.CODE_PROCESS || code == RouteCode.CODE_REDIRECT;
  }

  /**
   * 检查是否成功跳转 严格的检查
   *
   * @return true：只有请求目标和导航目标一致 才认为成功 false：其他情况
   */
  public static boolean checkNavigation(@RouteCode.Code int code) {
    return code == RouteCode.CODE_SUCCESS;
  }

  /**
   * 检查是否跳转页面 宽松的检查 因为重定向也会导致页面跳转
   *
   * @return true：只要界面跳转就认为成功 false：其他情况
   */
  public static boolean checkNavigationLoose(@RouteCode.Code int code) {
    return code == RouteCode.CODE_SUCCESS || code == RouteCode.CODE_REDIRECT;
  }

  @NonNull
  @Override
  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
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
