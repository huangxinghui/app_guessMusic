package com.huangxinghui.games.guessmusic.data;

import java.util.Random;

/**
 * 全局配置
 */
public class Const {

    public static final int INDEX_FILE_NAME = 0;
    public static final int INDEX_SONG_NAME = 1;

    public static final int TOTAL_COINS = 1000;

    public static final String[][] SONG_INFO;

    static {
        String[][] tempData = {
            {"__00000.m4a","征服"},
            {"__00001.m4a","童话"},
            {"__00002.m4a","同桌的你"},
            {"__00003.m4a","七里香"},
            {"__00004.m4a","传奇"},
            {"__00005.m4a","大海"},
            {"__00006.m4a","后来"},
            {"__00007.m4a","你的背包"},
            {"__00008.m4a","再见"},
            {"__00009.m4a","老男孩"},
            {"__00010.m4a","龙的传人"},

            //重复歌曲，填充数量
            {"__00010.m4a","龙的传人"},
            {"__00008.m4a","再见"},
            {"__00002.m4a","同桌的你"},
            {"__00005.m4a","大海"},
            {"__00008.m4a","再见"},
            {"__00006.m4a","后来"},
            {"__00007.m4a","你的背包"},
            {"__00008.m4a","再见"},
            {"__00009.m4a","老男孩"}
        };

        //打乱顺序   随机
        for (int i = tempData.length - 1; i >= 0; i--) {
            int index = new Random().nextInt(i + 1);

            String[] buf = tempData[index];
            tempData[index] = tempData[i];
            tempData[i] = buf;
        }

        SONG_INFO = tempData;

    }

    //数据保存文件
    public static final String FILE_NAME_SAVE_DATA = "data.dat";
    public static final int INDEX_LOAD_DATA_STAGE = 0; //关卡索引
    public static final int INDEX_LOAD_DATA_COINS = 1; //金币索引
}
