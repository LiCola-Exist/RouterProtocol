package com.licola.route.api.exceptions;

/**
 * 路由空响应异常
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteEmptyResponseException extends Throwable {

  public RouteEmptyResponseException(String message) {
    super(message);
  }
}
