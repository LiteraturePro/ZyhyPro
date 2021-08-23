package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Fragment_Home_bianmin extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView lost,opinion,position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_bianmin);
        back = (ImageView)findViewById(R.id.bianminback);
        lost = (TextView)findViewById(R.id.bm_lost);
        opinion = (TextView)findViewById(R.id.bm_opinion);
        position = (TextView)findViewById(R.id.bm_position);

        back.setOnClickListener(this);
        lost.setOnClickListener(this);
        opinion.setOnClickListener(this);
        position.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bianminback:
                finish();
                break;
            case R.id.bm_lost:
                Intent intent1 = new Intent(this, Fragment_Me_lost.class);
                startActivity(intent1);
                break;
            case R.id.bm_opinion:
                Intent intent2 = new Intent(this, Fragment_Me_feedback.class);
                startActivity(intent2);
                break;
            case R.id.bm_position:
                Intent intent3 = new Intent(this, Fragment_guide_map.class);
                intent3.putExtra("bit", "wen");
                startActivity(intent3);
                break;
            default:
                break;
        }
    }
}
