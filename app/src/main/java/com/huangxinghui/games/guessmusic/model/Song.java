package com.huangxinghui.games.guessmusic.model;

public class Song {

    //歌曲名称
    private String mSongName;

    //歌曲文件名称
    private String mSongFileName;

    //歌曲名字长度
    private int mNameLength;

    public char[] getNameCharacters(){
        return mSongName == null ? null : mSongName.toCharArray();
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;

        this.mNameLength = (songName == null? 0 : songName.length());
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String songFileName) {
        this.mSongFileName = songFileName;
    }

    public int getNameLength() {
        return mNameLength;
    }

    public void setNameLength(int nameLength) {
        this.mNameLength = nameLength;
    }
}
