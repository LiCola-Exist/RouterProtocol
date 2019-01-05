package com.licola.model.routerprotocol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.licola.route.annotation.Route;

@Route(path = "second")
public class SecondActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);
    TextView textView = findViewById(R.id.tv_args);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      textView.append("带参数的Intent\n");
      for (String key : extras.keySet()) {
        textView.append("key:" + key + "/value:" + extras.get(key) + "\n");
      }
    } else {
      textView.append("不带参数的Intent\n");
    }
  }

  @Override
  public void onBackPressed() {
    Intent data = new Intent();
    data.putExtra("key1", "value1");
    data.putExtra("key2", 1000);
    setResult(Activity.RESULT_OK, data);
    super.onBackPressed();
  }
}
