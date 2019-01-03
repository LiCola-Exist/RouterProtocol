package com.licola.route.api;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.licola.route.api.exceptions.RouteBadChainException;
import com.licola.route.api.source.Source;

/**
 * @author LiCola
 * @date 2019/1/3
 */
public class ResolveInterceptor implements Interceptor {

  @Override
  public void intercept(Chain chain) {

    if (!(chain instanceof RealChain)) {
      chain.onBreak(new RouteBadChainException("错误的Chain链对象调用"));
      return;
    }

    RealChain realChain = (RealChain) chain;

    RouteRequest request = realChain.getRequest();

    Intent intent = request.getIntent();

    Source source = realChain.getSource();

    PackageManager packageManager = source.getContext().getPackageManager();
    ResolveInfo resolveInfo = packageManager
        .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    if (resolveInfo != null) {
      //有应用能直接解析
      source.startActivity(intent, request.getRequestCode(), request.getBundle());
      RouteResponse response = RouteResponse.createResolveResponse();
      realChain.setResponse(response);
    }

    chain.onProcess(request);
  }
}
