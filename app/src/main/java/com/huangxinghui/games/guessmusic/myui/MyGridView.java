package com.huangxinghui.games.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.huangxinghui.games.guessmusic.R;
import com.huangxinghui.games.guessmusic.model.IWordButtonClickListener;
import com.huangxinghui.games.guessmusic.model.WordButton;
import com.huangxinghui.games.guessmusic.util.Util;

import java.util.ArrayList;
import java.util.List;


public class MyGridView extends GridView {

    public static final int COUNTS_WORDS = 24;
    private List<WordButton> mArrayList = new ArrayList<>();
    private MyGridAdapter mAdapter;
    private Context mContext;

    private Animation mScaleAnimation;
    private IWordButtonClickListener mWordButtonClickListener;

    public MyGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        mAdapter = new MyGridAdapter();
        this.setAdapter(mAdapter);
    }

    public void updateData(List<WordButton> list){
        mArrayList = list;

        //重新设置数据源
        setAdapter(mAdapter);
    }


    class MyGridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup p) {
            final WordButton holder;

            if(v == null){
                v = Util.getView(mContext, R.layout.self_ui_gridview_item);
                holder = mArrayList.get(position);

                //加载动画
                mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
                //设置动画的延迟时间
                mScaleAnimation.setStartOffset(position * 100);

                holder.mIndex = position;
                holder.mViewButton = v.findViewById(R.id.item_btn);
                holder.mViewButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWordButtonClickListener.onWordButtonClick(holder);
                    }
                });
                v.setTag(holder);
            }else {
                holder = (WordButton) v.getTag();
            }

            holder.mViewButton.setText(holder.mWordString);

            //播放动画
            v.startAnimation(mScaleAnimation);
            return v;
        }
    }

    /**
     * 注册监听接口
     * @param listener
     */
    public void registOnWordButtonClick(IWordButtonClickListener listener){
        mWordButtonClickListener = listener;
    }
}
