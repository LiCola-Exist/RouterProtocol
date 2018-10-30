package com.licola.route.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 路由链路
 * 设计参考OkHttp-Call和Chain
 * @author LiCola
 * @date 2018/7/23
 */
public interface Chain  {

  /**
   * 链路 默认处理
   * @return
   */
  @Nullable
  RouteResponse onProcess();

  /**
   * 使用Response响应体 处理链路
   * @param response
   * @return
   */
  @Nullable
  RouteResponse onProcess(RouteResponse response);

  /**
   * 使用异常 中断链路
   * @param throwable
   */
  void onBreak(Throwable throwable);

  @NonNull
  RouteRequest getRequest();

  @Nullable
  RouteResponse getResponse();

  @NonNull
  Chain clone();
}
