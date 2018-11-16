package com.licola.route.api.source;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public class FragmentSource extends Source {

  private FragmentActivity fragmentActivity;
  private Fragment fragment;

  public FragmentSource(FragmentActivity fragmentActivity, Fragment fragment) {
    this.fragmentActivity = fragmentActivity;
    this.fragment = fragment;
  }

  @Override
  public Context getContext() {
    return fragmentActivity;
  }

  @Override
  public void startActivity(Intent intent, int requestCode) {
    if (fragment != null) {
      fragmentActivity.startActivityFromFragment(fragment, intent, requestCode);
    } else {
      fragmentActivity.startActivityForResult(intent, requestCode);
    }
  }
}
