package com.licola.route.api;

/**
 * Created by LiCola on 2018/7/7.
 * 对导航之后的拦截器
 */
public interface RouteInterceptor {

  /**
   *
   * @param response 能够跳转的路由响应信息
   * @return true：拦截信息流 false：不拦截信息流，继续调用后续拦截器
   */
  boolean intercept(final RouteResponse response);
}
