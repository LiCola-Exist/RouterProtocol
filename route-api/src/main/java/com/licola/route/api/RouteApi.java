package com.licola.route.api;

import static com.licola.route.api.Constants.ROUTE_METHOD_NAVIGATION;

import android.app.Application;
import android.support.annotation.NonNull;
import com.licola.route.annotation.RouteMeta;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteApi implements InvocationHandler {

  @NonNull
  private Map<String, RouteMeta> routeMap;
  private Application application;
  private List<Interceptor> interceptors;

  public static RouteApi build(Application application,
      RouteProtocol protocol) {
    return build(application, protocol, Collections.EMPTY_LIST);
  }

  public static RouteApi build(Application application,
      RouteProtocol protocol, Interceptor interceptor) {
    return build(application, protocol, Arrays.asList(interceptor));
  }

  public static RouteApi build(Application application,
      RouteProtocol protocol, List<Interceptor> interceptors) {
    RouteApi routeApi = new RouteApi(application);
    HashMap<String, RouteMeta> routeMetaHashMap = new HashMap<>();
    protocol.load(routeMetaHashMap);
    routeApi.routeMap = Collections.unmodifiableMap(routeMetaHashMap);

    ArrayList<Interceptor> allInterceptors = new ArrayList<>();
    if (!interceptors.isEmpty()) {
      allInterceptors.addAll(interceptors);
    }
    allInterceptors.add(new JumpInterceptor());
    routeApi.interceptors = Collections.unmodifiableList(allInterceptors);
    return routeApi;
  }

  public <T> T create(final Class<T> tClass) {
    Object proxyInstance = Proxy
        .newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, this);
    return tClass.cast(proxyInstance);
  }

  public RouteApi(Application application) {
    this.application = application;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    if (method.getName().equals(ROUTE_METHOD_NAVIGATION)) {

      if (args == null || args.length <= 0) {
        throw new IllegalArgumentException("参数为空");
      }

      String target = (String) args[0];
      Interceptor argInterceptor = null;
      if (args.length == 2) {
        Object arg = args[1];
        if (arg instanceof Interceptor) {
          argInterceptor = (Interceptor) arg;
        }
      }

      RouteResponse response = RouteResponse.buildProcess(target);

      //随方法注入的拦截器 优先
      if (argInterceptor != null) {
        response = argInterceptor.intercept(RouteApi.this, response);
        if (response.getCode() == RouteCode.CODE_FAILED) {
          return false;
        }
      }

      //依次遍历拦截器
      for (Interceptor interceptor : interceptors) {
        response = interceptor.intercept(RouteApi.this, response);
        if (response.getCode() == RouteCode.CODE_FAILED) {
          return false;
        }
      }

      return response.getCode()==RouteCode.CODE_SUCCESS;
    }

    return null;
  }

  @NonNull
  public Map<String, RouteMeta> getRouteMap() {
    return routeMap;
  }

  public Application getApplication() {
    return application;
  }
}
