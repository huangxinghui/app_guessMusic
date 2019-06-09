package com.huangxinghui.games.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.huangxinghui.games.guessmusic.R;
import com.huangxinghui.games.guessmusic.util.Util;

/**
 * 通关界面
 */
public class AllPassView extends Activity {

    //返回按钮
    private ImageButton mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pass);

        //隐藏右上角的金币按钮
        FrameLayout frameLayout = findViewById(R.id.layout_bar_coin);
        frameLayout.setVisibility(View.INVISIBLE);

        //返回按钮
        mBtnBack = findViewById(R.id.btn_bar_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(AllPassView.this, IndexActivity.class);
            }
        });
    }


}
