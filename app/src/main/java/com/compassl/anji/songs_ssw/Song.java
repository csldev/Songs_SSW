package com.compassl.anji.songs_ssw;

/**
 * Created by Administrator on 2017/10/28.
 */
public class Song {
    private String name;
    private int imgRes;

    public Song(String name, int imgRes) {
        this.name = name;
        this.imgRes = imgRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}