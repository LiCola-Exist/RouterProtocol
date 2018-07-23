package com.licola.route.api;

import android.util.Log;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class LogRouteInterceptor implements RouteInterceptor {

  private static final String TAG = "Route";

  @Override
  public boolean intercept(Chain chain, RouteResponse response) {
    Log.i(TAG, "success navigation" + " request:" + chain.getRequest() + " response:" + response);
    return false;
  }

  @Override
  public boolean intercept(Chain chain, Throwable throwable) {
    Log.e(TAG, "fail navigation " + " request:" + chain.getRequest(), throwable);
    return false;
  }
}
