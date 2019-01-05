package com.licola.route.api;

import java.util.List;
import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/23
 */
final class Utils {

  static boolean isEmpty(CharSequence value) {
    return value == null || value.length() == 0;
  }

  static boolean isEmpty(Map map) {
    return map == null || map.isEmpty();
  }

  static boolean isEmpty(List list) {
    return list == null || list.isEmpty();
  }
}
