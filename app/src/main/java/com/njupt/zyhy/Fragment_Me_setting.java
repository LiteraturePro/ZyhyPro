package com.njupt.zyhy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import com.njupt.zyhy.unicloud.UnicloudApi;

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
        try {
            item_version.setText(UnicloudApi.GetAppVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                Logout();
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
                        try{
                            UnicloudApi.resetPwd("1",sp.getString("id", ""),myAlertInputDialog.getResult());
                            Logout();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            myAlertInputDialog.dismiss();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
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
                        try {
                            UnicloudApi.DeleteData(sp.getString("token", ""),"uni-id-users",sp.getString("id", ""));
                            Logout();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void cancel() {
                    }
                });
                confirmDialog.show();
                break;
            case R.id.item_version:
                try {
                    showToast("当前版本: v"+ UnicloudApi.GetAppVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    private void Logout(){
        try {
            UnicloudApi.Logout(sp.getString("token", ""));
            SharedPreferences.Editor editor = sp.edit();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
