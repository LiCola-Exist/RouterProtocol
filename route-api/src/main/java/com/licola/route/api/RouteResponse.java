package com.licola.route.api;

import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;

/**
 * 路由响应体的封装
 *
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteResponse {

  @Nullable
  private String targetPath;
  @Nullable
  private RouteMeta meta;

  private boolean isRoute;

  /**
   * 是否成功导航
   *
   * @return true:响应体非空
   */
  public static boolean isSuccess(RouteResponse response) {
    return response != null;
  }

  /**
   * 是否路由表内跳转
   *
   * @return true:显式启动，有明确的targetPath和meta信息
   */
  public static boolean isRoute(RouteResponse response) {
    return isSuccess(response) && response.isRoute;
  }

  /**
   * 创建非显式导航结果 如通过Action隐式启动 这样的启动有多个目标或是外部应用， 所以创建的响应体不能明确targetPath（导航路径）和routeMeta（路由信息）故为空
   *
   * @return 通过隐式启动的结果
   */
  public static RouteResponse createRouteResponse(String targetPath, RouteMeta meta) {
    return new RouteResponse(targetPath, meta, true);
  }

  /**
   * 创建显式导航结果 即通过路由查表方式实现的导航 这样的启动有明确targetPath（导航目标）和routeMeta（路由信息）
   *
   * @return 创建显式导航结果
   */
  public static RouteResponse createResolveResponse() {
    return new RouteResponse(null, null, true);
  }

  RouteResponse(String targetPath,
      RouteMeta meta, boolean isRoute) {
    this.targetPath = targetPath;
    this.meta = meta;
    this.isRoute = isRoute;
  }

  @Nullable
  public String getTargetPath() {
    return targetPath;
  }

  @Nullable
  public RouteMeta getMeta() {
    return meta;
  }

  public boolean isRoute() {
    return isRoute;
  }
}
