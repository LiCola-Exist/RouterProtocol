package com.licola.route.api;

/**
 * @author LiCola
 * @date 2019/1/23
 */
public class AdapterRouteInterceptor implements RouteInterceptor {

  @Override
  public boolean onResponse(Chain chain, RouteResponse response) {
    return false;
  }

  @Override
  public boolean onFailure(Chain chain, Throwable throwable) {
    return false;
  }
}
