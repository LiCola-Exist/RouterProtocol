package com.licola.model.routerprotocol;

import com.licola.route.annotation.RouteMeta;
import com.licola.route.api.RouteRoot;
import java.util.Arrays;
import java.util.List;

/**
 * @author LiCola
 * @date 2018/7/13 自定义的路由类 用于解决引入第三方库模块，无法直接修改源码打上注解（即：不能自动生成路由类） 手动编写路由信息，实现注入第三方库到路由 代码参考：
 * @see com.licola.route.RouteApp
 */
public class MyRoute {

  public static final String MODULE_NAME = "second";

  public static final String THIRD_ACTIVITY = "third";

  public static class Route implements RouteRoot {

    @Override
    public List<RouteMeta> load() {
      return Arrays.asList(
          RouteMeta
              .create( "other/third",
                  THIRD_ACTIVITY, MODULE_NAME,com.licola.model.module.third.ThirdActivity.class)
      );
    }
  }
}
