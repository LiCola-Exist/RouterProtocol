package com.licola.model.routerprotocol.mvp;

import android.app.Activity;
import android.content.Intent;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public interface VPContract {

  interface View{

  }

  interface Presenter{

    void onNavigation();

    void onNavigationInterceptor();

    void onNavigation(Activity activity,Intent intent);
  }
}
