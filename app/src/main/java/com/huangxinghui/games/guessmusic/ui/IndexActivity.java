package com.huangxinghui.games.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.huangxinghui.games.guessmusic.R;
import com.huangxinghui.games.guessmusic.data.Const;
import com.huangxinghui.games.guessmusic.util.Util;

public class IndexActivity extends Activity {

    //关于按钮
    private ImageButton mBoutButton;
    //猜图按钮
    private ImageButton mGuessPicButton;
    //当前关卡
    private TextView mCurrentStageTextView;
    //总关卡
    private TextView mTotalStageTextView;
    //开始猜歌按钮
    private ImageButton mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //关于按钮
        mBoutButton = findViewById(R.id.btn_index_about);
        mBoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IndexActivity.this, "welcome", Toast.LENGTH_SHORT).show();
            }
        });


        //猜图按钮
        mGuessPicButton = findViewById(R.id.btn_index_guessPic);
        mGuessPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IndexActivity.this, "coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        //读取游戏数据
        int[] datas = Util.loadData(this);
        int currentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        int totalStage = Const.SONG_INFO.length;

        //关卡数据
        mCurrentStageTextView = findViewById(R.id.text_index_current_stage);
        mCurrentStageTextView.setText((currentStageIndex)+ "");
        mTotalStageTextView = findViewById(R.id.text_index_total_stage);
        mTotalStageTextView.setText(totalStage + "");

        //开始猜歌按钮
        mPlayButton = findViewById(R.id.btn_index_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(IndexActivity.this, MainActivity.class);
            }
        });
    }


    /**
     * 禁用退回键
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
