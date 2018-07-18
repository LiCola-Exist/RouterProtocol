package com.licola.route.api;

import android.support.annotation.NonNull;
import com.licola.route.annotation.RouteMeta;
import java.util.Map;

/**
 * @author LiCola
 * @date 2018/7/18
 */
public interface Router {

  @NonNull
  Map<String,RouteMeta> getRouteMap();

}
