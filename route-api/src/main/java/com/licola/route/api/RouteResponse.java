package com.licola.route.api;

import com.licola.route.annotation.RouteMeta;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteResponse {

  public static RouteResponse buildProcess(String module, String target) {
    RouteResponse routeResponse = new RouteResponse();
    routeResponse.code = RouteCode.CODE_PROCESS;
    routeResponse.module = module;
    routeResponse.target = target;
    return routeResponse;
  }

  public static RouteResponse notifyTarget(RouteResponse routeResponse, String target) {
    return notifyTarget(routeResponse, routeResponse.getModule(), target);
  }

  public static RouteResponse notifyTarget(RouteResponse routeResponse, String module,
      String target) {
    routeResponse.code = RouteCode.CODE_REDIRECT;
    routeResponse.module = module;
    routeResponse.target = target;
    return routeResponse;
  }

  public static RouteResponse notifyFailed(RouteResponse routeResponse) {
    routeResponse.code = RouteCode.CODE_FAILED;
    return routeResponse;
  }

  public static RouteResponse notifySuccess(RouteResponse routeResponse, RouteMeta routeMeta) {
    routeResponse.code = RouteCode.CODE_SUCCESS;
    routeResponse.routeMeta = routeMeta;
    return routeResponse;
  }

  public static RouteResponse notifySuccessByRedirect(RouteResponse routeResponse,
      RouteMeta routeMeta) {
    routeResponse.code = RouteCode.CODE_REDIRECT;
    routeResponse.routeMeta = routeMeta;
    return routeResponse;
  }

  private RouteMeta routeMeta;
  private String target;
  private String module;
  @RouteCode.Code
  private int code;

  @RouteCode.Code
  public int getCode() {
    return code;
  }

  public String getTarget() {
    return target;
  }

  public String getModule() {
    return module;
  }

  public RouteMeta getRouteMeta() {
    return routeMeta;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteResponse{");
    sb.append("routeMeta=").append(routeMeta);
    sb.append(", target='").append(target).append('\'');
    sb.append(", module='").append(module).append('\'');
    sb.append(", code=").append(code);
    sb.append('}');
    return sb.toString();
  }
}
