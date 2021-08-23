package com.njupt.zyhy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;

public class Fragment_Me_trip extends Activity implements View.OnClickListener{

    private ImageView back;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.trip_back:
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
        setContentView(R.layout.fragment_me_stroke);

        back = (ImageView) findViewById(R.id.trip_back);
        back.setOnClickListener(this);
    }
}
