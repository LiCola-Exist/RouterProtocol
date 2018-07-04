package com.licola.route.compiler;

import com.licola.route.annotation.Route;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.lang.annotation.Retention;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by LiCola on 2017/6/21.
 */

public class ProcessorRouteContract {

  static final String AnnotationContractName = "Target";//契约类注释名

  public Set<? extends Element> elements;

  public static ProcessorRouteContract build(Set<? extends Element> elements) {
    return new ProcessorRouteContract(elements);
  }

  private ProcessorRouteContract(Set<? extends Element> elements) {
    this.elements = elements;
  }

  public TypeSpec process(String className) {
    if (CheckUtils.isEmpty(elements)) {
      return null;
    }

    //定义接口型 契约类
    Builder classSpecBuild = TypeSpec.interfaceBuilder(className);
    //定义IntDef注释
    AnnotationSpec.Builder interfaceAnnotationIntDefBuilder =
        AnnotationSpec.builder(ClassName.get("android.support.annotation", "IntDef"));

    int index = 0;
    for (Element element : elements) {
      String itemAty = element.getSimpleName().toString();
      addFieldAndAnnotationField(classSpecBuild, interfaceAnnotationIntDefBuilder, index,
          itemAty);
      index++;
    }

    //定义Retention注释
    AnnotationSpec interfaceAnnotationRetention = AnnotationSpec.builder(Retention.class)
        .addMember("value", "$L", ClassName.get("java.lang.annotation.RetentionPolicy", "SOURCE"))
        .build();

    //定义并构建 AnnotationContractName 注解
    TypeSpec annotationIntent = TypeSpec.annotationBuilder(AnnotationContractName)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(interfaceAnnotationIntDefBuilder.build())//添加IntDef
        .addAnnotation(interfaceAnnotationRetention)//添加Retention
        .addJavadoc("作用于Source源文件的注释\n用于代码检查\n")
        .build();

    classSpecBuild.addModifiers(Modifier.PUBLIC).addType(annotationIntent);//添加注释字段

    classSpecBuild.addJavadoc("Created by $L on $L \n", Route.class.getSimpleName(),
        Utils.getNowTime())
        .addJavadoc("路由的契约类\n")
        .addJavadoc("功能：约定Activity类与int数值间的直接对应关系\n");

    return classSpecBuild.build();
  }

  private void addFieldAndAnnotationField(Builder classSpecBuild,
      AnnotationSpec.Builder interfaceAnnotationIntDefBuilder, int index, String itemAty) {
    //根据遍历 定义并构建 Aty同名int字段（自增）
    FieldSpec interfaceField = FieldSpec.builder(int.class, itemAty)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$L", index)
        .build();
    classSpecBuild.addField(interfaceField);//添加同名字段到契约类
    interfaceAnnotationIntDefBuilder.addMember("value", "$L", itemAty);//添加同名字段到注释中
  }
}
