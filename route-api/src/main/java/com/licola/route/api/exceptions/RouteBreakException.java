package com.licola.route.api.exceptions;

/**
 * 路由终止异常 一般由用户主动抛出
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteBreakException extends RuntimeException {

  public RouteBreakException(String message) {
    super(message);
  }
}
