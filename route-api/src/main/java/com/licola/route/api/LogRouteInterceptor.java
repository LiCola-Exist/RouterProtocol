package com.licola.route.api;

import android.util.Log;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class LogRouteInterceptor implements RouteInterceptor {

  private static final String TAG = "Route";

  @Override
  public boolean onResponse(Chain chain, RouteResponse response) {
    Log.i(TAG, "onResponse " + " request:" + chain.getRequest() + " response:" + response);
    return false;
  }

  @Override
  public boolean onFailure(Chain chain, Throwable throwable) {
    Log.e(TAG, "onFailure  " + " request:" + chain.getRequest(), throwable);
    return false;
  }
}
