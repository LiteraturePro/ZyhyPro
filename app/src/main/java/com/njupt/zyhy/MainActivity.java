package com.njupt.zyhy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.dogecloud.support.DogeManager;
import com.mob.MobSDK;
import com.mob.pushsdk.MobPush;
import com.mob.pushsdk.MobPushCustomMessage;
import com.mob.pushsdk.MobPushNotifyMessage;
import com.mob.pushsdk.MobPushReceiver;
import com.next.easynavigation.view.EasyNavigationBar;
import com.njupt.zyhy.Fragment.Fragment_Collection;
import com.njupt.zyhy.Fragment.Fragment_Exhibition;
import com.njupt.zyhy.Fragment.Fragment_Guide;
import com.njupt.zyhy.Fragment.Fragment_Home;
import com.njupt.zyhy.Fragment.Fragment_Me;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EasyNavigationBar navigationBar;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Handler handler;

    private String[] tabText = {"首页","展览","导览","藏品","我的"};
    private int[] normalIcon = {R.drawable.zyhyhome,R.drawable.zyhyexhibition,R.drawable.zyhyguide,R.drawable.zyhycollection,R.drawable.zyhyme};
    private int[] selectIcon = {R.drawable.zyhyhome_1,R.drawable.zyhyexhibition_1,R.drawable.zyhyguide_1,R.drawable.zyhycollection_1,R.drawable.zyhyme_1};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化多吉云
        DogeManager.DogeInit(this);

        /**初始化数据库的连接*/
        Bmob.initialize(this, "cc15119f58279a130fb52b657be04b72");

        fragmentList.add(new Fragment_Home());
        fragmentList.add(new Fragment_Exhibition());
        fragmentList.add(new Fragment_Guide());
        fragmentList.add(new Fragment_Collection());
        fragmentList.add(new Fragment_Me());

        // 导航栏设置
        navigationBar = (EasyNavigationBar) findViewById(R.id.easy_bars);
        navigationBar.defaultSetting()  //恢复默认配置、可用于重绘导航栏
                .titleItems(tabText)      //  Tab文字集合  只传文字则只显示文字
                .normalIconItems(normalIcon)   //  Tab未选中图标集合
                .selectIconItems(selectIcon)   //  Tab选中图标集合
                .fragmentList(fragmentList)       //  fragment集合
                .fragmentManager(getSupportFragmentManager())
                .iconSize(20)     //Tab图标大小
                .tabTextSize(10)   //Tab文字大小
                .tabTextTop(2)     //Tab文字距Tab图标的距离
                .normalTextColor(Color.parseColor("#e0e0e0"))   //Tab未选中时字体颜色
                .selectTextColor(Color.parseColor("#78201f"))   //Tab选中时字体颜色
                .scaleType(ImageView.ScaleType.CENTER_INSIDE)  //同 ImageView的ScaleType
                .navigationBackground(Color.parseColor("#F5F5F5"))   //导航栏背景色
                .setOnTabClickListener(new EasyNavigationBar.OnTabClickListener() {
                    @Override
                    public boolean onTabSelectEvent(View view, int position) {
                        return false;
                    }

                    @Override
                    public boolean onTabReSelectEvent(View view, int position) {
                        return false;
                    }
                })
                .smoothScroll(false)  //点击Tab  Viewpager切换是否有动画
                .canScroll(false)    //Viewpager能否左右滑动
                .mode(EasyNavigationBar.NavigationMode.MODE_NORMAL)   //默认MODE_NORMAL 普通模式  //MODE_ADD 带加号模式
                .centerLayoutHeight(100)   //包含加号的布局高度 背景透明  所以加号看起来突出一块
                .navigationHeight(50)  //导航栏高度
                .lineHeight(10)         //分割线高度  默认1px
                .lineColor(Color.parseColor("#F5F5F5"))
                .centerLayoutRule(EasyNavigationBar.RULE_BOTTOM) //RULE_CENTER 加号居中addLayoutHeight调节位置 EasyNavigationBar.RULE_BOTTOM 加号在导航栏靠下
                .build();


        //防止多进程注册多次  可以在MainActivity或者其他页面注册MobPushReceiver
        String processName = getProcessName(this);
        if (getPackageName().equals(processName)) {
            MobPush.addPushReceiver(new MobPushReceiver() {
                @Override
                public void onCustomMessageReceive(Context context, MobPushCustomMessage message) {
                    //接收自定义消息(透传)
                    System.out.println("onCustomMessageReceive:" + message.toString());
                }

                @Override
                public void onNotifyMessageReceive(Context context, MobPushNotifyMessage message) {
                    //接收通知消
                    System.out.println("MobPush onNotifyMessageReceive:" + message.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "Message Receive:" + message.toString();
                    handler.sendMessage(msg);

                }

                @Override
                public void onNotifyMessageOpenedReceive(Context context, MobPushNotifyMessage message) {
                    //接收通知消息被点击事件
                    System.out.println("MobPush onNotifyMessageOpenedReceive:" + message.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = "Click Message:" + message.toString();
                    handler.sendMessage(msg);
                }

                @Override
                public void onTagsCallback(Context context, String[] tags, int operation, int errorCode) {
                    //接收tags的增改删查操作
                    System.out.println("onTagsCallback:" + operation + "  " + errorCode);
                }

                @Override
                public void onAliasCallback(Context context, String alias, int operation, int errorCode) {
                    //接收alias的增改删查操作
                    System.out.println("onAliasCallback:" + alias + "  " + operation + "  " + errorCode);
                }
            });

            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == 1) {
                        Toast.makeText(MobSDK.getContext(), "回调信息\n" + (String) msg.obj, Toast.LENGTH_LONG).show();
                        System.out.println("Callback Data:" + msg.obj);
                    }
                    return false;
                }
            });
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }
    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }
}