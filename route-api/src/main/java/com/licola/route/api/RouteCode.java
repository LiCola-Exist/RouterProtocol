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

  public static final int CODE_PROCESS = 100;
  public static final int CODE_SUCCESS = 200;
  public static final int CODE_REDIRECT = 300;
  public static final int CODE_FAILED = 400;

  @IntDef({CODE_PROCESS, CODE_SUCCESS, CODE_REDIRECT, CODE_FAILED})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Code {

  }
}
