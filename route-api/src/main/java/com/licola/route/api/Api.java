package com.licola.route.api;

/**
 * 路由对外暴露的api接口
 *
 * @author LiCola
 * @date 2018/7/18
 */
public interface Api {

  void navigation(String path);

  void navigation(String path, Interceptor interceptor);

  void navigation(RouteRequest request);

  void navigation(RouteRequest request,Interceptor interceptor);

  void navigation(Interceptor interceptor);

}
