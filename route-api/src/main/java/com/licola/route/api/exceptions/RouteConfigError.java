package com.licola.route.api.exceptions;

/**
 * 路由配置错误
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteConfigError extends Error {

  public RouteConfigError(String message) {
    super(message);
  }
}
