package com.huangxinghui.games.guessmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import com.huangxinghui.games.guessmusic.R;
import com.huangxinghui.games.guessmusic.util.Util;

/**
 * 欢迎界面
 */
public class SplashActivity extends BaseFullScreenActivity {

    private final int SPLASH_DISPLAY_LENGHT = 3000;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();

        //3秒之后启动MainActivity页面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.startActivity(SplashActivity.this, IndexActivity.class);
            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    /**
     * splash 禁用退回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
