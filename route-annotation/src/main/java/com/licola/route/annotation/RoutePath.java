package com.licola.route.annotation;

/**
 * Created by LiCola on 2018/7/7.
 */
public class RoutePath {

  private static final char PATH_SEPARATOR = '/';

  public static String makePath(String module, String target) {
    String joinPath = module + PATH_SEPARATOR + target;
    return joinPath.intern();
  }
}
