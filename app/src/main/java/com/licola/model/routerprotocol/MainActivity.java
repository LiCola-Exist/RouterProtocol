package com.licola.model.routerprotocol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.RouteModule;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RouteMeta;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteApi;
import com.licola.route.api.RouteApi.Builder;
import com.licola.route.api.RouteCode;
import com.licola.route.api.RouteInterceptor;
import com.licola.route.api.RouteResponse;

@Route(name = "main")
public class MainActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

  }

  public void onNavigationSimpleClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();
    //下面两行代码效果一致
//    routeApi.navigation("app", "second");//字面量导航
    routeApi.navigation(RouteApp.NAME, RouteApp.SecondActivity);//常量导航
  }

  public void onNavigationWithApi(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();

    RouteApp.Api api = new RouteApp.Api(routeApi);
//    api.navigation("second");//普通字面量
    api.navigation(RouteApp.SecondActivity);//参数注解会提示输入规则 must be one of
  }

  public void onNavigationInterceptorClick(View view) {
    RouteApi api = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())//注入app模块的路由
        .addRouteRoots(new RouteModule.Route())//注入module模块的路由
        .addInterceptors(new Interceptor() {//注入拦截器
          @Override
          public RouteResponse intercept(RouteApi route, RouteResponse response) {
            LLogger.d("拦截器 navigation开始时调用 可以重定向导航模块和目标");

            //模仿需要特殊模块拦截（比如需要登录模块）
            if (RouteApp.NAME.equals(response.getModule()) && RouteApp.SecondActivity
                .equals(response.getTarget())) {
              LLogger.d("强制导航到其他模块（如登录模块）");
              return RouteResponse
                  .notifyTarget(response, RouteModule.NAME, RouteModule.ModuleActivity);
            }
            return response;
          }
        })
        .addRouteInterceptors(new RouteInterceptor() {//注入路由拦截器
          @Override
          public boolean intercept(RouteMeta meta) {
            LLogger.d("成功导航之后调用 可以记录页面跳转");
            LLogger
                .d("页面跳转到 模块:" + meta.getModule() + " 名称:" + meta.getName() + " 路径:" + RoutePath
                    .makePath(meta.getModule(), meta.getName()));
            return true;
          }
        })
        .build();

    @RouteCode.Code int code = api.navigation(RouteApp.NAME, RouteApp.SecondActivity);
    switch (code) {
      case RouteCode.CODE_FAILED:
        LLogger.d("导航失败");
        break;
      case RouteCode.CODE_PROCESS:
        LLogger.d("导航处理中 一般navigation返回后不会出现该值");
        break;
      case RouteCode.CODE_REDIRECT:
        LLogger.d("重定向导航 即实际跳转页面不是navigation参数的目标");
        break;
      case RouteCode.CODE_SUCCESS:
        LLogger.d("成功导航");
        break;
    }
  }

  public void onNavigationApiInterceptorClick(View view) {
    RouteApi routeApi = new Builder(getApplication())
        .addRouteRoots(new RouteApp.Route())
        .build();

    RouteApp.Api api = new RouteApp.Api(routeApi);
    api.navigation(RouteApp.SecondActivity, new Interceptor() {
      @Override
      public RouteResponse intercept(RouteApi route, RouteResponse response) {
        LLogger.d("随参数注入的拦截器被调用 优先级最高");
        return RouteResponse.notifyTarget(response, RouteApp.RedirectActivity);
      }
    });

  }


}
