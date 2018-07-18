package com.licola.route.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.RouteCode.Code;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 * 控制实际的跳转的 跳转拦截器
 */
public class JumpInterceptor implements Interceptor {

  @Code
  @Override
  public int intercept(RouteApi route, RouteResponse response) {
    Application application = route.getApplication();
    Map<String, RouteMeta> routeMap = route.getRouteMap();

    RouteMeta meta;
    Intent intent = response.getIntent();
    if (isResolveIntent(application, intent)) {
      meta = RouteMeta.create(null, "external", "unknown");
    } else {

      if (intent == null) {
        intent = new Intent();
        response.setIntent(intent);
      }

      if (routeMap.isEmpty()) {
        RouteResponse.notifyError(response, "路由配置错误 路由表为空");
        meta = null;
      } else {
        String target = response.getTarget();
        String module = response.getModule();
        meta = routeMap.get(RoutePath.makePath(module, target));
        if (meta == null) {
          RouteResponse.notifyFailed(response, "没有发现请求目标");
        }
      }
    }

    if (meta == null) {
      return response.getCode();
    }

    Class<?> metaTarget = meta.getTarget();
    if (metaTarget != null) {
      intent.setClass(application, metaTarget);
    }

    application.startActivity(intent);

    RouteResponse.notifySuccess(response, meta);

    return response.getCode();
  }

  /**
   *
   */
  private static boolean isResolveIntent(Context context, @Nullable Intent intent) {
    return intent != null && intent.resolveActivity(context.getPackageManager()) != null;
  }
}
