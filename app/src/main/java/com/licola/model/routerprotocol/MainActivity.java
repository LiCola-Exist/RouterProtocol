package com.licola.model.routerprotocol;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
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
import com.licola.route.api.RouterApi.Builder;
import com.licola.route.api.exceptions.RouteBadRequestException;
import com.licola.route.api.exceptions.RouteBreakException;

@Route(path = "main")
public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CODE = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_nest);
    LLogger.d(this, savedInstanceState, fragment);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LLogger.d(this);
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
  public void onNavigationUseProtocolClick(View view) {
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
        .addRouteRoot(new RouteApp.Route())
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
        .addInterceptors(new Interceptor() {
          @Override
          public void intercept(Chain chain) {
            RouteResponse routeResponse = chain.onProcess();
            LLogger.d(
                "是否成功跳转:" + RouteResponse.isSuccess(routeResponse),
                "是否路由表显式跳转：" + RouteResponse.isDeclare(routeResponse),
                "是否重定向：" + RouteResponse.isRedirect(routeResponse)
            );
          }
        })
        .openDebugLog()
        .build();
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY),
        new Interceptor() {
          @Override
          public void intercept(final Chain chain) {
            LLogger.d();
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("拦截器的使用")
                .setMessage("假设要跳转的模块需要定位服务，并假设检测到定位服务未开启，点击设置跳转到系统设置-定位服务，点击重定向转到其他页面（如说明页面）")
                .setNeutralButton("设置", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    RouteRequest request = chain.getRequest();
                    request.notifyIntent().setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    RouteResponse response = chain.onProcess();
                    LLogger.d(response);
                  }
                })
                .setNegativeButton("取消", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    chain.onBreak(new RouteBreakException("用户主动取消"));
                  }
                })
                .setPositiveButton("重定向", new OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    RouteRequest request = chain.getRequest();
                    request.notifyPath(
                        RoutePath.makePath(RouteUser.MODULE_NAME, RouteUser.REGISTER_ACTIVITY));
                    RouteResponse response = chain.onProcess();
                    LLogger.d(response);
                  }
                })
                .show();

          }

        });

  }

  public void onNavigationInterceptorArgClick(final View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .addInterceptors(new Interceptor() {
          @Override
          public void intercept(final Chain chain) {
            LLogger.d("优先级较低 且需要根据添加顺序调用 开始添加附带参数");
            Intent intent = chain.getRequest().putArgs();
            intent.putExtra("key-build", "value-build");
            RouteResponse response = chain.onProcess();
            LLogger.d(response);
          }
        })
        .build();

    api.navigation("app/second", new Interceptor() {
      @Override
      public void intercept(final Chain chain) {
        LLogger.d("优先级最高的 随参数注入拦截器 开始添加附带参数");
        RouteRequest request = chain.getRequest();
        request.putArgs()
            .putExtra("key-api-1", "value-api-1")
            .putExtra("key-api-2", 100);
        RouteResponse response = chain.onProcess();
        if (!RouteResponse.isRedirect(response)) {
          LLogger.d("成功导航且非重定向 即成功跳转到原目标 可以发送EventBus事件");
        }
        LLogger.d(response);

      }
    });

  }

  public void onNavigationRouteInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())//添加的表中没有 app/third路径
        .openDebugLog()
        .addRouteInterceptors(new RouteInterceptor() {
          @Override
          public boolean onResponse(Chain chain, RouteResponse response) {
            LLogger.d(chain, response);
            return false;
          }

          @Override
          public boolean onFailure(Chain chain, Throwable throwable) {
            LLogger.d(chain, throwable);
            if (throwable instanceof RouteBadRequestException) {
              Chain clone = chain.clone();
              RouteRequest request = clone.getRequest();
              //如重定向到版本检测页 引导下载新版本
              request.notifyPath("app/user/login");
              clone.onProcess();
            }
            return false;
          }
        })
        .build();

    /**
     * 故意发起一次注定失败的 模拟一些特殊场景导航
     * 如通过推送通道接收的新消息，该新消息指定（推送参数指定页面信息）需要跳转到某个模块
     * 而旧版本没有该模块，路由拦截器能够最终处理失败，处理旧版本新推送类型问题
     */
    api.navigation("app/third");

  }


  public void onNavigationNotDeclareClick(View view) {
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .openDebugLog()
        .build();

    /**
     * 尝试导航到 外部应用
     * 因为本地路由表根本没有处理该导航的目标，所以方法只有拦截器参数
     * 把外部应用的跳转页统一到路由器中来，统一管理
     */
    api.navigation(new Interceptor() {
      @Override
      public void intercept(Chain chain) {
        RouteRequest request = chain.getRequest();
        request.notifyIntent().setAction(Intent.ACTION_DIAL)
            .setData(Uri.parse("tel:17600000001"));
        RouteResponse response = chain.onProcess();
        LLogger.d(RouteResponse.isDeclare(response));
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LLogger.d(this,requestCode, resultCode, data);
    Bundle extras = data.getExtras();
    if (extras != null) {
      LLogger.d("带的参数的Result,Intent数据非空");
      for (String key : extras.keySet()) {
        LLogger.d(key, extras.get(key));
      }
    } else {
      LLogger.d("不带的参数的Result,Intent数据为空");
    }
  }


}
