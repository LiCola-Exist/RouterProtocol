package com.licola.model.routerprotocol;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author LiCola
 * @date 2019-06-21
 */
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    if (!LeakCanary.isInAnalyzerProcess(this)) {
      LeakCanary.install(this);
    }
  }
}
