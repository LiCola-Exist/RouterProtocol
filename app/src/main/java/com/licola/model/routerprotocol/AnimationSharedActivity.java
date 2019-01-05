package com.licola.model.routerprotocol;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
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
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
    setContentView(R.layout.activity_shared);

    final ImageView imageView = findViewById(R.id.iv_big_cover);
    ViewCompat.setTransitionName(imageView, "cover");
    supportPostponeEnterTransition();

    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      //手动停止 进入动画
      Transition transition = getWindow().getSharedElementEnterTransition();
      transition.setDuration(1000);
      transition.addListener(new TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {
          LLogger.d();
        }

        @Override
        public void onTransitionEnd(Transition transition) {
          LLogger.d();
        }

        @Override
        public void onTransitionCancel(Transition transition) {
          LLogger.d();

        }

        @Override
        public void onTransitionPause(Transition transition) {
          LLogger.d();

        }

        @Override
        public void onTransitionResume(Transition transition) {
          LLogger.d();
        }
      });

      imageView.postDelayed(new Runnable() {
        @Override
        public void run() {
          LLogger.d();
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
    LLogger.d("获取传递来的参数:" + valueImage);
    imageView.setImageResource(valueImage);
  }
}
