package com.licola.route.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由注解
 * 使用方法：注解Activity。
 * 原理：代码编译期间，根据注解读取到每个被注解的Activity类和名称。
 * 代码生成Activity同名的int变量。添加到路由容器中。
 * 容器采用<key,value>形式存储：<Integer：Activity名称int代码值：Class：Activity类>
 * 这样MVP架构中，P层可以直接使用路由中的Key作为跳转控制，而不用依赖V层的实现类Activity。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Route {

}
