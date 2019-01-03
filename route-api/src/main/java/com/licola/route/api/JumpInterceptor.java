package com.licola.route.api;

import android.content.Intent;
import android.os.Bundle;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.exceptions.RouteBadChainException;
import com.licola.route.api.exceptions.RouteBadRequestException;
import com.licola.route.api.exceptions.RouteConfigError;
import com.licola.route.api.source.Source;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5. 控制实际的跳转的 跳转拦截器
 */
public class JumpInterceptor implements Interceptor {

  @Override
  public void intercept(Chain chain) {
    if (!(chain instanceof RealChain)) {
      chain.onBreak(new RouteBadChainException("错误的Chain链对象调用"));
      return;
    }

    RealChain realChain = (RealChain) chain;

    Map<String, RouteMeta> routeMap = realChain.getRouteMap();

    RouteRequest request = realChain.getRequest();

    Intent intent = request.getIntent();
    int requestCode = request.getRequestCode();
    Bundle bundle = request.getBundle();

    String requestPath = request.getRoutePath();

    if (Utils.isEmpty(routeMap)) {
      chain.onBreak(new RouteConfigError("路由配置错误 路由表为空"));
      return;
    }

    RouteMeta meta = routeMap.get(requestPath);

    if (meta != null) {

      Source source = request.getSource();
      Class<?> target = meta.getTarget();

      intent.setClass(source.getContext(), target);

      RouteResponse response = RouteResponse.createRouteResponse(requestPath, meta);

      source.startActivity(intent, requestCode, bundle);

      realChain.setResponse(response);
    }

    chain.onProcess(request);

  }


}
