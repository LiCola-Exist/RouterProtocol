package com.licola.model.routerprotocol;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.licola.llogger.LLogger;
import com.licola.route.annotation.Route;

/**
 * @author LiCola
 * @date 2019/1/2
 */
@Route(path = "animation/shared", description = "共享元素动画界面")
public class AnimationSharedActivity extends AppCompatActivity {

  public static final String KEY_IMAGE = "key:image";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shared);

    final ImageView imageView = findViewById(R.id.iv_big_cover);
    ViewCompat.setTransitionName(imageView, "cover");
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      //手动停止 进入动画
      postponeEnterTransition();
      imageView.postDelayed(new Runnable() {
        @Override
        public void run() {
          loadImage(imageView);
          //延迟执行进入动画 可以作为图片加载时间等
          supportStartPostponedEnterTransition();
        }
      }, 200);
    } else {
      loadImage(imageView);
    }
  }

  @Override
  public void onBackPressed() {
    LLogger.d();
    //结束时 调起退出动画 新的sdk已经默认调起
    super.onBackPressed();
  }

  private void loadImage(ImageView imageView) {

    int valueImage = getIntent().getIntExtra(KEY_IMAGE, 0);
    imageView.setImageResource(valueImage);
  }
}
