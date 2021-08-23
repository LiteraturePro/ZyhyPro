package com.njupt.zyhy.bean;

public class HistoryBean {
    private String time;
    private String pic;
    private String title;

    public HistoryBean(String time, String pic, String title) {
        this.time = time;
        this.pic = pic;
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
