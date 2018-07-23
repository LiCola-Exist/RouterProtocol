package com.licola.model.routerprotocol;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.RouteUser;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.Api;
import com.licola.route.api.Chain;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteInterceptor;
import com.licola.route.api.RouteRequest;
import com.licola.route.api.RouteResponse;
import com.licola.route.api.RouterApiImpl.Builder;
import com.licola.route.api.exceptions.RouteBadRequestException;
import com.licola.route.api.exceptions.RouteBreakException;

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
        .addRouteRoot(new RouteApp.Route())
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
        .addRouteRoot(new RouteApp.Route())
        .build();

    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY), this,
        REQUEST_CODE);
  }

  /**
   * 使用自动生成的Api 友好使用 带注解提示的 路由使用
   */
  public void onNavigationWithApiClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .build();

    RouteApp.Api appApi = new RouteApp.Api(api);
    appApi.navigation(RouteApp.SECOND_ACTIVITY);//参数注解会提示输入规则 must be one of
  }

  /**
   * 示例注入第三方库的 路由使用
   */
  public void onNavigationThirdClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new MyRoute.Route())
        .build();

    api.navigation("other/third");
  }

  /**
   * 拦截器的使用示例
   */
  public void onNavigationInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())//注入app模块的路由
        .addRouteRoot(new RouteUser.Route())//注入module模块的路由
        .build();
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY),
        new Interceptor() {
          @Override
          public void intercept(final Chain chain) {
            LLogger.d();
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("拦截器的使用")
                .setMessage("假设要跳转的模块需要定位服务，并假设检测到定位服务未开启，点击设置跳转到定位服务设置")
                .setNeutralButton("设置", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    RouteRequest request = chain.getRequest();
                    request.redirectByIntent().setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    chain.onProcess();
                  }
                })
                .setNegativeButton("取消", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    chain.onBreak(new RouteBreakException("用户主动取消"));
                  }
                })
                .setPositiveButton("跳转", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    chain.onProcess();
                  }
                })
                .show();
          }

        });

  }

  public void onNavigationInterceptorArgClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .addInterceptors(new Interceptor() {
          @Override
          public void intercept(Chain chain) {
            LLogger.d("优先级较低 且需要根据添加顺序 开始添加附带参数");
            Intent intent = chain.getRequest().putExtra();
            intent.putExtra("key-build", "value-build");
            chain.onProcess();
          }
        })
        .build();

    api.navigation("app/second", new Interceptor() {
      @Override
      public void intercept(Chain chain) {
        LLogger.d("优先级最高的 随参数注入拦截器 开始添加附带参数");
        Intent intent = chain.getRequest().putExtra();
        intent.putExtra("key-api", "value-api");
        chain.onProcess();
      }
    });

  }


  public void onNavigationRouteInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .openDebugLog()
        .addRouteInterceptors(new RouteInterceptor() {
          @Override
          public boolean intercept(Chain chain, RouteResponse response) {
            LLogger.d(chain, response);
            return false;
          }

          @Override
          public boolean intercept(Chain chain, Throwable throwable) {
            LLogger.d(chain, throwable);
            if (throwable instanceof RouteBadRequestException) {
              Chain clone = chain.clone();
              RouteRequest request = clone.getRequest();
              request.redirectByPath("app/user/login");
              clone.onProcess();
            }
            return false;
          }
        })
        .build();

    api.navigation("app/third");

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LLogger.d(requestCode, resultCode, data);
  }
}
