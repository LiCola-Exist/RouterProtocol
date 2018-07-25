package com.licola.route.api;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteRequest {

  public static final int STANDARD_REQUEST_CODE = Activity.RESULT_OK;

  private int requestCode;
  private String path;
  @Nullable
  private String redirectPath;
  @Nullable
  private Intent intent;

  public static RouteRequest create(int requestCode,
      String path){
    return new RouteRequest(requestCode,path);
  }

  private RouteRequest(int requestCode,
      String path) {
    this.requestCode = requestCode;
    this.path = path;
  }

  /**
   * 通过该方法 得到Intent对象 可以添加附加参数
   * @return
   */
  public Intent putExtra(){
    if (this.intent == null) {
      this.intent = new Intent();
    }
    return intent;
  }

  public boolean redirectByPath(String redirectPath){
    if (redirectPath==null){
      return false;
    }

    if (path.equals(redirectPath)){
      return false;
    }

    this.redirectPath=redirectPath;
    return true;
  }

  public Intent redirectByIntent(){
    if (this.intent == null) {
      this.intent = new Intent();
    }
    return intent;
  }

  public String getPath() {
    return path;
  }

  @Nullable
  public String getRedirectPath() {
    return redirectPath;
  }

  @Nullable
  Intent getIntent() {
    return intent;
  }

  void setIntent(@NonNull Intent intent) {
    this.intent = intent;
  }

  public int getRequestCode() {
    return requestCode;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteRequest{");
    sb.append("requestCode=").append(requestCode);
    sb.append(", path='").append(path).append('\'');
    sb.append(", redirectPath='").append(redirectPath).append('\'');
    sb.append(", intent=").append(intent);
    sb.append('}');
    return sb.toString();
  }
}
