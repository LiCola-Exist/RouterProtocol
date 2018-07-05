package com.licola.route.compiler;

import static com.licola.route.compiler.Constants.PACKAGE_API;
import static com.licola.route.compiler.Constants.ROUTE_ANNOTATION_PROTOCOL;
import static com.licola.route.compiler.Constants.ROUTE_CLASS_PROTOCOL_PREFIX;
import static com.licola.route.compiler.Constants.ROUTE_METHOD_LOAD;
import static com.licola.route.compiler.Constants.ROUTE_METHOD_LOAD_PARAMETER;

import com.google.auto.common.MoreElements;
import com.licola.route.annotation.Route;
import com.licola.route.annotation.RouteMeta;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.lang.annotation.Retention;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by LiCola on 2017/6/21.
 */

public class ProcessorRouteProtocol {


  private Set<? extends Element> elements;
  private String className;
  private String moduleName;

  public static ProcessorRouteProtocol build(Set<? extends Element> elements,
      String protocolName, String moduleName) {
    return new ProcessorRouteProtocol(elements, protocolName, moduleName);
  }

  public ProcessorRouteProtocol(Set<? extends Element> elements, String className,
      String moduleName) {
    this.elements = elements;
    this.className = className;
    this.moduleName = moduleName;
  }

  TypeSpec process() {
    if (CheckUtils.isEmpty(elements)) {
      return null;
    }

    if (CheckUtils.isEmpty(className)) {
      return null;
    }

    //定义类
    TypeSpec.Builder classSpecBuild = TypeSpec.classBuilder(className);

    //定义Def注释
    AnnotationSpec.Builder annotationBuilder =
        AnnotationSpec.builder(ClassName.get("android.support.annotation", "StringDef"));

    MethodSpec.Builder methodBuild = MethodSpec.methodBuilder(ROUTE_METHOD_LOAD)
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterizedTypeName.get(Map.class, String.class, RouteMeta.class),
            ROUTE_METHOD_LOAD_PARAMETER)
        .returns(void.class);

    for (Element element : elements) {
      addClassAndAnnotationField(element, moduleName, classSpecBuild, annotationBuilder,
          methodBuild);
    }

    //定义Retention注释
    AnnotationSpec interfaceAnnotationRetention = AnnotationSpec.builder(Retention.class)
        .addMember("value", "$L", ClassName.get("java.lang.annotation.RetentionPolicy", "SOURCE"))
        .build();

    //定义并构建 protocol 注解
    TypeSpec annotationIntent = TypeSpec.annotationBuilder(ROUTE_ANNOTATION_PROTOCOL)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(annotationBuilder.build())//添加Def注释
        .addAnnotation(interfaceAnnotationRetention)//添加Retention
        .addJavadoc("作用于Source源文件的注释\n")
        .addJavadoc("用于代码检查和辅助输入\n")
        .build();

    classSpecBuild
        .addModifiers(Modifier.PUBLIC)
        .addType(annotationIntent);//添加类的注释字段

    classSpecBuild
        .addSuperinterface(ClassName.get(PACKAGE_API, ROUTE_CLASS_PROTOCOL_PREFIX))
        .addMethod(methodBuild.build());

    classSpecBuild.addJavadoc("Created by @$L on $L \n", Route.class.getSimpleName(),
        Utils.getNowTime())
        .addJavadoc("路由的协议类\n")
        .addJavadoc("功能：约定Activity类与特定值的直接对应关系\n");

    return classSpecBuild.build();
  }

  private void addClassAndAnnotationField(Element element, String moduleName,
      Builder classSpecBuild, AnnotationSpec.Builder annotationBuilder,
      MethodSpec.Builder methodBuild) {
    String name = element.getAnnotation(Route.class).name();
    if (CheckUtils.isEmpty(name)) {
      name = element.getSimpleName().toString();
    }

    //类中 添加静态变量
    classSpecBuild.addField(FieldSpec.builder(String.class, name)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$S", name)
        .build());

    //注释中 添加字段
    annotationBuilder.addMember("value", "$L", name);

    //方法中 添加表达式
    methodBuild
        .addStatement(ROUTE_METHOD_LOAD_PARAMETER + ".put($L,$T.create($L.class,$L,$S))", name,
            RouteMeta.class,
            MoreElements.asType(element).getQualifiedName().toString(), name, moduleName);
  }


}
