package com.licola.route.api;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteResponse {

  public static RouteResponse buildProcess(String module, String target) {
    RouteResponse response = new RouteResponse();
    response.code = RouteCode.CODE_PROCESS;
    response.module = module;
    response.target = target;
    response.msg = "请求中";
    return response;
  }

  public static void notifyTarget(RouteResponse response, String target) {
    notifyTarget(response, response.getModule(), target);
  }

  public static void notifyTarget(RouteResponse response, String module,
      String target) {
    response.code = RouteCode.CODE_REDIRECT;
    response.module = module;
    response.target = target;
    response.msg = "重定向";
  }

  /**
   * 更改Response的 Intent
   *
   * @param response 每次方法调用 都会产生新的intent对象
   */
  public static Intent notifyIntent(RouteResponse response) {
    Intent intent = new Intent();
    response.intent = intent;
    response.code = RouteCode.CODE_REDIRECT;
    response.msg = "通过Intent重定向";
    return intent;
  }

  public static Intent putExtraIntent(RouteResponse response) {
    if (response.intent == null) {
      response.intent = new Intent();
    }
    response.msg = "给附加Intent的参数";
    return response.intent;
  }

  public static void notifyFailed(RouteResponse response, String msg) {
    response.code = RouteCode.CODE_FAILED;
    response.msg = msg;
  }

  public static void notifyError(RouteResponse response, String msg) {
    response.code = RouteCode.CODE_ERROR;
    response.msg = msg;
  }

  public static void notifySuccess(RouteResponse response, RouteMeta routeMeta) {
    response.code = response.getCode() == RouteCode.CODE_REDIRECT ? RouteCode.CODE_REDIRECT
        : RouteCode.CODE_SUCCESS;
    response.routeMeta = routeMeta;
    response.msg = "请求成功";
  }

  private String target;
  private String module;
  @Nullable
  private Intent intent;

  @RouteCode.Code
  private int code;
  private String msg;

  private RouteMeta routeMeta;


  private RouteResponse() {
  }

  Intent getIntent() {
    return intent;
  }

  void setIntent(@NonNull Intent intent){
    this.intent=intent;
  }
  public String getTarget() {
    return target;
  }

  public String getModule() {
    return module;
  }

  public RouteMeta getRouteMeta() {
    return routeMeta;
  }

  @RouteCode.Code
  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteResponse{");
    sb.append("target='").append(target).append('\'');
    sb.append(", module='").append(module).append('\'');
    sb.append(", intent=").append(intent);
    sb.append(", code=").append(code);
    sb.append(", msg='").append(msg).append('\'');
    sb.append(", routeMeta=").append(routeMeta);
    sb.append('}');
    return sb.toString();
  }
}
