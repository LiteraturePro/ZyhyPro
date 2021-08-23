package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;


public class Fragment_guide_map extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide_map);

        Intent intent=getIntent();
        String bit =intent.getStringExtra("bit");

        if(bit.equals("map")){
            //终点
            Poi end = new Poi("遵义会议会址", new LatLng(27.688164,106.919717), "B0354006NC");
            // 组件参数配置
            AmapNaviParams params = new AmapNaviParams(null,null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            // 退出时销毁容器
            params.setNeedDestroyDriveManagerInstanceWhenNaviExit(true);
            params.setUseInnerVoice(true);
            // 启动组件
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        }else {
            //终点
            Poi end = new Poi("纪念广场", new LatLng(27.687616,106.918619), "B0354006NC");
            // 组件参数配置
            AmapNaviParams params = new AmapNaviParams(null,null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            // 退出时销毁容器
            params.setNeedDestroyDriveManagerInstanceWhenNaviExit(true);
            params.setUseInnerVoice(true);
            // 启动组件
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //退出导航组件
        AmapNaviPage.getInstance().exitRouteActivity();

    }
    @Override
    public void onPause() {
        super.onPause();
        //退出导航组件
        AmapNaviPage.getInstance().exitRouteActivity();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出导航组件
        AmapNaviPage.getInstance().exitRouteActivity();
    }
}
