package com.licola.route.api;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.licola.route.annotation.RoutePath;
import com.licola.route.api.source.ActivitySource;
import com.licola.route.api.source.ApplicationSource;
import com.licola.route.api.source.FragmentSource;
import com.licola.route.api.source.Source;

/**
 * 路由请求体的封装
 *
 * @author LiCola
 * @date 2018/7/23
 */
public class RouteRequest {

  public static final int STANDARD_REQUEST_CODE = Activity.RESULT_OK;
  @NonNull
  private final Source source;
  private final int requestCode;

  @Nullable
  private final String routePath;
  @NonNull
  private final Intent intent;
  @Nullable
  private final Bundle bundle;


  private RouteRequest(Builder builder) {
    this.source = builder.source;
    this.requestCode = builder.requestCode;
    this.routePath = builder.routePath;
    this.intent = builder.intent;
    this.bundle = builder.bundle;
  }

  @NonNull
  public Source getSource() {
    return source;
  }

  public int getRequestCode() {
    return requestCode;
  }

  @Nullable
  public String getRoutePath() {
    return routePath;
  }

  @NonNull
  public Intent getIntent() {
    return intent;
  }

  @Nullable
  public Bundle getBundle() {
    return bundle;
  }

  public static class Builder {

    private int requestCode;
    private String routePath;
    private Intent intent;
    private Bundle bundle;
    private Source source;

    public Builder(Source source) {
      this.source = source;
      this.requestCode = STANDARD_REQUEST_CODE;
      this.intent = new Intent();
    }

    public Builder(Application application) {
      this(new ApplicationSource(application));
    }

    public Builder(Activity activity) {
      this(new ActivitySource(activity));
    }

    public Builder(Fragment fragment) {
      this(new FragmentSource(fragment));
    }

    public Builder routePath(String routePath) {
      this.routePath = routePath;
      return this;
    }

    public Builder routePath(String module, String target) {
      this.routePath = RoutePath.makePath(module, target);
      return this;
    }

    public Builder routeSource(Source source) {
      this.source = source;
      return this;
    }

    public Builder routeSource(Application application) {
      return routeSource(new ApplicationSource(application));
    }

    public Builder routeSource(Activity activity) {
      return routeSource(new ActivitySource(activity));
    }

    public Builder routeSource(Fragment fragment) {
      return routeSource(new FragmentSource(fragment));
    }

    public Builder requestCode(int requestCode) {
      this.requestCode = requestCode;
      return this;
    }

    public Builder putIntent(Bundle extras) {
      if (extras != null) {
        this.intent.putExtras(extras);
      }
      return this;
    }

    public Builder putBundle(Bundle extras) {
      if (extras != null) {
        if (this.bundle == null) {
          this.bundle = new Bundle();
        }
        this.bundle.putAll(extras);
      }
      return this;
    }

    public Builder(RouteRequest request) {
      this.source = request.source;
      this.requestCode = request.requestCode;
      this.routePath = request.routePath;
      this.intent = request.intent;
      this.bundle = request.bundle;
    }

    public RouteRequest build() {
      return new RouteRequest(this);
    }
  }
}
