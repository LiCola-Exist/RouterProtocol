package com.licola.route.api;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteResponse {

  @NonNull
  private Intent intent;
  private int requestCode;

  @Nullable
  private String targetPath;
  @Nullable
  private RouteMeta meta;

  private boolean isRedirect;

  private boolean isDeclare;

  /**
   * 是否成功导航
   *
   * @return true:响应体非空 且显式启动（应用内部） 且非重定向
   */
  public static boolean isSuccess(RouteResponse response) {
    return response != null && response.isDeclare && !response.isRedirect;
  }

  /**
   * 是否重定向结果
   *
   * @return true:重定向方式导航，即该RouteResponse的导航目标和RouteRequest导航目标不一致 false:其他情况
   */
  public static boolean isRedirect(RouteResponse response) {
    return response != null && response.isRedirect;
  }

  /**
   * 是否显式启动结果
   *
   * @return true:显式启动，有明确的targetPath和meta信息
   */
  public static boolean isDeclare(RouteResponse response) {
    return response != null && response.isDeclare;
  }

  /**
   * 创建非显式导航结果 如通过Action隐式启动
   * 这样的启动有多个目标或是外部应用，
   * 所以创建的响应体不能明确targetPath（导航路径）和routeMeta（路由信息）故为空
   *
   * @return 通过隐式启动的结果
   */
  public static RouteResponse createNotDeclare(Intent intent, int requestCode, boolean isRedirect) {
    return new RouteResponse(intent, requestCode, null, null, isRedirect, false);
  }

  /**
   * 创建显式导航结果 即通过路由查表方式实现的导航
   * 这样的启动有明确targetPath（导航目标）和routeMeta（路由信息）
   *
   * @return 创建显式导航结果
   */
  public static RouteResponse createDeclare(Intent intent, int requestCode, String targetPath,
      RouteMeta routeMeta, boolean isRedirect) {
    return new RouteResponse(intent, requestCode, targetPath, routeMeta, isRedirect, true);
  }


  RouteResponse(Intent intent, int requestCode, String targetPath,
      RouteMeta meta, boolean isRedirect, boolean isDeclare) {
    this.intent = intent;
    this.requestCode = requestCode;
    this.targetPath = targetPath;
    this.meta = meta;
    this.isRedirect = isRedirect;
    this.isDeclare = isDeclare;
  }

  @NonNull
  public Intent getIntent() {
    return intent;
  }

  public int getRequestCode() {
    return requestCode;
  }

  @Nullable
  public String getTargetPath() {
    return targetPath;
  }

  @Nullable
  public RouteMeta getMeta() {
    return meta;
  }

  public boolean isRedirect() {
    return isRedirect;
  }

  public boolean isDeclare() {
    return isDeclare;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteResponse{");
    sb.append("intent=").append(intent);
    sb.append(", requestCode=").append(requestCode);
    sb.append(", targetPath='").append(targetPath).append('\'');
    sb.append(", meta=").append(meta);
    sb.append(", isRedirect=").append(isRedirect);
    sb.append(", isDeclare=").append(isDeclare);
    sb.append('}');
    return sb.toString();
  }
}
