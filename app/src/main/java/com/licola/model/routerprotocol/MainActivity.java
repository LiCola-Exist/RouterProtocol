package com.licola.model.routerprotocol;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.RouteUser;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteApi;
import com.licola.route.api.RouteApi.Builder;
import com.licola.route.api.RouteCode;
import com.licola.route.api.RouteInterceptor;
import com.licola.route.api.RouteResponse;

@Route(name = "main")
public class MainActivity extends AppCompatActivity {


  RouteInterceptor baseRouteInterceptor = new RouteInterceptor() {
    @Override
    public boolean intercept(RouteResponse response) {
      LLogger.d(response);

      return false;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

  }

  /**
   * 简单的路由使用
   */
  public void onNavigationSimpleClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .addRouteInterceptors(baseRouteInterceptor)
        .build();
    //下面两行代码效果一致
//    routeApi.navigation("app", "second");//字面量导航
    routeApi.navigation(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY);//常量导航
  }

  /**
   * 使用自动生成的Api 友好使用 带注解提示的 路由使用
   */
  public void onNavigationWithApiClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .addRouteInterceptors(baseRouteInterceptor)
        .build();

    RouteApp.Api api = new RouteApp.Api(routeApi);
//    api.navigation("second");//普通字面量
    api.navigation(RouteApp.SECOND_ACTIVITY);//参数注解会提示输入规则 must be one of
  }

  /**
   * 示例注入第三方库的 路由使用
   */
  public void onNavigationThirdClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new MyRoute.Route())
        .addRouteInterceptors(baseRouteInterceptor)
        .build();

    routeApi.navigation(MyRoute.MODULE_NAME, MyRoute.THIRD_ACTIVITY);
  }

  /**
   * 拦截器的使用示例
   */
  public void onNavigationInterceptorClick(View view) {
    RouteApi api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())//注入app模块的路由
        .addRouteRoots(new RouteUser.Route())//注入module模块的路由
        .addInterceptors(new Interceptor() {//注入拦截器
          @Override
          public int intercept(RouteApi route, RouteResponse response) {
            LLogger.d("拦截器 navigation开始时调用 可以重定向导航模块和目标");

            //模仿特定模块拦截（比如需要登录的模块）
            String module = response.getModule();
            String target = response.getTarget();
            if (RouteApp.MODULE_NAME.equals(module) && RouteApp.SECOND_ACTIVITY.equals(target)) {
              LLogger.d("重定向导航到其他模块（如用户模块注册页面）");
              RouteResponse
                  .notifyTarget(response, RouteUser.MODULE_NAME, RouteUser.REGISTER_ACTIVITY);
            }
            return response.getCode();
          }
        })
        .addRouteInterceptors(baseRouteInterceptor)
        .addRouteInterceptors(new RouteInterceptor() {
          @Override
          public boolean intercept(RouteResponse response) {
            LLogger.d("成功导航之后调用 可以记录页面跳转");
            LLogger.d("页面跳转到 模块:" + response.getModule() + " 名称:" + response.getTarget() + " 路径:"
                + RoutePath
                .makePath(response.getModule(), response.getTarget()));
            //不拦截信息路流 继续转递下去
            return true;
          }//注入路由拦截器

        })
        .build();

    @RouteCode.Code int code = api.navigation(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY);
    switch (code) {

      case RouteCode.CODE_PROCESS:
        LLogger.d("导航处理中 一般navigation返回后不会出现该值");
        break;
      case RouteCode.CODE_REDIRECT:
        LLogger.d("重定向导航 即实际跳转页面不是navigation参数的目标");
        break;
      case RouteCode.CODE_SUCCESS:
        LLogger.d("成功导航");
        break;
      case RouteCode.CODE_FAILED:
        LLogger.d("导航失败");
        break;
      case RouteCode.CODE_ERROR:
        LLogger.d("路由内部错误");
        break;
    }

    LLogger.d("是否严格意义上的成功跳转：" + RouteApi.checkNavigation(code));
    LLogger.d("是否发生跳转：" + RouteApi.checkNavigationLoose(code));
  }

  /**
   * 自动生成Api的拦截器的使用示例
   */
  public void onNavigationInterceptorApiClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .addInterceptors(new Interceptor() {
          @Override
          public int intercept(RouteApi route, RouteResponse response) {
            LLogger.d("构造注入的拦截器 优先级较低");

            RouteResponse.putExtraIntent(response).putExtra("build-key", "basics");

            return response.getCode();
          }
        })
        .addRouteInterceptors(baseRouteInterceptor)
        .build();

    RouteApp.Api api = new RouteApp.Api(routeApi);
    api.navigation(RouteApp.SECOND_ACTIVITY, new Interceptor() {
      @Override
      public int intercept(RouteApi route, RouteResponse response) {
        LLogger.d("随参数注入的拦截器 优先级最高");

        //可以通过拦截器 给Intent添加特定数据

        Intent intent = RouteResponse.putExtraIntent(response);
        intent.putExtra("api-key", "specific");

        return response.getCode();
      }
    });

  }


  public void onNavigationInterceptorApiIntentClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .addRouteInterceptors(baseRouteInterceptor)
        .build();

    routeApi.navigation(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY, new Interceptor() {
      @Override
      public int intercept(RouteApi route, RouteResponse response) {

        //假设要跳转的组件 和定位服务相关 且检测到定位功能未开启

        //修改Intent的action 跳转到系统定位设置
        Intent intent = RouteResponse.notifyIntent(response);
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        return RouteCode.CODE_REDIRECT;
      }
    });
  }
}
