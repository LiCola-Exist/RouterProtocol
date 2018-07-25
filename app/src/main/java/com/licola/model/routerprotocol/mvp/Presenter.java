package com.licola.model.routerprotocol.mvp;

import android.app.Activity;
import android.content.Intent;
import com.licola.model.routerprotocol.mvp.VPContract.View;
import com.licola.route.api.Api;
import com.licola.route.api.Chain;
import com.licola.route.api.Interceptor;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class Presenter implements VPContract.Presenter {

  private Api api;
  private View view;

  public Presenter(View view, Api api) {
    this.view = view;
    this.api = api;
  }

  @Override
  public void onNavigation() {
    api.navigation("app/second");
  }

  @Override
  public void onNavigationInterceptor() {
    api.navigation("app/second", new Interceptor() {
      @Override
      public void intercept(Chain chain) {
        chain.onProcess();
      }
    });
  }

  @Override
  public void onNavigation(Activity activity,Intent intent) {
    activity.startActivity(intent);
  }
}
