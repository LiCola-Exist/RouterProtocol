package com.licola.model.routerprotocol;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.AdapterRouteInterceptor;
import com.licola.route.api.Api;
import com.licola.route.api.Chain;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteInterceptor;
import com.licola.route.api.RouteRequest;
import com.licola.route.api.RouteResponse;
import com.licola.route.api.RouterApi.Builder;
import com.licola.route.api.exceptions.RouteBadRequestException;
import com.licola.route.api.exceptions.RouteBreakException;

@Route(path = "main", description = "首页界面")
public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CODE = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
    //step1：构造路由api实例
    Api api = new Builder(getApplication())
        .build();

    //step2：开始导航
    //以下两行代码 效果一样
//    api.navigation("app/second");//直接路径导航 参数格式：模块名/页面路径
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY));//使用生成的常量导航 使用工具方法构造参数
  }

  /**
   * 带RequestCode的路由请求
   */
  public void onNavigationRequestCodeClick(View view) {
    Api api = new Builder(getApplication())
        .build();

    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY),
        new Interceptor() {
          @Override
          public void intercept(Chain chain) {
            chain.onProcess(new RouteRequest.Builder(chain.getRequest())
                .requestCode(REQUEST_CODE)
                .build());
          }
        });
  }

  /**
   * 使用自动生成的Api 友好使用 带注解提示的 路由使用
   */
  public void onNavigationUseProtocolClick(View view) {
    Api api = new Builder(getApplication())
        .build();

    //模块内 跳转 路径参数带注解提示
    RouteApp.Api appApi = new RouteApp.Api(api);
    appApi.navigation(RouteApp.SECOND_ACTIVITY);//参数注解会提示输入规则
  }

  /**
   * 示例注入第三方库的 路由使用
   */
  public void onNavigationThirdClick(View view) {
    Api api = new Builder(getApplication())
        .build();

    api.navigation("other/third");
  }


  /**
   * 拦截器的使用示例
   */
  public void onNavigationInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .addInterceptors(new Interceptor() {
          @Override
          public void intercept(Chain chain) {
            RouteResponse routeResponse = chain.onProcess(chain.getRequest());
            LLogger.d(routeResponse, "是否成功跳转:" + RouteResponse.isSuccess(routeResponse));
          }
        })
        .openDebugLog()//开启调试模式 log打印导航结果
        .build();


    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY),
        new Interceptor() {
          @Override
          public void intercept(final Chain chain) {

            if (BuildConfig.DEBUG){
              //假设 跳转时检测到 无法直接跳转到目标页面
              new AlertDialog.Builder(MainActivity.this)
                  .setTitle("拦截器的使用")
                  .setMessage("假设要跳转的模块需要定位服务，并假设检测到定位服务未开启，点击设置跳转到系统设置-定位服务，点击重定向转到其他页面（如说明页面）")
                  .setNeutralButton("设置", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      RouteRequest request = chain.getRequest();
                      Intent intent = request.getIntent();
                      intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                      chain.onProcess(request);
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
                      RouteRequest redirectRequest = new RouteRequest.Builder(chain.getRequest())
                          .routePath(RouteApp.MODULE_NAME, RouteApp.REDIRECT_ACTIVITY)//构造新的导航路径
                          .build();
                      chain.onProcess(redirectRequest);
                    }
                  })
                  .show();
            }else {
              //假设可以 直接跳转
              chain.onProcess(chain.getRequest());
            }
          }
        });

  }

  public void onNavigationInterceptorArgClick(final View view) {
    Api api = new Builder(getApplication())
        .addInterceptors(new Interceptor() {
          @Override
          public void intercept(final Chain chain) {
            LLogger.d("路由配置，统一添加附带参数");
            Bundle extras = new Bundle();
            extras.putInt("baseKeyInt", 1);
            extras.putString("baseKeyStr", "路由配置的统一参数，注意覆盖问题");
            RouteResponse response = chain.onProcess(new RouteRequest.Builder(chain.getRequest())
                .putIntent(extras)
                .build());
            LLogger.d(response);
          }
        })
        .build();

    api.navigation("app/second", new Interceptor() {
      @Override
      public void intercept(final Chain chain) {
        LLogger.d("随参数注入拦截器 开始添加附带参数");
        Bundle extras = new Bundle();
        extras.putString("key1", "随方法参数拦截器注入的参数");
        RouteResponse response = chain.onProcess(new RouteRequest.Builder(chain.getRequest())
            .putIntent(extras)
            .build());
        LLogger.d(response);
      }
    });

  }

  public void onNavigationRouteInterceptorClick(View view) {
    Api api = new Builder(getApplication())
        .openDebugLog()//开始调试日志
        .addRouteInterceptors(new RouteInterceptor() {
          @Override
          public boolean onResponse(Chain chain, RouteResponse response) {
            //成功导航回调 只打印结果
            LLogger.d(chain, response);
            return false;
          }

          @Override
          public boolean onFailure(Chain chain, Throwable throwable) {
            //失败导航回调
            LLogger.d(chain, throwable);
            if (throwable instanceof RouteBadRequestException) {
              Toast.makeText(MainActivity.this, "失败的导航，降级处理", Toast.LENGTH_SHORT).show();
              Chain clone = chain.clone();
              //如重定向到版本检测页 引导下载新版本
              clone.onProcess(new RouteRequest.Builder(chain.getRequest())
                  .routePath("app/redirect")
                  .build());
              return true;
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
    final Api api = new Builder(getApplication())
        .addRouteInterceptors(new AdapterRouteInterceptor(){//提供空实现类 选择实现部分方法
          @Override
          public boolean onFailure(Chain chain, Throwable throwable) {
            Toast.makeText(MainActivity.this, "无法打开外部应用", Toast.LENGTH_SHORT).show();
            return true;
          }
        })
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
        //通过action 调起拨号 一般都可以成功
//        RouteRequest request = chain.getRequest();
//        request.getIntent()
//            .setAction(Intent.ACTION_DIAL)
//            .setData(Uri.parse("tel:17600000001"));
//        RouteResponse response = chain.onProcess(request);
//        LLogger.d(RouteResponse.isRoute(response));

//        //通过包名和类名 调起特定app 如微信
        RouteRequest request = chain.getRequest();
        Intent intent = request.getIntent();
        intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));//微信
//        intent.setComponent(new ComponentName("com.sina.weibo", "com.sina.weibo.SplashActivity"));//微博
//        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.SplashActivity"));//qq
//        intent.setComponent(new ComponentName("com.ss.android.ugc.aweme", "com.ss.android.ugc.aweme.splash.SplashActivity"));//抖音
        intent.setAction(Intent.ACTION_MAIN);
        RouteResponse response = chain.onProcess(request);
        if (RouteResponse.isSuccess(response)) {
          LLogger.d("成功导航到外部应用");
        } else {
          LLogger.d("无法导航到外部应用");
        }
      }
    });
  }

  public void onNavigationAnimationClick(final View view) {

    final Api api = new Builder(getApplication())
        .build();

    RouteApp.Api appApi = new RouteApp.Api(api);
    appApi.navigation(RouteApp.ANIMATION_SHARED_ACTIVITY, new Interceptor() {
      @Override
      public void intercept(Chain chain) {
        Bundle extras = new Bundle();
        extras.putInt(AnimationSharedActivity.KEY_IMAGE, R.drawable.cover);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
            .makeSceneTransitionAnimation(MainActivity.this, view, "cover");

        RouteRequest.Builder requestBuilder = new RouteRequest.Builder(chain.getRequest())
            .routeSource(MainActivity.this)
            .putIntent(extras)
            .putBundle(activityOptions.toBundle());

        chain.onProcess(requestBuilder.build());
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    LLogger.d(this, requestCode, resultCode, data);
    Bundle extras = data.getExtras();
    if (extras != null) {
      LLogger.d("带的参数的Result,Intent数据非空");
//      Toast.makeText(this, "Activity收到结果", Toast.LENGTH_SHORT).show();
      for (String key : extras.keySet()) {
        LLogger.d(key, extras.get(key));
      }
    } else {
      LLogger.d("不带的参数的Result,Intent数据为空");
    }
  }


}
