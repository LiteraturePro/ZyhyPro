package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;

public class Feedbacks extends BmobObject {

    private String messages;     // 信息


    public String getMessages() {

        return messages;
    }

    public void setMessages(String messages) {

        this.messages = messages;
    }
}