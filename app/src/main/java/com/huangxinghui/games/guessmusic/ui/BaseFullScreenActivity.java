package com.huangxinghui.games.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;

import com.huangxinghui.games.guessmusic.util.NavigationBarUtil;

/**
 * 全屏activity
 */
public class BaseFullScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏，隐藏虚拟按键
        NavigationBarUtil.hideNavigationBar(getWindow());
    }
}
