package com.njupt.zyhy.unicloud;

public class UnicloudClass_Lost {

    /**
     * Text_title : 物品名称
     * user_id : 用户id
     * Text_address : 领取地址
     * Text_date : 发布时间
     * Text_user : 信息id
     * Lost_iamge : 物品图片
     * User_Get : 是否领取
     */
    private String Text_title;

    private String user_id;

    private String Text_address;

    private String Text_date;

    private String Text_user;

    private String Lost_iamge;

    private Boolean User_Get;


    public String getText_title() {
        return Text_title;
    }

    public void setText_title(String text_title) {
        Text_title = text_title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getText_address() {
        return Text_address;
    }

    public void setText_address(String text_address) {
        Text_address = text_address;
    }

    public String getText_date() {
        return Text_date;
    }

    public void setText_date(String text_date) {
        Text_date = text_date;
    }

    public String getText_user() {
        return Text_user;
    }

    public void setText_user(String text_user) {
        Text_user = text_user;
    }

    public String getLost_iamge() {
        return Lost_iamge;
    }

    public void setLost_iamge(String lost_iamge) {
        Lost_iamge = lost_iamge;
    }

    public Boolean getUser_Get() {
        return User_Get;
    }

    public void setUser_Get(Boolean user_Get) {
        User_Get = user_Get;
    }


}
