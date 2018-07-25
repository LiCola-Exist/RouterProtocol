package com.licola.route.api;

/**
 * 路由拦截器
 * 在导航之后调起
 * 作用：
 * 1：导航埋点，记录应用跳转信息，主要通过onResponse获取跳转信息并记录
 * 2：导航统一异常处理，对能够处理的异常，重新发起导航到其他页面，主要通过onFailure方法的{@link Chain#clone()}重新发起请求
 *
 * @author LiCola
 * @date 2018/7/7
 */
public interface RouteInterceptor {

  /**
   * 成功响应的回调方法
   */
  boolean onResponse(Chain chain, RouteResponse response);

  /**
   * 失败的回调方法
   * 关于throwable
   *
   * @see com.licola.route.api.exceptions.RouteConfigError
   * @see com.licola.route.api.exceptions.RouteBadRequestException
   * @see com.licola.route.api.exceptions.RouteBreakException
   * @see com.licola.route.api.exceptions.RouteEmptyResponseException
   */
  boolean onFailure(Chain chain, Throwable throwable);
}
