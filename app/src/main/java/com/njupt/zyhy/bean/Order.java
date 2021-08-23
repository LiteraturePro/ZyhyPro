package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;

public class Order extends BmobObject {

    private String Name;

    private String id;

    private String Certificates;

    private String Number;

    private String Time;

    public void setName(String title) {
        this.Name = title;
    }

    public String getName() {
        return Name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setCertificates(String text) {
        this.Certificates = text;
    }

    public String getCertificates() {
        return Certificates;
    }

    public void setNumber(String text) {
        this.Number = text;
    }

    public String getNumber() {
        return Number;
    }

    public void setTime(String text) {
        this.Time = text;
    }

    public String getTime() {
        return Time;
    }

}
