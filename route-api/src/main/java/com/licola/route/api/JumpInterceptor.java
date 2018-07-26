package com.licola.route.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.exceptions.RouteBadRequestException;
import com.licola.route.api.exceptions.RouteConfigError;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 * 控制实际的跳转的 跳转拦截器
 */
public class JumpInterceptor implements Interceptor {

  @Override
  public void intercept(Chain chain) {
    RealChain realChain = (RealChain) chain;

    Map<String, RouteMeta> routeMap = realChain.getRouteMap();
    Context context = realChain.getContext();

    RouteRequest request = realChain.getRequest();
    if (request == null) {
      chain.onBreak(new RouteBadRequestException("RouteRequest == null"));
      return;
    }

    if (realChain.getResponse() != null) {
      chain.onProcess();
      return;
    }

    Intent intent = request.getIntent();
    int requestCode = request.getRequestCode();
    RouteResponse response;

    String requestPath = request.getOriginalPath();
    String redirectPath = request.getRedirectPath();

    if (isResolveNotDeclareIntent(context, intent)) {
      boolean isRedirect = !Utils.isEmpty(requestPath) || !Utils.isEmpty(redirectPath);
      response = RouteResponse.createNotDeclare(intent, requestCode, isRedirect);
    } else {
      if (Utils.isEmpty(routeMap)) {
        chain.onBreak(new RouteConfigError("路由配置错误 路由表为空"));
        return;
      }

      //优先使用 重定向路径
      String path = Utils.isEmpty(redirectPath) ? requestPath : redirectPath;

      if (Utils.isEmpty(path)) {
        chain.onBreak(new RouteBadRequestException("路由查表方式跳转 但是path为空 无法查表"));
        return;
      }

      RouteMeta meta = routeMap.get(path);
      if (meta == null || meta.getTarget() == null) {
        chain.onBreak(new RouteBadRequestException("路由表中没有查询到请求目标"));
        return;
      }

      Class<?> target = meta.getTarget();
      if (intent == null) {
        intent = new Intent();
        request.setIntent(intent);
      }
      intent.setClass(context, target);

      response = RouteResponse
          .createDeclare(intent, requestCode, path, meta, !Utils.isEmpty(redirectPath));
    }

    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, requestCode);
    } else if (context instanceof Application) {
      context.startActivity(intent);
    }

    chain.onProcess(response);

  }

  /**
   * 该Intent能否被PackageManager隐式解析
   * 如通过 setAction() 设置，使得能够被外部解析
   *
   * @return true:能够被隐式解析
   */
  private static boolean isResolveNotDeclareIntent(Context context, @Nullable Intent intent) {
    if (intent == null) {
      return false;
    }

    ResolveInfo resolveInfo = context.getPackageManager()
        .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return resolveInfo != null;
  }


}
