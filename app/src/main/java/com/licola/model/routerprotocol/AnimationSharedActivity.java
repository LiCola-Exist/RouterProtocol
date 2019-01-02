package com.licola.model.routerprotocol;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * @author LiCola
 * @date 2019/1/2
 */
public class AnimationSharedActivity extends AppCompatActivity {

  public static final String KEY_IMAGE = "key:image";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shared);

    final ImageView imageView = findViewById(R.id.iv_big_cover);

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
      }, 1000);
    } else {
      loadImage(imageView);
    }
  }

  private void loadImage(ImageView imageView) {

    int valueImage = getIntent().getIntExtra(KEY_IMAGE, 0);
    imageView.setImageResource(valueImage);
  }
}
