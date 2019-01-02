package com.licola.route.api;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * 路由对外暴露的api接口
 *
 * @author LiCola
 * @date 2018/7/18
 */
public interface Api {

  void navigation(String path);

  void navigation(Interceptor interceptor);

  void navigation(String path, Interceptor interceptor);

  void navigation(Activity activity, int requestCode, Interceptor interceptor);

  void navigation(Fragment fragment, int requestCode, Interceptor interceptor);

  void navigation(String path, Activity activity, int requestCode);

  void navigation(String path, Fragment fragment, int requestCode);

  void navigation(String path, Activity activity, int requestCode, Interceptor interceptor);

  void navigation(String path, Fragment fragment, int requestCode, Interceptor interceptor);

}
