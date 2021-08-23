package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;

public class Collection extends BmobObject {

    /**
     * 物品ID
     */
    private String id;

    /**
     * 物品分类
     */
    private String Class;

    /**
     * 物品音频
     */
    private String Voice;

    /**
     * 物品名称
     */
    private  String Name;
    /**
     * 物品介绍
     */
    private String Introduce;
    /**
     * 物品图片
     */
    private String Pic1;
    private String Pic2;
    private String Pic3;

    public void setId(String id) {
        this.id = id ;
    }

    public String getId() {
        return id;
    }

    public void setClass(String Class ) {
        this.Class = Class;
    }

    public String getClasss() {
        return Class;
    }

    public void setVoice(String Voice) {
        this.Voice = Voice;
    }

    public String getVoice() {
        return Voice;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }
    public void setIntroduce(String Introduce) {
        this.Introduce = Introduce;
    }

    public String getIntroduce() {
        return Introduce;
    }
    public void setPic1(String Pic1) {
        this.Pic1 = Pic1;
    }

    public String getPic1() {
        return Pic1;
    }
    public void setPic2(String Pic2) {
        this.Pic2 = Pic2;
    }

    public String getPic2() {
        return Pic2;
    }
    public void setPic3(String Pic3) {
        this.Pic3 = Pic3;
    }

    public String getPic3() {
        return Pic3;
    }


}

