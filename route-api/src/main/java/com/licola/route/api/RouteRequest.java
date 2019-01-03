package com.licola.route.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 路由请求体的封装
 *
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteRequest {

  public static final int STANDARD_REQUEST_CODE = Activity.RESULT_OK;

  private final int requestCode;
  @Nullable
  private final String routePath;
  @NonNull
  private final Intent intent;
  @Nullable
  private final Bundle bundle;

  private RouteRequest(Builder builder) {
    this.requestCode = builder.requestCode;
    this.routePath = builder.routePath;
    this.intent = builder.intent;
    this.bundle = builder.bundle;
  }

  public int getRequestCode() {
    return requestCode;
  }

  @Nullable
  public String getRoutePath() {
    return routePath;
  }

  @NonNull
  public Intent getIntent() {
    return intent;
  }

  @Nullable
  public Bundle getBundle() {
    return bundle;
  }

  public static class Builder {

    private int requestCode;
    private String routePath;
    private Intent intent;
    private Bundle bundle;

    public Builder(int requestCode, String routePath) {
      this.requestCode = requestCode;
      this.routePath = routePath;
      this.intent = new Intent();
    }

    public Builder(RouteRequest request) {
      this.requestCode = request.requestCode;
      this.routePath = request.routePath;
      this.intent = request.intent;
      this.bundle = request.bundle;
    }

    public Builder routePath(String routePath) {
      this.routePath = routePath;
      return this;
    }

    public Builder requestCode(int requestCode) {
      this.requestCode = requestCode;
      return this;
    }

    public Builder putIntent(@NonNull Bundle extras) {
      this.intent.putExtras(extras);
      return this;
    }

    public Builder putBundle(@NonNull Bundle extras) {
      if (this.bundle==null){
        this.bundle=new Bundle();
      }
      this.bundle.putAll(extras);
      return this;
    }

    public RouteRequest build() {
      return new RouteRequest(this);
    }
  }
}
