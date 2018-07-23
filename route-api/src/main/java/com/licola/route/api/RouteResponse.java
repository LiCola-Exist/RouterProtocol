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

  public static RouteResponse createNotDeclare(Intent intent, int requestCode, boolean isRedirect) {
    return new RouteResponse(intent, requestCode, null, null, isRedirect, false);
  }

  public static RouteResponse createDeclare(Intent intent, int requestCode, String targetPath,
      RouteMeta meta, boolean isRedirect) {
    return new RouteResponse(intent, requestCode, targetPath, meta, isRedirect, true);
  }


  public RouteResponse(Intent intent, int requestCode, String targetPath,
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
