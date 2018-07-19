package com.licola.route.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.licola.route.annotation.RouteMeta;
import java.util.List;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteResponse {

  public static final int STANDARD_REQUEST_CODE = Activity.RESULT_OK;

  public static void notifyTarget(RouteResponse response, String module,
      String target) {
//    response.code = RouteCode.CODE_REDIRECT;
//    response.module = module;
//    response.target = target;
//    response.msg = "重定向";
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
//    response.code = response.getCode() == RouteCode.CODE_REDIRECT ? RouteCode.CODE_REDIRECT
//        : RouteCode.CODE_SUCCESS;
//    response.routeMeta = routeMeta;
//    response.msg = "请求成功";
  }

  private Router router;
  private List<Interceptor> interceptors;
  private Context context;
  private int requestCode;

  private String path;
  @Nullable
  private Intent intent;

  @RouteCode.Code
  private int code;
  private String msg;

  private RouteMeta routeMeta;

  RouteResponse(Router router,Context context,int requestCode,List<Interceptor> interceptors, String path) {
    this.router = router;
    this.context=context;
    this.requestCode=requestCode;
    this.interceptors = interceptors;
    this.path=path;
    this.code=RouteCode.CODE_PROCESS;
    this.msg="开始请求";
  }

  private int index=0;

  public void onProcess() {

    if (index<interceptors.size()){
      interceptors.get(index++).intercept(router,this);
    }else {

    }
  }

  public Context getContext() {
    return context;
  }

  public int getRequestCode() {
    return requestCode;
  }

  public String getPath() {
    return path;
  }

  Intent getIntent() {
    return intent;
  }

  void setIntent(@NonNull Intent intent) {
    this.intent = intent;
  }

  public void onBreak() {

  }
}
