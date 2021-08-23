package com.njupt.zyhy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.codbking.view.ItemView;
import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.njupt.zyhy.bean.CacheUtil;
import com.njupt.zyhy.bean.ConfirmDialogQuit;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bmob.bson.BSONObject;
import com.njupt.zyhy.bmob.restapi.Bmob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import cn.bmob.v3.BmobUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Fragment_Me_setting extends Activity implements View.OnClickListener {

    private Button exitBtn;
    private ImageView back;
    private ItemView item_cache;
    private ItemView item_version,item_account,item_password;
    private ConfirmDialogQuit confirmDialog;
    private SharedPreferences sp;
    private TextView tvYS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_setting);
        exitBtn = (Button) findViewById(R.id.btn_exit);
        back = (ImageView) findViewById(R.id.ivLeft);
        item_cache = (ItemView) findViewById(R.id.item_cache);
        item_version = (ItemView) findViewById(R.id.item_version);
        item_account = (ItemView) findViewById(R.id.item_account);
        item_password = (ItemView) findViewById(R.id.item_password);
        tvYS = (TextView) findViewById(R.id.tvYS);

        try {
            item_cache.setText(CacheUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        exitBtn.setOnClickListener(this);
        back.setOnClickListener(this);
        item_cache.setOnClickListener(this);
        item_version.setOnClickListener(this);
        item_account.setOnClickListener(this);
        item_password.setOnClickListener(this);
        tvYS.setOnClickListener(this);

        // 初始化弹窗对象
        confirmDialog = new ConfirmDialogQuit(this);
        // 获取用户缓存信息
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        // 初始化版本信息
        item_version.setText(find());

        // 在Android4.0以后，会发现，只要是写在主线程（就是Activity）中的HTTP请求，运行时都会报错，
        // 这是因为Android在4.0以后为了防止应用的ANR（Aplication Not Response）异常，
        // Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生。
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                BmobUser.logOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ivLeft:
                finish();
                break;
            case R.id.item_password:
                final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(this).builder()
                        .setTitle("请输入新的密码")
                        .setEditText("");
                myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String etName = sp.getString("id", "");
                        update(etName,myAlertInputDialog.getResult());
                        BmobUser.logOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        myAlertInputDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAlertInputDialog.dismiss();
                    }
                });
                myAlertInputDialog.show();
                break;
            case R.id.item_cache:
                Dialog();
                break;
            case R.id.item_account:
                ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setLogoImg(R.mipmap.dialog_notice).setMsg("账号即将删除,之后不可使用！");
                confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void ok() {
                        BmobUser.logOut();
                        // 删除账号
                        detele(sp.getString("id", ""));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void cancel() {
                    }
                });
                confirmDialog.show();
                break;
            case R.id.item_version:
                showToast("当前版本: v"+find());
                break;
            case R.id.tvYS:
                String str = initAssets("yszc.txt");
                final View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_xieyi_yinsi_style, null);
                TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
                tv_title.setText("隐私政策");
                TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
                tv_content.setText(str);
                final Dialog dialog = new AlertDialog
                        .Builder(this)
                        .setView(inflate)
                        .show();
                final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = 800;
                params.height = 1200;
                dialog.getWindow().setAttributes(params);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            default:
                break;
        }
    }

    public void Dialog (){
        confirmDialog.setOnDialogClickListener(new ConfirmDialogQuit.OnDialogClickListener() {
            @Override
            public void onOKClick() throws Exception {
                // 获取存储对象
                CacheUtil.clearAllCache(getApplicationContext());
                confirmDialog.dismiss();
                item_cache.setText(CacheUtil.getTotalCacheSize(getApplicationContext()));
            }
            @Override
            public void onCancelClick() {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.setCancelable(true);//点击空白处消失
        confirmDialog.show();
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
    private void detele(String ID){
        InitBmob.Initbmob();
        String re;
        re = Bmob.delete("_User", ID);
        showToast("删除成功");
        Log.d("删除代码",re);

    }
    private String find(){
        InitBmob.Initbmob();
        String re,yongjin_type = "1.0";
        re = Bmob.find("AppVersion", 1);
        Log.d("查询代码",re);
        JSONObject jsonObject = JSON.parseObject(re);
        //获取当前嵌套下的属性
        String status = jsonObject.getString("results");
        if (status!=null){
            System.out.println(status);
            //获取嵌套中的json串,细心观察 content为json数组，里面可放多个json对象
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            //将json数组中取出一个json ，当前只有一个json组，所以下标为0
            JSONObject jsonFirst = jsonArray.getJSONObject(0);

            //取出这个json中的值
            yongjin_type = jsonFirst.getString("version");
            if (yongjin_type!=null){
                System.out.println(yongjin_type);
            }
        }
        return yongjin_type;

    }

    // 读取文件
    public String initAssets(String fileName) {
        String str = null;
        try {
            InputStream inputStream = getAssets().open(fileName);

            str = getString(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return str;
    }
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
