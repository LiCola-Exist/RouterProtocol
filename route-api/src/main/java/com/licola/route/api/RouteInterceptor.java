package com.licola.route.api;

import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/7.
 * 对导航之后的拦截器
 */
public interface RouteInterceptor {

  /**
   *
   * @param meta 路由信息
   * @return true：拦截信息流 false：不拦截信息流，继续调用后续拦截器
   */
  boolean intercept(final RouteMeta meta);
}
