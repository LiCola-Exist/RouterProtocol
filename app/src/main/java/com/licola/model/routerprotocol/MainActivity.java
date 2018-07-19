package com.licola.model.routerprotocol;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.RouteUser;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.Api;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteResponse;
import com.licola.route.api.Router;
import com.licola.route.api.RouterApiImpl.Builder;

@Route(name = "main")
public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CODE = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

  }

  /**
   * 路由的简单使用
   */
  public void onNavigationSimpleClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();
    //以下两行代码 效果一样
//    api.navigation("app/second");
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY));//常量导航
  }

  /**
   * 带RequestCode的路由请求
   */
  public void onNavigationRequestCodeClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();

    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY), this,
        REQUEST_CODE);
  }

  /**
   * 使用自动生成的Api 友好使用 带注解提示的 路由使用
   */
  public void onNavigationWithApiClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();

    RouteApp.Api appApi = new RouteApp.Api(api);
    appApi.navigation(RouteApp.SECOND_ACTIVITY);//参数注解会提示输入规则 must be one of
  }

  /**
   * 示例注入第三方库的 路由使用
   */
  public void onNavigationThirdClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoots(new MyRoute.Route())
        .build();

    api.navigation("other/third");
  }

  /**
   * 拦截器的使用示例
   */
  public void onNavigationInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())//注入app模块的路由
        .addRouteRoots(new RouteUser.Route())//注入module模块的路由
        .build();
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY),
        new Interceptor() {
          @Override
          public void intercept(Router router, final RouteResponse response) {
            LLogger.d();
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("将要发生界面跳转")
                .setNegativeButton("取消", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    response.onBreak();
                  }
                })
                .setNeutralButton("跳转", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    response.onProcess();

                  }
                })
                .show();
          }
        });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LLogger.d(requestCode, resultCode, data);
  }
}
