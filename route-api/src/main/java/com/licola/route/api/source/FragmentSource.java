package com.licola.route.api.source;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public final class FragmentSource implements Source {

  private Fragment fragment;

  public FragmentSource(Fragment fragment) {
    this.fragment = fragment;
  }

  @Override
  public Context getContext() {
    return fragment.getContext();
  }

  @Override
  public void startActivity(Intent intent, int requestCode, Bundle bundle) {
    fragment.startActivityForResult(intent, requestCode,bundle);
  }
}
