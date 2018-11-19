package com.licola.route.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author LiCola
 * @date 2018/11/19
 */
public class UtilsTest {


  @org.junit.Test
  public void checkAndUpperFirstChar() {
    assertEquals("User", Utils.checkAndUpperFirstChar("user"));
    assertEquals("User", Utils.checkAndUpperFirstChar("User"));
    assertEquals("UserData", Utils.checkAndUpperFirstChar("userData"));
  }

  @org.junit.Test(expected = IllegalArgumentException.class)
  public void checkAndUpperFirstCharException() {
    Utils.checkAndUpperFirstChar("1Main");
  }

  @org.junit.Test
  public void classNameToUnderline() {
    assertEquals("MAIN", Utils.classNameToUnderline("Main"));
    assertEquals("MAIN_ATY", Utils.classNameToUnderline("MainAty"));
    assertEquals("MAIN_VIEW_ATY", Utils.classNameToUnderline("MainViewAty"));
  }

  @org.junit.Test
  public void getNowTime() {
    assertNotNull(Utils.getNowTime());
  }
}