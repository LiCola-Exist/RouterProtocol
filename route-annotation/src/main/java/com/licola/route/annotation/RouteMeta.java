package com.licola.route.annotation;

import android.support.annotation.Nullable;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteMeta {

  @Nullable
  private Class<?> target;
  private String name;
  private String module;

  public RouteMeta(@Nullable Class<?> target, String name, String module) {
    this.target = target;
    this.name = name;
    this.module = module;
  }

  public static RouteMeta create(Class<?> target, String name, String module){
    return new RouteMeta(target,name,module);
  }

  @Nullable
  public Class<?> getTarget() {
    return target;
  }

  public void setTarget(@Nullable Class<?> target) {
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteMeta{");
    sb.append("target=").append(target);
    sb.append(", name='").append(name).append('\'');
    sb.append(", module='").append(module).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
