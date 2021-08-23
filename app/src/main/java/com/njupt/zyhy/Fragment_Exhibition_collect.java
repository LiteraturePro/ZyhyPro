package com.njupt.zyhy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.njupt.zyhy.bean.Collect;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Fragment_Exhibition_collect extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;
    private Button button;
    private EditText Name;
    private EditText Description;
    private EditText Source;
    private EditText Ways;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exhibition_collect);

        back = (ImageView) findViewById(R.id.collect_back);
        back.setOnClickListener(this);

        Description = (EditText) findViewById(R.id.c_miaosu);
        Source = (EditText) findViewById(R.id.c_laiyuan);
        Name = (EditText) findViewById(R.id.c_name);
        Ways = (EditText) findViewById(R.id.c_lainxi);

        button = (Button) findViewById(R.id.coo_put);
        button.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_back:
                finish();
                break;
            case R.id.coo_put:
                add_collect();
                break;
            default:
                break;
        }

    }

    private void add_collect() {
        String C_Description = Description.getText().toString().trim();
        String C_Source = Source.getText().toString().trim();
        String C_Name = Name.getText().toString().trim();
        String C_Ways = Ways.getText().toString().trim();

        /**
         * 提交登记的文物信息
         */
        Collect collect = new Collect();
        if (TextUtils.isEmpty(C_Description) || TextUtils.isEmpty(C_Source) || TextUtils.isEmpty(C_Name) || TextUtils.isEmpty(C_Ways) ) {
            showToast("内容不能为空!");
            return;
        }
        else if(!isMobileNO(C_Ways)){
            showToast("请输入正确的联系方式！");
            return;
        }
        else{
            collect.setName(C_Name);
            collect.setDescription(C_Description);
            collect.setSource(C_Source);
            collect.setWays(C_Ways);
            collect.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        showToast("提交成功!");
                        finish();
                    } else {
                        showToast("提交失败！");
                    }
                }
            });
        }
    }
    private boolean isMobileNO(String mobiles) {
        String telRegex = "^((1[3,5,7,8][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



}