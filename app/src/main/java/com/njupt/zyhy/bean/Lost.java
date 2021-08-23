package com.njupt.zyhy.bean;


import cn.bmob.v3.BmobObject;

public class Lost extends BmobObject {

    /**
     * 图片地址
     */
    private String Lost_picsrc;

    /**
     * 物品名称
     */
    private String Lost_title;

    /**
     * 领取地点
     */
    private String Lost_address;


    public void setLost_picsrc(String picsrc){
        this.Lost_picsrc = picsrc;
    }

    public String getLost_picsrc(){
        return Lost_picsrc;
    }

    public void setLost_title(String title){
        this.Lost_title = title;
    }
    public  String getLost_title(){
        return Lost_title;
    }

    public void setLost_address(String address){
        this.Lost_address = address;
    }
    public  String getLost_address(){
        return Lost_address;
    }



}