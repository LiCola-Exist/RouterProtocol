# [RouterProtocol](https://github.com/LiCola/RouterProtocol)

[ ![Download](https://api.bintray.com/packages/licola/maven/RouterProtocol-annotation/images/download.svg) ](https://bintray.com/licola/maven/RouterProtocol-annotation/_latestVersion)
[ ![Download](https://api.bintray.com/packages/licola/maven/RouterProtocol-api/images/download.svg) ](https://bintray.com/licola/maven/RouterProtocol-api/_latestVersion)
[ ![Download](https://api.bintray.com/packages/licola/maven/RouterProtocol-compiler/images/download.svg) ](https://bintray.com/licola/maven/RouterProtocol-compiler/_latestVersion)

# 作用
路由库，动态生成路由配置，注解参数友好化提示，拦截器支持丰富的功能定制

# 引用
```java
    implementation 'com.licola:route-api:1.3.3'
    implementation 'com.licola:route-annotation:1.3.3'
    annotationProcessor 'com.licola:route-compiler:1.3.3'

```

# 使用

最简单配置+导航使用 
```java
    //step1：构造路由api实例
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())//选择注入的路由表
        .build();
    
    //step2：开始导航
    //以下两行代码 效果一样
//    api.navigation("app/second");//直接路径导航 参数格式：模块名/页面路径
    api.navigation(RoutePath.makePath(RouteApp.MODULE_NAME, RouteApp.SECOND_ACTIVITY));//使用生成的常量导航 使用工具方法构造参数

```

## 注解配置
这里认为Activity才是导航目标，所以注解只能打在Activity上
```java
import com.licola.route.annotation.Route;

@Route(path = "second",description = "描述该页面功能，用于生成注释")
public class SecondActivity extends AppCompatActivity {
  
}

```

## 模块配置
在需要生成路由api的模块的gradle配置内加入
```groovy
android {

    defaultConfig {
        
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [moduleName: project.getName()]//输入该模块名称 如用户模块，就填写user，一般在主app中就是app=project.getName()
            }
        }
    }
    
}

```

## 生成的代码示例
这里的代码由注解自动生成，类名由Route+moduleName构成，如app模块的，就生成RouteApp类
![生成的代码](https://github.com/LiCola/RouterProtocol/blob/master/image/generate-code.png)

# 进阶使用

## 使用动态生成的api

```java
    //step1：构造路由api实例
    Api api = new Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .build();

    //stpe2：实例化动态生成的api
    RouteApp.Api appApi = new RouteApp.Api(api);
    //step3：模块内导航，只需要页面路径
    appApi.navigation(RouteApp.SECOND_ACTIVITY);//参数注解会提示输入规则
```

如果输入不符合规范，在输入过程中就会有输入提示
![输入提示](https://github.com/LiCola/RouterProtocol/blob/master/image/hint.png)

因为模块内路径已知，可以生成提示注解，在组件化模块间导航时就无法使用该功能，因为无法预知到其他模块内的页面路径，这就需要开发时协定。

## 拦截器的使用
路由提供的拦截器，可以实现很多功能，这里展示
1：跳转检测功能
2：导航重定向+导航取消+导航结果检测功能

```java
     Api api = new Builder(getApplication())
         .addRouteRoot(new RouteApp.Route())//注入app模块的路由
         .addRouteRoot(new RouteUser.Route())//注入module模块的路由
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
                           .routePath(RouteUser.MODULE_NAME, RouteUser.REGISTER_ACTIVITY)//构造新的导航路径
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
```

因为拦截器的加入，我们的导航动作就变成整个流程可控
这里提供一些思路
- 配置页面权限检测拦截器，在跳转时由拦截器发起权限申请。
- 配置页面导航记录拦截器，记录用户浏览路径。
- 配置跳转失败拦截器，统一处理跳转失败，如旧版本上因为推送跳转到新功能页面失败引导用户到下载页。
- 配置统一intent参数，发起请求时统一都带上某些页面参数。

更多功能使用参考
[参考代码](https://github.com/LiCola/RouterProtocol/blob/master/app/src/main/java/com/licola/model/routerprotocol/MainActivity.java)


# 最后
这里项目起源是我在组件化构建项目中，因为项目模块间需要页面跳转所写的路有库。简化一些功能，提供参数注解和动态生成常量辅助开发，更面向Android开发者。
## 参考
- 整体设计参考[OkHttp](https://github.com/square/okhttp)的拦截器，Request请求体和Response响应体结构。
- 借鉴[ARouter](https://github.com/alibaba/ARouter)的部分api使用