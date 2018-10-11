package com.licola.model.routerprotocol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.licola.llogger.LLogger;
import com.licola.route.annotation.Route;

@Route(name = "second")
public class SecondActivity extends AppCompatActivity {

  private static final int RESULT_CODE=200;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      LLogger.d("带参数的Intent");
      for (String key : extras.keySet()) {
        LLogger.d(key, extras.get(key));
      }
    } else {
      LLogger.d("不带参数的Intent");
    }

    setResult(RESULT_CODE);
  }

}
