package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobObject;

public class Exhibit extends BmobObject {
    private String Title,Subtitle,Text,Pic1,Pic2,Pic3,Pic4;

    public void setTitle(String Title){
        this.Title = Title;
    }

    public String getTitle(){
        return Title;
    }

    public void setSubtitle(String Subtitle){
        this.Subtitle = Subtitle;
    }

    public String getSubtitle(){
        return Subtitle;
    }

    public void setText(String Text){
        this.Text = Text;
    }

    public String getText(){
        return Text;
    }

    public void setPic1(String Pic1){
        this.Pic1 = Pic1;
    }

    public String getPic1(){
        return Pic1;
    }

    public void setPic2(String Pic2){
        this.Pic2 = Pic2;
    }

    public String getPic2(){
        return Pic2;
    }

    public void setPic3(String Pic3){
        this.Pic3 = Pic3;
    }

    public String getPic3(){
        return Pic3;
    }

    public void setPic4(String Pic4){
        this.Pic4 = Pic4;
    }

    public String getPic4(){
        return Pic4;
    }
}
