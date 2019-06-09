package com.huangxinghui.games.guessmusic.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huangxinghui.games.guessmusic.R;
import com.huangxinghui.games.guessmusic.data.Const;
import com.huangxinghui.games.guessmusic.model.IAlertDialogButtonListener;
import com.huangxinghui.games.guessmusic.model.IWordButtonClickListener;
import com.huangxinghui.games.guessmusic.model.Song;
import com.huangxinghui.games.guessmusic.model.WordButton;
import com.huangxinghui.games.guessmusic.myui.MyGridView;
import com.huangxinghui.games.guessmusic.util.MyLog;
import com.huangxinghui.games.guessmusic.util.MyPlayer;
import com.huangxinghui.games.guessmusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseFullScreenActivity implements IWordButtonClickListener {

    public final static String TAG = "MainActivity";

    // 答案状态 —— 正确
    public final static int STATUS_ANSWER_RIGHT = 1;

    // 答案状态 —— 错误
    public final static int STATUS_ANSWER_WRONG = 2;

    // 答案状态 —— 不完整
    public final static int STATUS_ANSWER_LACK = 3;

    // 闪烁次数
    public final static int SPASH_TIMES = 6;

    public final static int ID_DIALOG_DELETE_WORD = 1;
    public final static int ID_DIALOG_TIP_ANSWER = 2;
    public final static int ID_DIALOG_LACK_COINS = 3;

    //唱片相关动画
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    private ImageView mViewPan;

    private ImageView mViewPanBar;

    //当前关索引
    private TextView mCurrentStagePassView;

    private TextView mCurrentStageView;

    //当前歌曲名称
    private TextView mCurrentSongNamePassView;

    //Play 按键事件
    private ImageButton mBtnPlayStart;

    //当前动画是否正在运行
    private Boolean mIsRunning = false;

    //文字框容器
    private List<WordButton> mAllWords;

    private List<WordButton> mBtnSelectWords;

    private MyGridView mMyGridView;

    //已选择文字框UI容器
    private LinearLayout mViewWordsContainer;

    //当前歌曲
    private Song mCurrentSong;

    //当前关索引
    private int mCurrentStageIndex = 0;

    // 过关界面
    private View mPassView;

    //当前金币数量
    private int mCurrentCoins = Const.TOTAL_COINS;

    //金币view
    private TextView mViewCurrentCoins;

    //返回按钮
    private ImageButton mBtnBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //读取游戏数据
        int[] datas = Util.loadData(this);
        mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

        mViewPan = findViewById(R.id.imageView1);
        mViewPanBar = findViewById(R.id.imageView2);

        mMyGridView = findViewById(R.id.gridview);

        mViewCurrentCoins = findViewById(R.id.text_bar_coin);
        mViewCurrentCoins.setText(mCurrentCoins+"");


        //注册监听
        mMyGridView.registOnWordButtonClick(this);

        mViewWordsContainer = findViewById(R.id.word_select_container);

        //初始化动画
        mPanAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mBarInLin);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPanBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarInAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarOutAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBtnPlayStart =  findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayButton();
            }
        });

        //初始化游戏数据
        initCurrentStageData(mCurrentStageIndex<=0? ++mCurrentStageIndex : mCurrentStageIndex);

        //处理删除按键事件
        handleDeleteWord();

        //处理提示按键事件
        handleTipAnswer();

        //返回按钮
        mBtnBack = findViewById(R.id.btn_bar_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(MainActivity.this, IndexActivity.class);
            }
        });
    }

    private void handlePlayButton(){
        if(mViewPanBar != null){
            if(!mIsRunning){
                mIsRunning = true;

                //开始拨杆进入动画
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);

                //播放音乐
                MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());

            }
        }
    }

    @Override
    protected void onPause() {

        //保存游戏数据
        Util.saveData(MainActivity.this, mCurrentStageIndex, mCurrentCoins);

        mViewPan.clearAnimation();

        //暂停音乐
        MyPlayer.stopTheSong(MainActivity.this);

        super.onPause();
    }

    private Song loadStageSongInfo(int stageIndex){
        Song song = new Song();
        String[] stage = Const.SONG_INFO[stageIndex];
        song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
        song.setSongName(stage[Const.INDEX_SONG_NAME]);
        return song;
    }


    /**
     * 加载关卡的数据
     * @param stageIndex
     */
    private void initCurrentStageData(int stageIndex){

        //显示最新的金币数据
        mViewCurrentCoins = findViewById(R.id.text_bar_coin);
        if(mViewCurrentCoins != null){
            mViewCurrentCoins.setText(mCurrentCoins+"");
        }

        //读取当前关歌曲的信息
        mCurrentSong = loadStageSongInfo(stageIndex-1);

        //初始化已选择框
        mBtnSelectWords = initWordSelect();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140,140);

        //清空原来的答案
        mViewWordsContainer.removeAllViews();

        //增加新的答案框
        for(int i = 0; i < mBtnSelectWords.size(); i++){
            mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
        }

        //显示当前关的索引
        mCurrentStageView = findViewById(R.id.text_current_stage);
        if(mCurrentStageView != null){
            mCurrentStageView.setText(stageIndex + "");
        }

        //获得数据
        mAllWords = initAllWord();

        //更新数据  MyGridView
        mMyGridView.updateData(mAllWords);

        //一开始播放音乐
        handlePlayButton();
    }


    /**
     * 初始化待选文字框
     * @return
     */
    private List<WordButton> initAllWord(){
        List<WordButton> data = new ArrayList<>();

        //获得所有待选文字
        String[] words = generateWords();

        for (int i = 0; i< MyGridView.COUNTS_WORDS; i++){
            WordButton button = new WordButton();
            button.mWordString = words[i];
            data.add(button);
        }
        return data;
    }

    /**
     * 初始化已选择文字框
     * @return
     */
    private List<WordButton> initWordSelect(){
        List<WordButton> data = new ArrayList<>();

        for (int i = 0; i< mCurrentSong.getNameLength(); i++){
            View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);

            final WordButton holder = new WordButton();
            holder.mViewButton = view.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            holder.mIsVisiable = false;

            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            holder.mViewButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    clearTheAnswer(holder);
                }
            });
            data.add(holder);
        }

        return data;
    }

    @Override
    public void onWordButtonClick(WordButton wordButton) {
        setSelectWord(wordButton);

        // 获得答案状态
        int checkResult = checkTheAnswer();

        // 检查答案
        if (checkResult == STATUS_ANSWER_RIGHT) {
            // 过关并获得奖励
			//Toast.makeText(this, "STATUS_ANSWER_RIGHT", Toast.LENGTH_SHORT).show();
            handlePassEvent();
        } else if (checkResult == STATUS_ANSWER_WRONG) {
            // 闪烁文字并提示用户
            sparkTheWrods();
        } else if (checkResult == STATUS_ANSWER_LACK) {
            // 设置文字颜色为白色（Normal）
            for (int i = 0; i < mBtnSelectWords.size(); i++) {
                mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
            }
        }

    }


    /**
     * 处理过关界面及事件
     */
    private void handlePassEvent() {
        //显示过关页面
        mPassView = this.findViewById(R.id.pass_view);
        mPassView.setVisibility(View.VISIBLE);

        //停止未完成的动画
        mViewPan.clearAnimation();

        //停止正在运行的音乐
        MyPlayer.stopTheSong(MainActivity.this);

        //播放音效
        MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);

        //当前关的索引
        mCurrentStagePassView = findViewById(R.id.text_current_stage_pass);
        if(mCurrentStagePassView != null){
            mCurrentStagePassView.setText(mCurrentStageIndex + "");
        }

        //显示歌曲名称
        mCurrentSongNamePassView = findViewById(R.id.text_current_song_name_pass);
        if(mCurrentSongNamePassView != null){
            mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
        }

        //过关奖励
        addCoinHandle(getPassStageCoins());

        //下一关按键处理
        ImageButton btnPass = findViewById(R.id.btn_next);
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通关或下一关
                if(judgeAppPassed()){
                    //进入通关界面
                    Util.startActivity(MainActivity.this, AllPassView.class);
                }else{
                    //开始下一关
                    mPassView.setVisibility(View.GONE);

                    //加载关卡数据
                    initCurrentStageData(++mCurrentStageIndex);
                }
            }
        });

    }

    /**
     * 判断是否通关
     * @return
     */
    private boolean judgeAppPassed(){
        return (mCurrentStageIndex == Const.SONG_INFO.length);
    }


    private void clearTheAnswer(WordButton wordButton) {
        wordButton.mViewButton.setText("");
        wordButton.mWordString = "";
        wordButton.mIsVisiable = false;

        // 设置待选框可见性
        setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
    }


    /**
     * 设置答案
     * @param wordButton
     */
    private void setSelectWord(WordButton wordButton){
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {
                // 设置答案文字框内容及可见性
                mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
                mBtnSelectWords.get(i).mIsVisiable = true;
                mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
                // 记录索引
                mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

                MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

                // 设置待选框可见性
                setButtonVisiable(wordButton, View.INVISIBLE);
                break;
            }
        }
    }

    /**
     * 设置待选文字框是否可见
     *
     * @param button
     * @param visibility
     */
    private void setButtonVisiable(WordButton button, int visibility) {
        button.mViewButton.setVisibility(visibility);
        button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

        MyLog.d(TAG, button.mIsVisiable + "");
    }



    /**
     * 生成所有的待选文字
     *
     * @return
     */
    private String[] generateWords() {
        Random random = new Random();

        String[] words = new String[MyGridView.COUNTS_WORDS];

        // 存入歌名
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }

        // 获取随机文字并存入数组
        for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
            words[i] = getRandomChar() + "";
        }

        // 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
        // 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
        // 这样能够确保每个元素在每个位置的概率都是1/n。
        for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);

            String buf = words[index];
            words[index] = words[i];
            words[i] = buf;
        }

        return words;
    }

    /**
     * 生成随机汉字
     * http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
     * @return
     */
    private char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    /**
     * 检查答案
     *
     * @return
     */
    private int checkTheAnswer() {
        // 先检查长度
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            // 如果有空的，说明答案还不完整
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {
                return STATUS_ANSWER_LACK;
            }
        }

        // 答案完整，继续检查正确性
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            sb.append(mBtnSelectWords.get(i).mWordString);
        }

        return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
    }

    /**
     * 文字闪烁
     */
    private void sparkTheWrods() {
        // 定时器相关
        TimerTask task = new TimerTask() {
            boolean mChange = false;
            int mSpardTimes = 0;

            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (++mSpardTimes > SPASH_TIMES) {
                            return;
                        }

                        // 执行闪烁逻辑：交替显示红色和白色文字
                        for (int i = 0; i < mBtnSelectWords.size(); i++) {
                            mBtnSelectWords.get(i).mViewButton.setTextColor(mChange ? Color.RED : Color.WHITE);
                        }

                        mChange = !mChange;
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    /**
     * 处理删除待选文字事件
     */
    private void handleDeleteWord() {
        ImageButton button = findViewById(R.id.btn_delete_word);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //deleteOneWord();
                showConfirmDialog(ID_DIALOG_DELETE_WORD);
            }
        });
    }

    /**
     * 删除文字
     */
    private void deleteOneWord() {
        // 减少金币
        if (!handleCoins(-getDeleteWordCoins())) {
            // 金币不够，显示提示对话框
            showConfirmDialog(ID_DIALOG_LACK_COINS);
            return;
        }

        // 将这个索引对应的WordButton设置为不可见
        setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
    }

    /**
     * 增加或者减少指定数量的金币
     *
     * @param data
     * @return true 增加/减少成功，false 失败
     */
    private boolean handleCoins(int data) {
        // 判断当前总的金币数量是否可被减少
        if (mCurrentCoins + data >= 0) {
            mCurrentCoins += data;

            mViewCurrentCoins.setText(mCurrentCoins + "");

            return true;
        } else {
            // 金币不够
            return false;
        }
    }

    /**
     * 从配置文件里读取删除操作所要用的金币
     *
     * @return
     */
    private int getDeleteWordCoins() {
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }

    /**
     * 从配置文件里读取提示操作所要用的金币
     *
     * @return
     */
    private int getTipCoins() {
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    /**
     * 从配置文件里读取过关奖励的金币
     *
     * @return
     */
    private int getPassStageCoins() {
        return this.getResources().getInteger(R.integer.pass_stage_coin);
    }


    /**
     * 找到一个不是答案的文件，并且当前是可见的
     *
     * @return
     */
    private WordButton findNotAnswerWord() {
        Random random = new Random();
        WordButton buf = null;

        while(true) {
            int index = random.nextInt(MyGridView.COUNTS_WORDS);

            buf = mAllWords.get(index);

            if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
                return buf;
            }
        }
    }

    /**
     * 找到一个答案文字
     *
     * @param index 当前需要填入答案框的索引
     * @return
     */
    private WordButton findIsAnswerWord(int index) {
        WordButton buf = null;

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            buf = mAllWords.get(i);

            if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
                return buf;
            }
        }

        return null;
    }


    /**
     * 判断某个文字是否为答案
     *
     * @param word
     * @return
     */
    private boolean isTheAnswerWord(WordButton word) {
        boolean result = false;

        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            if (word.mWordString.equals("" + mCurrentSong.getNameCharacters()[i])) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 处理提示按键事件
     */
    private void handleTipAnswer() {
        ImageButton button = findViewById(R.id.btn_tip_answer);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showConfirmDialog(ID_DIALOG_TIP_ANSWER);
                //tipAnswer();
            }
        });
    }

    /**
     * 自动选择一个答案
     */
    private void tipAnswer() {
        boolean tipWord = false;
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {

                // 减少金币数量
                if (!handleCoins(-getTipCoins())) {
                    // 金币数量不够，显示对话框
                    showConfirmDialog(ID_DIALOG_LACK_COINS);
                    return;
                }

                // 根据当前的答案框条件选择对应的文字并填入
                onWordButtonClick(findIsAnswerWord(i));

                tipWord = true;


                break;
            }
        }

        // 没有找到可以填充的答案
        if (!tipWord) {
            // 闪烁文字提示用户
            sparkTheWrods();
        }
    }

    //自定义AlertDialog事件响应
    //删除错误答案
    private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件
            deleteOneWord();
        }
    };

    //答案提示
    private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件
            tipAnswer();
        }
    };

    //金币不足
    private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            //执行事件

        }
    };

    /**
     * 显示对话框
     * @param id
     */
    private void showConfirmDialog(int id){
        switch (id){
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins() +"个金币去掉一个错误答案？", mBtnOkDeleteWordListener );
                break;
            case ID_DIALOG_TIP_ANSWER:
                Util.showDialog(MainActivity.this, "确认花掉" + getTipCoins() +"个金币获得一个文字提示？", mBtnOkTipAnswerListener );
                break;
            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this, "金币不足,去商店补充？", mBtnOkLackCoinsListener );
                break;
        }
    }

    /**
     * 过关奖励
     * @param addCoins
     */
    private void addCoinHandle(int addCoins){
        mCurrentCoins += addCoins;
    }

}