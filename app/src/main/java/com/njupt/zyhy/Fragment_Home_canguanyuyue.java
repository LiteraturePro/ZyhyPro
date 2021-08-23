package com.njupt.zyhy;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Fragment_Home_canguanyuyue extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;
    private TextView tv_yuyue,tv_canguan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_canguanyuyue);
        back = (ImageView)findViewById(R.id.canguanback);
        back.setOnClickListener(this);
        tv_yuyue = (TextView)findViewById(R.id.textView_yuyue);
        tv_yuyue.setOnClickListener(this);
        tv_canguan =(TextView)findViewById(R.id.textView_canguan);
        tv_canguan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.canguanback:
                finish();
                break;
            case R.id.textView_yuyue:
                Intent intent = new Intent(this, Fragment_Me_order.class);
                startActivity(intent);
                break;
            case R.id.textView_canguan:
                Intent intent1 = new Intent(this, Fragment_Home_canguanyuyue_add.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
}
