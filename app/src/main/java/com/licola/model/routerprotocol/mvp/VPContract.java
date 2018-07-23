package com.licola.model.routerprotocol.mvp;

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
  }
}
