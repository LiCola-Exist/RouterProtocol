package com.licola.route.api;

/**
 *
 * 拦截器
 *
 * @author LiCola
 * @date 2018/7/5
 */
public interface Interceptor {

  void intercept(Chain chain);
}
