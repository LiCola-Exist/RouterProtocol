package com.licola.route.compiler;

import static com.licola.route.compiler.Constants.PACKAGE_API;
import static com.licola.route.compiler.Constants.PACKAGE_BASE;
import static com.licola.route.compiler.Constants.ROUTE_ANNOTATION_CODE;
import static com.licola.route.compiler.Constants.ROUTE_ANNOTATION_PROTOCOL;
import static com.licola.route.compiler.Constants.ROUTE_CLASS_INTERCEPTOR;
import static com.licola.route.compiler.Constants.ROUTE_CLASS_ROUTE_CODE;
import static com.licola.route.compiler.Constants.ROUTE_METHOD_NAVIGATION;
import static com.licola.route.compiler.Constants.ROUTE_METHOD_NAVIGATION_PARAMETER_1;
import static com.licola.route.compiler.Constants.ROUTE_METHOD_NAVIGATION_PARAMETER_2;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

/**
 * Created by LiCola on 2018/7/5.
 */
public class ProcessorRoute {

  private String className;
  private String protocolName;

  public static ProcessorRoute build(String className, String protocolName) {
    return new ProcessorRoute(className, protocolName);
  }

  public ProcessorRoute(String className, String protocolName) {
    this.className = className;
    this.protocolName = protocolName;
  }

  TypeSpec process() {
    if (CheckUtils.isEmpty(className)) {
      return null;
    }

    TypeSpec.Builder classSpecBuild = TypeSpec.interfaceBuilder(className)
        .addModifiers(Modifier.PUBLIC);

    MethodSpec methodSpec1 = MethodSpec.methodBuilder(ROUTE_METHOD_NAVIGATION)
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addParameter(ParameterSpec.builder(String.class, ROUTE_METHOD_NAVIGATION_PARAMETER_1)
            .addAnnotation(ClassName.get(PACKAGE_BASE, protocolName, ROUTE_ANNOTATION_PROTOCOL))
            .build())
        .addAnnotation(ClassName.get(PACKAGE_API,ROUTE_CLASS_ROUTE_CODE,ROUTE_ANNOTATION_CODE))
        .returns(int.class)
        .build();

    MethodSpec methodSpec2 = MethodSpec.methodBuilder(ROUTE_METHOD_NAVIGATION)
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .addParameter(ParameterSpec.builder(String.class, ROUTE_METHOD_NAVIGATION_PARAMETER_1)
            .addAnnotation(ClassName.get(PACKAGE_BASE, protocolName, ROUTE_ANNOTATION_PROTOCOL))
            .build())
        .addParameter(
            ParameterSpec.builder(ClassName.get(PACKAGE_API, ROUTE_CLASS_INTERCEPTOR),
                ROUTE_METHOD_NAVIGATION_PARAMETER_2)
                .build())
        .addAnnotation(ClassName.get(PACKAGE_API,ROUTE_CLASS_ROUTE_CODE,ROUTE_ANNOTATION_CODE))
        .returns(int.class)
        .build();

    classSpecBuild
        .addMethod(methodSpec1)
        .addMethod(methodSpec2);

    return classSpecBuild.build();
  }
}
