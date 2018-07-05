package com.licola.route.annotation;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteMeta {
  private Class<?> target;
  private String name;
  private String module;

  public RouteMeta(Class<?> target, String name, String module) {
    this.target = target;
    this.name = name;
    this.module = module;
  }

  public static RouteMeta create(Class<?> target, String name, String module){
    return new RouteMeta(target,name,module);
  }

  public Class<?> getTarget() {
    return target;
  }

  public void setTarget(Class<?> target) {
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
}
