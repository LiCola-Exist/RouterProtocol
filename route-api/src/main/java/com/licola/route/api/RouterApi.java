package com.licola.route.api;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.source.ApplicationSource;
import com.licola.route.api.source.FragmentSource;
import com.licola.route.api.source.Source;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouterApi implements Api {

  @NonNull
  private Application application;
  @NonNull
  private Map<String, RouteMeta> routeMap;
  @NonNull
  private List<Interceptor> interceptors;
  private List<RouteInterceptor> routeInterceptors;

  RouterApi(Builder builder) {
    this.application = builder.application;
    this.interceptors = Collections.unmodifiableList(builder.interceptors);
    this.routeInterceptors = Collections.unmodifiableList(builder.routeInterceptors);
    this.routeMap = Collections.unmodifiableMap(loadRoute(builder.routeRoots));
  }

  @Override
  public void navigation(String path) {
    process(path, null, null, RouteRequest.STANDARD_REQUEST_CODE, null);
  }

  @Override
  public void navigation(String path, FragmentActivity activity, int requestCode) {
    process(path, activity, null, requestCode, null);
  }

  @Override
  public void navigation(String path, Fragment fragment, int requestCode) {
    process(path, null, fragment, requestCode, null);
  }

  @Override
  public void navigation(String path, Interceptor interceptor) {
    process(path, null, null, RouteRequest.STANDARD_REQUEST_CODE, interceptor);
  }

  @Override
  public void navigation(Interceptor interceptor) {
    process(null, null, null, RouteRequest.STANDARD_REQUEST_CODE, interceptor);
  }

  @Override
  public void navigation(FragmentActivity activity, int requestCode, Interceptor interceptor) {
    process(null, activity, null, requestCode, interceptor);
  }

  @Override
  public void navigation(Fragment fragment, int requestCode, Interceptor interceptor) {
    process(null, null, fragment, requestCode, interceptor);
  }

  @Override
  public void navigation(String path, FragmentActivity activity, int requestCode,
      Interceptor interceptor) {
    process(path, activity, null, requestCode, interceptor);
  }

  @Override
  public void navigation(String path, Fragment fragment, int requestCode, Interceptor interceptor) {
    process(path, null, fragment, requestCode, interceptor);
  }

  @NonNull
  private Map<String, RouteMeta> loadRoute(List<RouteRoot> routeRoots) {
    HashMap<String, RouteMeta> totalMap = new HashMap<>();
    for (RouteRoot routeRoot : routeRoots) {
      Map<String, RouteMeta> routeMetaMap = routeRoot.load();
      if (Utils.isEmpty(routeMetaMap)) {
        continue;
      }

      for (Entry<String, RouteMeta> entry : routeMetaMap.entrySet()) {
        String key = entry.getKey();
        RouteMeta newValue = entry.getValue();
        RouteMeta oldValue = totalMap.put(key, newValue);
        if (oldValue != null) {
          throw new IllegalArgumentException(
              String.format(Locale.CHINA, "path=%s,被%s和%s重复定义", key, newValue, oldValue));
        }
      }
    }
    return totalMap;
  }

  private Source createSource(FragmentActivity activity, Fragment fragment) {

    if (activity != null) {
      return new FragmentSource(activity, fragment);
    } else if (fragment != null) {
      return new FragmentSource(fragment.getActivity(), fragment);
    }
    return new ApplicationSource(application);
  }

  private void process(String path, FragmentActivity activity, Fragment fragment, int requestCode,
      Interceptor interceptor) {
    if (Utils.isEmpty(path) && interceptor == null) {
      throw new IllegalArgumentException(
          "path and interceptor cannot be null/empty at the same time ");
    }

    int size = interceptors.size() + 1;
    List<Interceptor> interceptorAll = new ArrayList<>(size);
    if (interceptor != null) {
      interceptorAll.add(interceptor);
    }
    interceptorAll.addAll(interceptors);

    Source source = createSource(activity, fragment);

    RouteRequest request = RouteRequest.create(requestCode, path);
    Chain chain = RealChain.newChain(routeMap, source, interceptorAll, routeInterceptors, request);

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

      return new RouterApi(this);
    }
  }
}
