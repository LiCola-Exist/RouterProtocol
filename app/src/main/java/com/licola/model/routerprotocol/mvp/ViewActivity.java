package com.licola.model.routerprotocol.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.licola.model.routerprotocol.R;
import com.licola.route.RouteApp;
import com.licola.route.annotation.Route;
import com.licola.route.api.Api;
import com.licola.route.api.RouterApi;

@Route
public class ViewActivity extends AppCompatActivity implements VPContract.View {

  private VPContract.Presenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view);

    Api api = new RouterApi.Builder(getApplication())
        .addRouteRoot(new RouteApp.Route())
        .build();

    presenter = new Presenter(this, api);
  }


  public void onNavigationSimpleClick(View view) {
    presenter.onNavigation();
  }

  public void onNavigationInterceptorClick(View view) {
    presenter.onNavigationInterceptor();
  }
}
