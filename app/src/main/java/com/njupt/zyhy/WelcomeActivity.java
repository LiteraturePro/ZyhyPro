package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;


import com.mob.MobSDK;

import cn.bmob.v3.Bmob;

public class WelcomeActivity extends Activity {
    /**
     * 跳转判断
     */
    @Override
    /*** 创建欢迎页面
     * @version 1.0
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /**初始化数据库的连接*/
        Bmob.initialize(this, "cc15119f58279a130fb52b657be04b72");

        MobSDK.submitPolicyGrantResult(true,null);

        // 初始化安装包信息表
        //BmobUpdateAgent.initAppVersion();

        // 使用PostDelayed方法，两秒后调用此Runnable对象
        // handler.postDelayed(runnable, 2000);
        // 实际上也就实现了一个2s的一个定时器

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /**跳转到登录页面*/
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
