package com.njupt.zyhy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class Fragment_Me_collection extends Activity implements View.OnClickListener{

    private ImageView back;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collection_back:
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_favorites);
        back = (ImageView) findViewById(R.id.collection_back);
        back.setOnClickListener(this);
    }
}