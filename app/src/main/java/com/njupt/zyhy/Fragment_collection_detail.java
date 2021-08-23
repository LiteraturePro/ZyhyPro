package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.lzj.gallery.library.views.BannerViewPager;
import java.util.ArrayList;
import java.util.List;

public class Fragment_collection_detail extends Activity implements View.OnClickListener{
    private BannerViewPager banner;
    private List<String> urlList_home;
    private TextView textView_name,textView_Introduce;
    private ImageView back;
    private String C_Voice;
    private int bing =0;
    private ImageButton buttonplaystop;
    private MediaPlayer mediaplayer;
    private SeekBar seekBar;
    private final Handler handler=new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_collection_detail);

        banner = (BannerViewPager) findViewById(R.id.banner);

        /**
         * 获取数据
         */
        Intent intent=getIntent();
        String C_Name=intent.getStringExtra("C_Name");
        C_Voice=intent.getStringExtra("C_Voice");
        String C_Introduce=intent.getStringExtra("C_Introduce");
        String C_Pic1=intent.getStringExtra("C_Pic1");
        String C_Pic2=intent.getStringExtra("C_Pic2");
        String C_Pic3=intent.getStringExtra("C_Pic3");

        urlList_home = new ArrayList<>();
        urlList_home.add(C_Pic1);
        urlList_home.add(C_Pic2);
        urlList_home.add(C_Pic3);

        banner.initBanner(urlList_home, false)//关闭3D画廊效果
                .addPageMargin(0, 0)//无间距
                .addPointMargin(6)//添加指示器
                .addStartTimer(2)//自动轮播5秒间隔
                .addPointBottom(7)
                .finishConfig()//这句必须加
                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                    @Override
                    public void onBannerClick(int position) {
                        //点击item
                        Log.i("test","--------------00x3");
                    }
                });
        textView_name = (TextView) findViewById(R.id.name);
        textView_Introduce = (TextView) findViewById(R.id.Introduce);
        back = (ImageView) findViewById(R.id.wenwu_back);

        textView_name.setText(C_Name);
        textView_Introduce.setText(C_Introduce);
        back.setOnClickListener(this);
        initViews();

    }
    private  void initViews(){
        buttonplaystop =(ImageButton)findViewById(R.id.ButtonPlayStop);  // 控制 开始 和暂停

        /*****************控制按钮的点击事件****************************/
        buttonplaystop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick();
            }
        });
        /***设置音频的位置***/
        mediaplayer = new MediaPlayer();
        mediaplayer.setAudioAttributes(new AudioAttributes.Builder()
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        try {
            //调用setDataSource方法，传入音频文件的http位置，此时处于Initialized状态
            mediaplayer.setDataSource(C_Voice);
            mediaplayer.prepare();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        seekBar=(SeekBar) findViewById(R.id.SeekBar01);
        seekBar.setMax(mediaplayer.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);  /*****调整进度条时调用的方法*****/
                return false;
            }
        });
    }

    public  void startPlayProgressUpdater(){
        seekBar.setProgress(mediaplayer.getCurrentPosition());

        if(mediaplayer.isPlaying()) {
            Runnable notification = new Runnable() {
                @Override
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else {
            mediaplayer.pause();
            buttonplaystop.setImageResource(R.drawable.play);
            seekBar.setProgress(0);
        }
    }

    private void seekChange(View v) {
        if(mediaplayer.isPlaying()){
            SeekBar sb=(SeekBar)v;
            mediaplayer.seekTo(sb.getProgress());
        }
    }

    /*****************控制按钮的点击事件****************************/
    private void buttonClick() {
        /*********开始状态******/
        if(bing == 0) {
            buttonplaystop.setImageResource(R.drawable.stop);
            try {
                mediaplayer.start();
                bing = 1;
                startPlayProgressUpdater();
            }catch (IllegalStateException e) {
                mediaplayer.pause();
            }
        }else if(bing == 1)
        {  /*********暂停状态******/
            buttonplaystop.setImageResource(R.drawable.play);
            bing = 0;
            mediaplayer.pause();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wenwu_back:
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaplayer.pause();
    }
}
