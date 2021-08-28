package com.njupt.zyhy.bean;

public class HistoryBean {
    private String name;
    private String pic;
    private String title;

    public HistoryBean(String name, String pic, String title) {
        this.name = name;
        this.pic = pic;
        this.title = title;
    }

    public String getTime() {
        return name;
    }

    public void setTime(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
