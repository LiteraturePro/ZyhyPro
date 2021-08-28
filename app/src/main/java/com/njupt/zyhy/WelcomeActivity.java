package com.njupt.zyhy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.mob.MobSDK;


public class WelcomeActivity extends Activity {
    private SharedPreferences sp;
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

        MobSDK.submitPolicyGrantResult(true,null);

        // 获取用户缓存信息
        sp = this.getSharedPreferences("userInfo", Context.MODE_MULTI_PROCESS);

        // 使用PostDelayed方法，两秒后调用此Runnable对象
        // handler.postDelayed(runnable, 2000);
        // 实际上也就实现了一个2s的一个定时器

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(sp.getString("token","")) || sp.getBoolean("auto_isCheck",false)){

                    /**跳转到登录页面*/
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    /**跳转到主页面*/
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);
    }
}
