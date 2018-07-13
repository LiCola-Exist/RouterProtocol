package com.licola.model.module.third;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 第三方库的Activity示例
 * 一般第三方库的内部Activity我们无法直接打上注解（因为无法直接修改源码）
 *
 */
public class ThirdActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);
  }
}
