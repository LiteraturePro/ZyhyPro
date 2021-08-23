package com.njupt.zyhy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.unicloud.UnicloudApi;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText accountLoginName,accountLoginPassword;
    private Button loginBtn;
    private TextView registerAccountBtn,forgetBtn;
    private ProgressBar progressBar;
    private LinearLayout llLogin;
    private AutoCompleteTextView accountLoginNames;

    private CheckBox remember_key;//记住密码勾选框
    private CheckBox automatic_login;//自动登录选框
    private SharedPreferences sp;

    private String userNameValue,passwordValue;

    private Boolean rem_isCheck = false;
    private Boolean auto_isCheck = false;

    @Override
    /*** 创建登录页面
     * @version 1.0
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /**加载xml文件*/
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences("userInfo", Context.MODE_MULTI_PROCESS);

        initView();
        initData();
        initListener();

    }

    /*** 定义初始化函数
     * @use  进行一些初始化的定义，把他们都放在一个initView里面，然后直接调用这个函数就可以完成初始化了
     */
    private void initView() {
        accountLoginName = (EditText) findViewById(R.id.i8_accountLogin_name);
        accountLoginPassword = (EditText) findViewById(R.id.i8_accountLogin_password);
        loginBtn = (Button) findViewById(R.id.i8_accountLogin_toLogin);
        registerAccountBtn = (TextView) findViewById(R.id.register_account_btn);
        forgetBtn = (TextView) findViewById(R.id.forget_btn);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        llLogin = (LinearLayout) findViewById(R.id.ll_login);
        accountLoginNames = (AutoCompleteTextView) findViewById(R.id.i8_accountLogin_name);
        remember_key = (CheckBox) findViewById(R.id.remember_key);
        automatic_login = (CheckBox) findViewById(R.id.automatic_login);
    }

    private void initData() {

        //设置记住密码初始化为true
        remember_key.setChecked(true);

        rem_isCheck = remember_key.isChecked();
        auto_isCheck = automatic_login.isChecked();

        //判断记住密码多选框的状态
        boolean rem_isCheck_data=sp.getBoolean("rem_isCheck", true);
        boolean auto_isCheck_date=sp.getBoolean("auto_isCheck", true);

        if (rem_isCheck_data) {
            if(rem_isCheck) {
                String etName = sp.getString("USER_NAME", "");
                String etPassword = sp.getString("PASSWORD", "");
                Log.e("自动恢复保存的账号密码", etName+etPassword);
                System.out.println("登录成功之前的状态："+auto_isCheck_date);
                accountLoginName.setText(etName);
                accountLoginPassword.setText(etPassword);

                //判断自动登陆多选框状态
                if(auto_isCheck_date) {
                        //跳转界面
                        UserAccountLogin(etName, etPassword);
                    }
            }
        }
    }

    /*** 显示进度条
     * @version 1.0
     */
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        llLogin.setVisibility(View.GONE);
    }

    /*** 隐藏进度条
     * @version 1.0
     */
    private void hiddenProgressBar() {
        progressBar.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
    }

    /*** 定义监听函数
     * @version 1.0
     * @use  进行页面监听，等待用户操作
     */
    private void initListener() {
        loginBtn.setOnClickListener(this);
        registerAccountBtn.setOnClickListener(this);
        forgetBtn.setOnClickListener(this);
        accountLoginNames.setOnClickListener(this);
        initAutoComplete("history",accountLoginNames);

    }

    @Override
    /**  监听按钮动态
     * @version 1.0
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.i8_accountLogin_toLogin:
                /**点击登录，调用登录函数*/
                UserAccountLogin();
                break;
            case R.id.register_account_btn:
                /**点击注册，跳转到注册界面*/
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.forget_btn:
                /**点击忘记密码，跳转重置密码页面*/
                Intent intent_forget = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent_forget);
                break;
            default:
                break;
        }

    }

    private void UserAccountLogin() {

        userNameValue = accountLoginName.getText().toString().trim();
        passwordValue = accountLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userNameValue) || TextUtils.isEmpty(passwordValue) ) {
            showToast("账号或密码不能为空");
            return;
        }
        /**显示进度条*/
        showProgressBar();
        /**登录过程*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject Json = UnicloudApi.LoginJson(userNameValue,passwordValue);
                    if (Json.getString("code").equals("0")){
                        /**登录成功后进入主界面*/
                        saveHistory("history",accountLoginNames);
                        rem_isCheck = remember_key.isChecked();
                        auto_isCheck = automatic_login.isChecked();

                        //记住密码框为选中状态才保存用户信息
                        if (rem_isCheck) {
                            //记住用户名、密码、
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("USER_NAME", userNameValue);
                            editor.putString("PASSWORD", passwordValue);
                            editor.putBoolean("rem_isCheck", rem_isCheck);
                            editor.putBoolean("auto_isCheck", auto_isCheck);
                            editor.putString("id", Json.getString("uid"));
                            editor.putString("token", Json.getString("token"));
                            editor.putString("tokenExpired", Json.getString("tokenExpired"));
                            editor.apply();
                            System.out.println("登录成功的状态："+auto_isCheck);
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        showToast(Json.getString("msg"));
                        /**隐藏进度条*/
                        hiddenProgressBar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10);
    }

    // UserAccountLogin重载，使用自定义类
    private void UserAccountLogin(String accountName,String accountPassword ) {

        if (TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountPassword) ) {
            showToast("账号或密码不能为空");
            return;
        }
        /**显示进度条*/
        showProgressBar();
        /**登录过程*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject Json = UnicloudApi.LoginJson(accountName,accountPassword);
                    if (Json.getString("code").equals("0")){
                        /**登录成功后进入主界面*/
                        saveHistory("history",accountLoginNames);
                        rem_isCheck = remember_key.isChecked();
                        auto_isCheck = automatic_login.isChecked();

                        //记住密码框为选中状态才保存用户信息
                        if (rem_isCheck) {
                            //记住用户名、密码、
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("USER_NAME", accountName);
                            editor.putString("PASSWORD", accountPassword);
                            editor.putBoolean("rem_isCheck", rem_isCheck);
                            editor.putBoolean("auto_isCheck", auto_isCheck);
                            editor.putString("id", Json.getString("uid"));
                            editor.putString("token", Json.getString("token"));
                            editor.putString("tokenExpired", Json.getString("tokenExpired"));
                            editor.apply();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        showToast(Json.getString("msg"));
                        /**隐藏进度条*/
                        hiddenProgressBar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10);
    }
    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使
     * AutoCompleteTextView在一开始获得焦点时自动提示
     * @param field 保存在sharedPreference中的字段名
     * @param auto 要操作的AutoCompleteTextView
     */
    private void initAutoComplete(String field,AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString("history", "");
        String[]  hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);
        //只保留最近的50条的记录
        if(hisArrays.length > 50){
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(adapter);
        auto.setDropDownHeight(350);
        auto.setThreshold(1);
        // 设置auto参数的setOnFocusChangeListener监听器
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     * @param field  保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString(field, "");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

