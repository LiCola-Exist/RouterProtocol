package com.licola.route.api;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public interface Chain  {

  RouteResponse onProcess();

  RouteResponse onProcess(RouteResponse response);

  void onBreak(Throwable throwable);

  RouteRequest getRequest();

  RouteResponse getResponse();

  Chain clone();
}
