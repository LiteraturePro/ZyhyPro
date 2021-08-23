package com.njupt.zyhy.bean;

import cn.bmob.v3.BmobUser;

/**
 * @Title:   RegisterUser
 * @Description:  定义RegisterUser类继承自BmobObject类，用户注册类
 */
public class RegisterUser extends BmobUser {

    /**
     * 注册号码
     */
    private String registerName;

    /**
     * 密码
     */
    private String registerPassword;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 国家
     */

    private String country;


    /**
     * 年龄
     */
    private Integer age;


    /**
     * 性别
     */
    private Integer gender;


    /**
     * 头像
     */
    private String avatar;

    public String getRegisterName() {

        return registerName;
    }

    public void setRegisterName(String registerName) {

        this.registerName = registerName;
    }

    public String getRegisterPassword() {

        return registerPassword;
    }

    public void setRegisterPassword(String registerPassword) {

        this.registerPassword = registerPassword;
    }
    public String getNickname() {
        return nickname;
    }

    public RegisterUser setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public RegisterUser setCountry(String country) {
        this.country = country;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public RegisterUser setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public RegisterUser setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
