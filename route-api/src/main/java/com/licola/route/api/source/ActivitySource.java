package com.licola.route.api.source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author LiCola
 * @date 2019/1/2
 */
public final class ActivitySource implements Source {

  private Activity activity;

  public ActivitySource(Activity activity) {
    this.activity = activity;
  }

  @Override
  public Context getContext() {
    return activity;
  }

  @Override
  public void startActivity(Intent intent, int requestCode, Bundle bundle) {
    activity.startActivityForResult(intent, requestCode,bundle);
  }
}
