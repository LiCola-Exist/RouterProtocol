package com.licola.route.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 路由链路 设计参考OkHttp-Call和Chain
 *
 * @author LiCola
 * @date 2018/7/23
 */
public interface Chain {

  /**
   * 通过Request 尝试处理Response 如果成功可以取出非空的RouteResponse， 否则返回null对象
   */
  @Nullable
  RouteResponse onProcess(RouteRequest request);

  /**
   * 使用异常 中断链路
   */
  void onBreak(Throwable throwable);

  @NonNull
  RouteRequest getRequest();

  @NonNull
  Chain clone();
}
