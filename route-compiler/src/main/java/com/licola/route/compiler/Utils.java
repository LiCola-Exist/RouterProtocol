package com.licola.route.compiler;

import com.google.auto.common.MoreElements;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

/**
 * Created by LiCola on 2018/6/7.
 */
public class Utils {

  public static String checkAndUpperFirstChar(String input) {
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

  public static PackageElement getPackageElement(Element elementItem) {
    return MoreElements
        .asPackage(MoreElements.asType(elementItem).getEnclosingElement());
  }

  private static final String DATE_FORMAT_LOG_FILE = "yyyy/MM/dd HH:mm:ss";

  public static String getNowTime() {
    return new SimpleDateFormat(DATE_FORMAT_LOG_FILE,
        Locale.CHINA).format(new Date());
  }

}
