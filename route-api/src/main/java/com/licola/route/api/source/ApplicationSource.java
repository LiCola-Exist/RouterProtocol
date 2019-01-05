package com.licola.route.api.source;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public final class ApplicationSource implements Source {

  private Application application;

  public ApplicationSource(Application application) {
    this.application = application;
  }

  @Override
  public Context getContext() {
    return application;
  }

  @Override
  public void startActivity(Intent intent, int requestCode, Bundle bundle) {
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    application.startActivity(intent, bundle);
  }
}
