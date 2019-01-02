package com.licola.route.api.source;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public abstract class Source {


  public abstract Context getContext();

  public abstract void startActivity(Intent intent, int requestCode, Bundle bundle);


  /**
   * 该Intent能否被PackageManager隐式解析 如通过 setAction() 设置，使得能够被外部解析
   *
   * @return true:能够被隐式解析
   */
  public final boolean isResolveNotDeclareIntent(@Nullable Intent intent) {
    if (intent == null) {
      return false;
    }

    PackageManager packageManager = getContext().getPackageManager();
    ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return resolveInfo != null;
  }
}
