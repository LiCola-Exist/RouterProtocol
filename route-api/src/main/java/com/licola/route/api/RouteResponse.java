package com.licola.route.api;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteResponse {

  public static RouteResponse buildProcess(String target) {
    RouteResponse routeResponse = new RouteResponse();
    routeResponse.code = RouteCode.CODE_PROCESS;
    routeResponse.target = target;
    return routeResponse;
  }

  public static RouteResponse notifyFailed(RouteResponse routeResponse) {
    routeResponse.code = RouteCode.CODE_FAILED;
    return routeResponse;
  }

  public static RouteResponse notifySuccess(RouteResponse routeResponse, Class<?> targetClass,
      String target, String module) {
    routeResponse.code = RouteCode.CODE_SUCCESS;
    routeResponse.targetClass = targetClass;
    routeResponse.target = target;
    routeResponse.module = module;
    return routeResponse;
  }

  public static RouteResponse notifySuccessByRedirect(RouteResponse routeResponse, Class<?> targetClass,
      String target, String module) {
    routeResponse.code = RouteCode.CODE_REDIRECT;
    routeResponse.targetClass = targetClass;
    routeResponse.target = target;
    routeResponse.module = module;
    return routeResponse;
  }

  public static RouteResponse notifyTarget(RouteResponse routeResponse, String target) {
    routeResponse.code = RouteCode.CODE_REDIRECT;
    routeResponse.target = target;
    return routeResponse;
  }

  private Class<?> targetClass;
  private String target;
  private String module;
  @RouteCode.Code
  private int code;


  @RouteCode.Code
  public int getCode() {
    return code;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public String getTarget() {
    return target;
  }

  public String getModule() {
    return module;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteResponse{");
    sb.append("targetClass=").append(targetClass);
    sb.append(", target='").append(target).append('\'');
    sb.append(", module='").append(module).append('\'');
    sb.append(", code=").append(code);
    sb.append('}');
    return sb.toString();
  }
}
