package com.licola.model.routerprotocol.mvp;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import com.licola.llogger.LLogger;
import com.licola.route.api.Api;
import com.licola.route.api.Chain;
import com.licola.route.api.Interceptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author LiCola
 * @date 2018/7/23
 */
public class PresenterTest {

  @Mock
  VPContract.View view;

  @Mock
  Api api;

  @InjectMocks
  Presenter presenter;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * MVP模式中 P层作为控制层，理应控制界面跳转
   * 使用路由协议，可以verify验证api的navigation跳转方法和参数
   */
  @Test
  public void onNavigation() {
    presenter.onNavigation();
    verify(api).navigation("app/second");
  }

  @Test
  public void onNavigationInterceptor() {
    presenter.onNavigationInterceptor();
    ArgumentCaptor<Interceptor> captor = ArgumentCaptor.forClass(Interceptor.class);
    verify(api).navigation(eq("app/second"), captor.capture());
    Chain chain = mock(Chain.class);
    captor.getValue().intercept(chain);
    verify(chain).onProcess();
  }
}