package com.licola.model.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.licola.route.RouteModule;
import com.licola.route.annotation.Route;

@Route()
public class ModuleActivity extends AppCompatActivity {

  @BindView(R2.id.tv_hint)
  TextView tvHint;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_module);
    ButterKnife.bind(this);
    tvHint.setText("this module activity");

  }

  @OnClick(R2.id.bt_navigation)
  public void onViewClick(View view) {
    Log.d("module", "click");
  }
}
