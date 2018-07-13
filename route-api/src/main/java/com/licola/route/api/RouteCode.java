package com.licola.route.api;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by LiCola on 2018/7/5.
 * 路由状态码 参考HTTP状态码理解
 * 用于表示路由导航响应结果
 */
public class RouteCode {

  /**
   * 代表请求已被接受，需要继续处理，一般外部不会收到该状态
   * 100 Continue
   */
  public static final int CODE_PROCESS = 100;

  /**
   * 请求成功 成功通过参数导航到目标
   * 200 OK
   */
  public static final int CODE_SUCCESS = 200;

  /**
   * 对当前请求的响应被重定向到另一个目标
   * 303 See Other
   */
  public static final int CODE_REDIRECT = 303;

  /**
   * 请求失败 请求的目标没有在路由表中找到
   * 404 Not Found
   */
  public static final int CODE_FAILED = 404;

  @IntDef({CODE_PROCESS, CODE_SUCCESS, CODE_REDIRECT, CODE_FAILED})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Code {

  }
}
