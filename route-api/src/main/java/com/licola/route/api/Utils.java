package com.licola.route.api;

import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class Utils {

  public static boolean isEmpty(CharSequence value) {
    return value == null || value.length() == 0;
  }

  public static boolean isEmpty(Map map) {
    return map == null || map.isEmpty();
  }
}
