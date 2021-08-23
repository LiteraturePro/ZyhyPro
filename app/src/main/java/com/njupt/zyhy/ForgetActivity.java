package com.njupt.zyhy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mob.MobSDK;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bmob.bson.BSONObject;
import com.njupt.zyhy.bmob.restapi.Bmob;
import java.util.HashMap;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetActivity extends Activity implements View.OnClickListener{


    private EditText accountRegisterName,et_checkCode,accountRegisterPassword;
    private TextView tv_getCheckCode;
    private String CheckCode;
    private String phoneNumber;
    private ProgressDialog dialog;
    private Button registerBtn;
    private TextView registerBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.activity_foeget);
        initView();
        initData();
        initListener();

        // 在Android4.0以后，会发现，只要是写在主线程（就是Activity）中的HTTP请求，运行时都会报错，
        // 这是因为Android在4.0以后为了防止应用的ANR（Aplication Not Response）异常，
        // Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生。
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /*** 定义初始化函数
     * @version 1.0
     * @use  进行一些初始化的定义，把他们都放在一个initView里面，然后直接调用这个函数就可以完成初始化了
     */
    private void initView() {
        accountRegisterName = (EditText) findViewById(R.id.i8_accountRegister_name);
        et_checkCode = (EditText) findViewById(R.id.et_checkCode);
        accountRegisterPassword = (EditText) findViewById(R.id.i8_accountRegister_password);
        tv_getCheckCode = (TextView) findViewById(R.id.tv_getCheckCode);
        registerBtn = (Button) findViewById(R.id.i8_accountRegistern_toRegister);
        registerBackBtn = (TextView) findViewById(R.id.register_back_btn);

    }

    private void initData() { }

    /*** 定义监听函数
     * @version 1.0
     * @use  进行页面监听，等待用户操作
     */
    private void initListener() {

        registerBtn.setOnClickListener(this);
        registerBackBtn.setOnClickListener(this);
        tv_getCheckCode.setOnClickListener(this);

        MobSDK.init(this,"332c8fe3bd6f0","914a7a6e2fe11df8b96ddcdc20846b51");

        //注册短信回调
        SMSSDK.registerEventHandler(ev);
    }

    @Override
    /**  监听按钮动态
     * @version 1.0
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_getCheckCode:
                getCheckCode();
                break;
            case R.id.i8_account_toForget:
                /**点击忘记，调用重置函数*/
                sendCheckCode();
                Intent intent1 = new Intent(ForgetActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.register_back_btn:
                /**点击返回登录页面*/
                Intent intent2 = new Intent(ForgetActivity.this, LoginActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
    /**
     * 短信验证的回调监听
     * @version 1.0
     */
    private EventHandler ev = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                //提交验证码成功,如果验证成功会在data里返回数据。data数据类型为HashMap<number,code>
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Log.e("TAG", "提交验证码成功" + data.toString());
                    HashMap<String, Object> mData = (HashMap<String, Object>) data;
                    String country = (String) mData.get("country");//返回的国家编号
                    String phone = (String) mData.get("phone");//返回用户注册的手机号

                    Log.e("TAG", country + "====" + phone);

                    if (phone.equals(phoneNumber)) {
                        runOnUiThread(new Runnable() {//更改ui的操作要放在主线程，实际可以发送hander
                            @Override
                            public void run() {
                                showDailog("恭喜你！通过验证");
                                dialog.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDailog("验证失败");
                                dialog.dismiss();
                            }
                        });
                    }

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                    Log.e("TAG", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表

                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };

    private void showDailog(String text) {
        new AlertDialog.Builder(this)
                .setTitle(text)
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * 获取验证码
     *@version 1.0
     */
    public void getCheckCode() {
        phoneNumber = accountRegisterName.getText().toString().trim();
        //发送短信，传入国家号和电话号码
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("号码不能为空！");
        } else {
            SMSSDK.getVerificationCode("+86", phoneNumber);
            showToast("验证码发送成功!");
        }
    }

    /**
     * 向服务器提交验证码，在监听回调中监听是否验证
     *@version 1.0
     */
    private void sendCheckCode() {
        CheckCode = et_checkCode.getText().toString();
        if (!TextUtils.isEmpty(CheckCode)) {
            dialog = ProgressDialog.show(this, null, "正在验证...", false, true);
            //提交短信验证码
            SMSSDK.submitVerificationCode("+86", phoneNumber, CheckCode);//国家号，手机号码，验证码
            UpdateAccount();
        } else {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**  bmob更新密码
     * @version 1.0
     */
    private void UpdateAccount() {

        final String registerName = accountRegisterName.getText().toString().trim();
        final String registerPassword = accountRegisterPassword.getText().toString().trim();

        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPassword)) {
            showToast("注册账号或密码为空");
            return;
        }

        /**BmobUser类为Bmob后端云提供类*/
        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(registerName);
        bmobUser.setPassword(registerPassword);
        BmobQuery<BmobUser> userQuery = new BmobQuery<BmobUser>();//增加查询条件
        userQuery.addWhereEqualTo("username",registerName);
        userQuery.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                if(e == null) {
                    for (BmobUser user : list) {
                        //获得数据的objectId信息
                        update(user.getObjectId(),registerPassword);
                        Log.d("id",user.getObjectId());
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    // 直接调用API更新数据库
    private void update(String ID,String Passwd){
        InitBmob.Initbmob();
        String re;
        BSONObject bson = new BSONObject();
        bson.put("password", Passwd);
        re = Bmob.update("_User", ID, bson.toString());
        showToast("更新成功");
        Log.d("更新代码",re);

    }


    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterEventHandler(ev);
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
