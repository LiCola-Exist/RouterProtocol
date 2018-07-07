package com.licola.route.annotation;

/**
 * Created by LiCola on 2018/7/7.
 */
public class RoutePath {

  public static String makePath(String module, String target) {
    return module + "/" + target;
  }
}
