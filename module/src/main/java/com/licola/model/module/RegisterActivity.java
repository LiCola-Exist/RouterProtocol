package com.licola.model.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.licola.route.annotation.Route;

@Route(path = "register")
public class RegisterActivity extends AppCompatActivity {

  TextView tvHint;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    tvHint=findViewById(R.id.tv_hint);
    tvHint.setText("this register activity");


  }

  public void onNavigationClick(View view) {
    Log.d("module", "click");
  }




}
