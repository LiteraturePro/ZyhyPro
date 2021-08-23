package com.njupt.zyhy.bean;

import android.graphics.Bitmap;

public class W_Msg {

    private int id ;
    private Bitmap pic;
    private String title;
    private String pring;
    private String Mater;
    private String guge;

    public W_Msg(){

    }

    public W_Msg(int id, String title,Bitmap imgResId,String content,String Matre, String guge ) {
        this.id = id;
        this.pic = imgResId;
        this.title = title;
        this.pring = content;
        this.Mater = Matre;
        this.guge = guge;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImgResId() {
        return pic;
    }

    public void setImgResId(Bitmap imgResId) {
        this.pic = imgResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPring() {
        return pring;
    }

    public void setPring(String content) {
        this.pring = content;
    }
    public String getMater() {
        return Mater;
    }

    public void setMater(String content) {
        this.Mater = content;
    }
    public String getGuge() {
        return guge;
    }

    public void setGuge(String content) {
        this.guge = content;
    }
}
