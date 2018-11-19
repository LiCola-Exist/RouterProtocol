package com.licola.model.routerprotocol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.licola.llogger.LLogger;
import com.licola.route.annotation.Route;

@Route(path = "second")
public class SecondActivity extends AppCompatActivity {


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

    Intent data = new Intent();
    data.putExtra("key1", "value1");
    data.putExtra("key2", 1000);
    setResult(Activity.RESULT_OK, data);

  }


}
