package com.licola.route.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public interface Chain  {

  @Nullable
  RouteResponse onProcess();

  @Nullable
  RouteResponse onProcess(RouteResponse response);

  void onBreak(Throwable throwable);

  @NonNull
  RouteRequest getRequest();

  @Nullable
  RouteResponse getResponse();

  @NonNull
  Chain clone();
}
