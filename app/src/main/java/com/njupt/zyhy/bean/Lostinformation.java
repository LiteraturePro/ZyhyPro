package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;

public class Lostinformation extends BmobObject {

    /**
     * 消息头部
     */
    private String title;

    /**
     * 消息内容
     */
    private String address;

    private String bit;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAddress(String text) {
        this.address = text;
    }

    public String getAddress() {
        return address;
    }

    public void setBit(String text) {
        this.bit = text;
    }

    public String getBit() {
        return bit;
    }

}
