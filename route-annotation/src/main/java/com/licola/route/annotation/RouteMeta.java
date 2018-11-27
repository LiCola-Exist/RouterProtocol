package com.licola.route.annotation;

/**
 * Created by LiCola on 2018/7/5.
 */
public class RouteMeta {

  private Class<?> target;

  private String path;
  private String name;
  private String module;

  public RouteMeta(String path, String name, String module, Class<?> target) {
    this.path = path;
    this.name = name;
    this.module = module;
    this.target = target;
  }

  public static RouteMeta create(String path, String name, String module, Class<?> target) {
    return new RouteMeta(path, name, module, target);
  }

  public Class<?> getTarget() {
    return target;
  }

  public String getName() {
    return name;
  }

  public String getModule() {
    return module;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RouteMeta{");
    sb.append("target=").append(target);
    sb.append(", path='").append(path).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", module='").append(module).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
