package com.licola.route.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.annotation.RoutePath;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public class JumpInterceptor implements Interceptor {

  @Override
  public RouteResponse intercept(RouteApi route, RouteResponse response) {

    Map<String, RouteMeta> routeMap = route.getRouteMap();
    if (routeMap.isEmpty()) {
      return RouteResponse.notifyFailed(response);
    }

    String target = response.getTarget();
    String module = response.getModule();
    RouteMeta meta = routeMap.get(RoutePath.makePath(module, target));
    if (meta == null) {
      return RouteResponse.notifyFailed(response);
    }

    Application application = route.getApplication();
    Class<?> metaTarget = meta.getTarget();
    Intent intent = new Intent(application, metaTarget);
    if (isEmptyResolveIntent(application, intent)) {
      return RouteResponse.notifyFailed(response);
    }
    application.startActivity(intent);

    if (response.getCode() == RouteCode.CODE_REDIRECT) {
      return RouteResponse
          .notifySuccessByRedirect(response, meta);
    }

    return RouteResponse.notifySuccess(response, meta);
  }

  private static boolean isEmptyResolveIntent(Context context, Intent intent) {
    if (intent == null || context == null) {
      return true;
    }
    //只有当检查出能够接受intent的对象不为空 返回true
    return intent.resolveActivity(context.getPackageManager()) == null;
  }
}
