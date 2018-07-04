package com.licola.route.compiler;

import com.google.auto.common.MoreElements;
import com.licola.route.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.HashMap;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by LiCola on 2017/6/21.
 */

public class ProcessorRouteMapper {

  private static final String FieldMapperName = "router";//映射类字段名
  private static final String MethodMapperName = "mapperActivity";//映射类方法名

  public Set<? extends Element> elements;

  public static ProcessorRouteMapper build(Set<? extends Element> elements) {
    return new ProcessorRouteMapper(elements);
  }

  public ProcessorRouteMapper(Set<? extends Element> elements) {
    this.elements = elements;
  }

  public TypeSpec process(String className,  ClassName classNameContract,  ClassName classNameContractAnnotation) {
    if (CheckUtils.isEmpty(elements)) {
      return null;
    }

    //定义静态代码块
    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
    for (Element element : elements) {
      String itemAty = element.getSimpleName().toString();

      String elementPackageName =
          MoreElements.asType(element).getQualifiedName().toString();//获得Aty的全名称

      codeBlockBuilder.add("$L.put($L.$L,$L.class);\n ", FieldMapperName,
          classNameContract.simpleName(), itemAty, elementPackageName)
          .build();//在静态代码块中 添加代码行 构成int-Activity映射关系
    }

    //定义并构建 方法参数
    ParameterSpec targetParameter = ParameterSpec.builder(int.class, "target")
        .addModifiers(Modifier.FINAL)
        .addAnnotation(classNameContractAnnotation)//获取契约类的注释
        .build();

    //定义并构建 方法
    MethodSpec getClassMethod = MethodSpec.methodBuilder(MethodMapperName)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addAnnotation(ClassName.get("android.support.annotation", "Nullable"))
        .addParameter(targetParameter)
        .addStatement(" return $L.get(target) != null ? $L.get(target) : null", FieldMapperName,
            FieldMapperName)
        .returns(Class.class)
        .build();

    int mapInitSize = getMapCapacity(elements.size());

    //定义并构建 静态字段
    FieldSpec hashMapField =
        FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, Integer.class, Class.class),
            FieldMapperName, Modifier.PRIVATE, Modifier.STATIC)
            .initializer("new HashMap<>($L)", mapInitSize)//init with size
            .build();

    TypeSpec.Builder classSpecBuild = TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC)
        .addField(hashMapField)//添加字段
        .addMethod(getClassMethod);//添加方法

    classSpecBuild.addStaticBlock(codeBlockBuilder.build());
    classSpecBuild.addJavadoc("Created by $L on $L \n", Route.class.getSimpleName(),
        Utils.getNowTime())
        .addJavadoc("路由表\n")
        .addJavadoc("功能:通过int值获取Activity类\n");

    return classSpecBuild.build();
  }


  public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

  private int getMapCapacity(int fixSize) {
    if (fixSize < 3) {
      return fixSize + 1;
    }

    if (fixSize < MAX_POWER_OF_TWO) {
      return fixSize + fixSize / 3;
    }
    return Integer.MAX_VALUE;
  }
}
