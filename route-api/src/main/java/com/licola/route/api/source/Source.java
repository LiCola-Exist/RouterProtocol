package com.licola.route.api.source;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author LiCola
 * @date 2018/11/16
 */
public interface Source {

  Context getContext();

  void startActivity(Intent intent, int requestCode, Bundle bundle);

}
