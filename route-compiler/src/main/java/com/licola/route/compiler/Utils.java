package com.licola.route.compiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

/**
 * Created by LiCola on 2018/6/7.
 */
public class Utils {

  static String checkAndUpperFirstChar(String input) {
    char[] chars = input.toCharArray();
    char first = chars[0];
    if (Character.isJavaIdentifierStart(first)) {
      chars[0] = Character.toUpperCase(first);
    } else {
      throw new IllegalArgumentException(input + " is not java identifier start");
    }

    for (int i = 1; i < chars.length; i++) {
      if (!Character.isJavaIdentifierPart(chars[i])) {
        throw new IllegalArgumentException(input + " is not java identifier part");
      }
    }

    return new String(chars).intern();
  }

  /**
   * 类命名方式转常量命名方式
   * 如MainActivity->MAIN_ACTIVITY
   *
   * @param className UpperCamelCase命名方式
   * @return 常量命名方式 单词用下划线隔开
   */
  static String classNameToUnderline(String className) {
    StringBuilder builder = new StringBuilder();

    char[] chars = className.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char aChar = chars[i];
      if (i > 0 && Character.isUpperCase(aChar)) {
        builder.append('_');
      }
      builder.append(Character.toUpperCase(aChar));
    }

    return builder.toString();
  }

  static void error(Messager messager, String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg);
  }

  static void error(Messager messager, Element element, String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg, element);
  }

  static void error(Messager messager, Element element, AnnotationMirror annotation,
      String msg, Object... args) {
    if (args != null && args.length > 0) {
      msg = String.format(Locale.CHINA, msg, args);
    }
    messager.printMessage(Kind.ERROR, msg, element, annotation);
  }

  private static final String DATE_FORMAT_LOG_FILE = "yyyy/MM/dd HH:mm:ss";

  static String getNowTime() {
    return new SimpleDateFormat(DATE_FORMAT_LOG_FILE,
        Locale.CHINA).format(new Date());
  }

}
