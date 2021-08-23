package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;


public class Collect extends BmobObject {


    /**
     * 物品描述
     */
    private String Description;

    /**
     * 物品来源
     */
    private String Source;

    /**
     * 姓名
     */
    private  String Name;
    /**
     * 联系方式
     */
    private String Ways;



    public void setDescription(String Description ) {
        this.Description = Description;
    }
    public String getDescription() {
        return Description;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }
    public String getSource() {
        return Source;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public String getName() {
        return Name;
    }


    public void setWays(String Ways) {
        this.Ways = Ways;
    }
    public String getWays() {
        return Ways;
    }




}