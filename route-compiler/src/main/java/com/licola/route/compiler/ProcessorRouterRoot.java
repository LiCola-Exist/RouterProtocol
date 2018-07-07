package com.licola.route.compiler;

import static com.licola.route.compiler.Constants.KEY_MODULE_NAME;
import static com.licola.route.compiler.Constants.PACKAGE_BASE;
import static com.licola.route.compiler.Constants.ROUTE_CLASS_PREFIX;

import com.google.auto.service.AutoService;
import com.licola.route.annotation.Route;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by LiCola on 2017/6/21.
 *
 * 注解生成代码：生成目录在项目build包下，和目标类同级
 */
@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ProcessorRouterRoot extends AbstractProcessor {

  private Filer filer;
  private Messager messager;
  private String moduleName;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    filer = processingEnvironment.getFiler();
    messager = processingEnvironment.getMessager();

    moduleName = processingEnvironment.getOptions().get(KEY_MODULE_NAME);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
    annotations.add(Route.class);
    return annotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      final RoundEnvironment roundEnvironment) {

    if (moduleName == null) {
      Utils.error(messager, "请配置moduleName模块名称");
      throw new IllegalArgumentException("请配置moduleName模块名称 项目build.gradle android.defaultConfig \n"
          + "        javaCompileOptions {\n"
          + "            annotationProcessorOptions {\n"
          + "                arguments = [ moduleName : \"模块名称\" ]//或获取project.getName()\n"
          + "            }\n"
          + "        }");
    }

    final Set<? extends Element> elements = roundEnvironment
        .getElementsAnnotatedWith(Route.class);

    if (CheckUtils.isEmpty(elements)) {
      return true;
    }

    String noduleUpName = Utils.checkAndUpperFirstChar(moduleName);
    final String className = ROUTE_CLASS_PREFIX + noduleUpName;

    OutWriteCommand writeCommand = new OutWriteCommand() {
      @Override
      public void execute() throws IOException {
        TypeSpec typeSpec = ProcessorRoute
            .build(elements, PACKAGE_BASE, className, moduleName)
            .process();

        if (typeSpec == null) {
          return;
        }

        JavaFile javaFile = JavaFile.builder(PACKAGE_BASE, typeSpec).build();
        javaFile.writeTo(filer);
      }
    };

    try {
      writeCommand.execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return true;
  }


}
