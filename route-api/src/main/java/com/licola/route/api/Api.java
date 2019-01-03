package com.licola.route.api;

/**
 * 路由对外暴露的api接口
 *
 * @author LiCola
 * @date 2018/7/18
 */
public interface Api {

  void navigation(String path);

  void navigation(Interceptor interceptor);

  void navigation(String path, Interceptor interceptor);

  void navigation(RouteRequest request);

}
