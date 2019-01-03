package com.licola.model.routerprotocol;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.licola.llogger.LLogger;
import com.licola.route.RouteApp;
import com.licola.route.RouteApp.Route;
import com.licola.route.api.Api;
import com.licola.route.api.Chain;
import com.licola.route.api.Interceptor;
import com.licola.route.api.RouteRequest;
import com.licola.route.api.RouterApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


  private static final int FRAG_REQUEST_CODE = 101;


  public MainFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    rootView.findViewById(R.id.bt_navigation).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onNavigationRequestCodeFragmentClick();
      }
    });

    return rootView;
  }

  private void onNavigationRequestCodeFragmentClick() {

    Api api = new RouterApi.Builder(getActivity().getApplication())
        .addRouteRoot(new Route())
        .openDebugLog()
        .build();

    RouteApp.Api appApi = new RouteApp.Api(api);

    appApi.navigation(RouteApp.SECOND_ACTIVITY, new Interceptor() {
      @Override
      public void intercept(Chain chain) {
        chain.onProcess(new RouteRequest.Builder(chain.getRequest())
            .routeSource(MainFragment.this)
            .requestCode(FRAG_REQUEST_CODE)
            .build());
      }
    });

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Toast.makeText(getContext(), "Fragment收到结果", Toast.LENGTH_SHORT).show();
    LLogger.d(requestCode, resultCode, data);
  }
}
