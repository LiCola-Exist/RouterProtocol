package com.licola.route.api;

import com.licola.route.annotation.RouteMeta;
import java.util.List;
import java.util.Map;

/**
 * Created by LiCola on 2018/7/5.
 */
public interface RouteRoot {

  List<RouteMeta> load();
}
