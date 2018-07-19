package com.licola.route.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 * 控制实际的跳转的 跳转拦截器
 */
public class JumpInterceptor implements Interceptor {

  @Override
  public void intercept(Router router, RouteResponse response) {
    Map<String, RouteMeta> routeMap = router.getRouteMap();

    RouteMeta meta;
    Intent intent = response.getIntent();
    Context context = response.getContext();
    int requestCode = response.getRequestCode();

    if (isResolveIntent(context, intent)) {
      meta = RouteMeta.create(null, "external", "unknown");
    } else {

      if (intent == null) {
        intent = new Intent();
        response.setIntent(intent);
      }

      String path = response.getPath();
      if (routeMap.isEmpty()) {
        RouteResponse.notifyError(response, "路由配置错误 路由表为空");
        meta = null;
      }
      if (path == null || path.isEmpty()) {
        meta = null;
        RouteResponse.notifyFailed(response, "路由查表方式跳转 但是path路由为空 无法查表");
      } else {
        meta = routeMap.get(path);
        if (meta == null) {
          RouteResponse.notifyFailed(response, "没有发现请求目标");
        }
      }
    }

    if (meta != null && meta.getTarget() != null) {
      Class<?> target = meta.getTarget();
      intent.setClass(context, target);
    }

    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, requestCode);
    } else {
      context.startActivity(intent);
    }

    RouteResponse.notifySuccess(response, meta);

  }

  /**
   *
   */
  private static boolean isResolveIntent(Context context, @Nullable Intent intent) {
    return intent != null && intent.resolveActivity(context.getPackageManager()) != null;
  }


}
