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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

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

    final Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);

    if (CheckUtils.isEmpty(elements)) {
      return true;
    }

    if (moduleName == null) {
      error("请配置moduleName模块名称");
      throw new IllegalArgumentException("请配置moduleName模块名称 项目build.gradle android.defaultConfig \n"
          + "        javaCompileOptions {\n"
          + "            annotationProcessorOptions {\n"
          + "                arguments = [ moduleName : \"模块名称\" ]//或获取project.getName()\n"
          + "            }\n"
          + "        }");
    }

    checkPathRepeat(elements);

    final String className = ROUTE_CLASS_PREFIX + Utils.checkAndUpperFirstChar(moduleName);

    OutWriteCommand writeCommand = new OutWriteCommand() {
      @Override
      public void execute() throws IOException {
        TypeSpec typeSpec = ProcessorRoute
            .build(sortElementByName(elements), PACKAGE_BASE, className, moduleName)
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

  void error(String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg);
  }

  void error(Element element, String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg, element);
  }

  void error(Element element, AnnotationMirror annotation,
      String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg, element, annotation);
  }

  private List<? extends Element> sortElementByName(Set<? extends Element> elements) {
    ArrayList<? extends Element> sortList = new ArrayList<>(elements);
    sortList.sort(new Comparator<Element>() {
      @Override
      public int compare(Element e1, Element e2) {
        String name1 = fetchPath(e1);
        String name2 = fetchPath(e2);
        return name1.compareTo(name2);
      }
    });
    return sortList;
  }

  private void checkPathRepeat(Set<? extends Element> elements) {

    Map<String, Element> paths = new HashMap<>(elements.size());

    for (Element element : elements) {
      String key = fetchPath(element);
      Element old = paths.put(key, element);
      if (old != null) {
        error("存在重复的path定义");
        throw new IllegalArgumentException(
            String.format(Locale.CHINA, "@Route注解path=%s,%s和%s两者path定义重复", key, old, element));
      }
    }
  }

  private static String fetchPath(Element element) {

    String nameValue = element.getAnnotation(Route.class).path();

    if (CheckUtils.isEmpty(nameValue)) {
      return element.getSimpleName().toString();
    } else {
      return nameValue;
    }
  }
}
