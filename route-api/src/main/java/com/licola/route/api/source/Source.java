package com.licola.route.api.source;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public abstract class Source {


  public abstract Context getContext();

  public abstract void startActivity(Intent intent, int requestCode, Bundle bundle);


  private static Source createSource(Application application, Activity activity,
      Fragment fragment) {

    if (fragment != null) {
      return new FragmentSource(fragment);
    } else if (activity != null) {
      return new ActivitySource(activity);
    } else if (application != null) {
      return new ApplicationSource(application);
    }

    return null;
  }
}
