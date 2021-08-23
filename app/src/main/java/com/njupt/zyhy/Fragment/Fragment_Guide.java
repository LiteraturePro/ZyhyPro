package com.njupt.zyhy.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.njupt.zyhy.ARActivity;
import com.njupt.zyhy.Fragment_guide_webview;
import com.njupt.zyhy.R;
import com.njupt.zyhy.Fragment_guide_map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Guide#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Guide extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient client;
    private ImageView scan_imageView;

    public Fragment_Guide() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Guide.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Guide newInstance(String param1, String param2) {
        Fragment_Guide fragment = new Fragment_Guide();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__guide, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.showIndoorMap(true);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        client = new AMapLocationClient(null);
        client.setLocationListener(locationListener);

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setNeedAddress(true);
        option.setMockEnable(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度位置
        client.setLocationOption(option);
        client.startLocation();
        /** 创建定位器 */
        initAMap(aMap,(float) 27.688412,(float) 106.920726);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 路线导航点击事件
        ImageView road_sign = (ImageView) getActivity().findViewById(R.id.iv_road_sign);
        road_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_guide_map.class);
                intent.putExtra("bit", "map");
                startActivity(intent);
            }
        });

        scan_imageView = (ImageView) getActivity().findViewById(R.id.iv_camera);
        //相机点击事件
        scan_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_guide_webview.class);

                startActivity(intent);
            }
        });
        // AR体验点击事件
        getActivity().findViewById(R.id.iv_ar).setOnClickListener(new View.OnClickListener() {
            private long mLastClickTime = 0;
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(getActivity(), ARActivity.class));
            }
        });
    }

    public AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation.getErrorCode() == 0){
                LatLng latLng = new LatLng(27.688164,106.919717);//坐标对象
                aMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                options.position(latLng);//定位图标要显示的位置
                options.draggable(true);

                aMap.addMarker(options);//给地图添加标记
                CameraUpdate update = CameraUpdateFactory.changeLatLng(latLng);//更新定位范围
                aMap.moveCamera(update);//显示
            }else {

            }
        }
    };

    //移动到指定经纬度
    private void initAMap(AMap  mAMap,float latitude,float longitude) {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude),18,30,0);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        mAMap.moveCamera(cameraUpdate);
    }
}