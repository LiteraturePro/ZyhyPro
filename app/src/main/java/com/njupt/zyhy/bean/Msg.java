package com.njupt.zyhy.bean;

import android.graphics.Bitmap;

public class Msg {

    private int id ;
    private Bitmap imgResId;
    private String title;
    //private String content;

    public Msg(){

    }

    public Msg(int id, Bitmap imgResId, String title) {
        this.id = id;
        this.imgResId = imgResId;
        this.title = title;
        //this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImgResId() {
        return imgResId;
    }

    public void setImgResId(Bitmap imgResId) {
        this.imgResId = imgResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
