package com.njupt.zyhy.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lzj.gallery.library.views.BannerViewPager;
import com.njupt.zyhy.CircleImageView;
import com.njupt.zyhy.Fragment_Home_Dangjiang;
import com.njupt.zyhy.Fragment_Home_bianmin;
import com.njupt.zyhy.Fragment_Home_canguanyuyue;
import com.njupt.zyhy.Fragment_Home_education;
import com.njupt.zyhy.Fragment_Home_inform;
import com.njupt.zyhy.Fragment_Home_quanjing;
import com.njupt.zyhy.Fragment_Home_seach;
import com.njupt.zyhy.Fragment_Home_zhoubianwenchuang;
import com.njupt.zyhy.Fragment_Me_news;
import com.njupt.zyhy.Fragment_collection_detail;
import com.njupt.zyhy.Fragment_exhabition_detail;
import com.njupt.zyhy.Fragment_guide_map;
import com.njupt.zyhy.Fragment_guide_webview;
import com.njupt.zyhy.R;
import com.njupt.zyhy.ScanActivity;
import com.njupt.zyhy.unicloud.UnicloudApi;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private JSONArray C_DataJSONArray;
    private JSONArray Z_DataJSONArray;
    private Handler handler;
    private SharedPreferences sp;

    private BannerViewPager banner_1,banner_2,banner_3;
    private List<String> urlList_wenwu,urlList_book,urlList_home;
    private ImageView imageView,scan_imageView,Seach_imageView;
    private CircleImageView CircleImageView1,CircleImageView2,CircleImageView3,CircleImageView4,CircleImageView5,CircleImageView6,CircleImageView7,CircleImageView8;

    private String mParam1;
    private String mParam2;

    public Fragment_Home() {
        // Required empty public constructor
    }

    public static Fragment_Home newInstance(String param1, String param2) {
        Fragment_Home fragment = new Fragment_Home();
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

        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home, container, false);

        return view;

    }
    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            JSONObject DataJSONObject = GetData("uni-data-collection");
            C_DataJSONArray = DataJSONObject.getJSONArray("data");
            JSONObject DataJSONObject2 = GetData("uni-data-exhibit");
            Z_DataJSONArray = DataJSONObject2.getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 轮播图初始化
         */
        banner_1 = (BannerViewPager) getActivity().findViewById(R.id.banner_1);
        banner_2 = (BannerViewPager) getActivity().findViewById(R.id.banner_2);
        banner_3 = (BannerViewPager) getActivity().findViewById(R.id.banner_3);
        /**
         * 标题栏按钮初始化
         */
        imageView = (ImageView) getActivity().findViewById(R.id.ivMsg);
        scan_imageView = (ImageView) getActivity().findViewById(R.id.ivqr);
        Seach_imageView = (ImageView) getActivity().findViewById(R.id.ivSearch);

        /**
         * 获取数据
         */
        urlList_home = new ArrayList<>();
        urlList_home.add("http://bmob.wxiou.cn/2021/06/02/bac9eb2c401894e580853d35f2c6d382.jpg");
        urlList_home.add("http://bmob.wxiou.cn/2021/06/02/dc5963db402a50b780c7f327d5831154.jpg");
        urlList_home.add("http://bmob.wxiou.cn/2021/06/02/4830727f40137b98807e4eaa82e5c13f.jpg");

        urlList_book = new ArrayList<>();
        for(int i = 0; i < Z_DataJSONArray.size(); i++){
            urlList_book.add(Z_DataJSONArray.getJSONObject(i).getJSONArray("image").getString(0));
        }
        urlList_wenwu = new ArrayList<>();
        for(int i = 0; i < C_DataJSONArray.size(); i++){
            urlList_wenwu.add(C_DataJSONArray.getJSONObject(i).getJSONArray("image").getString(1));
        }
        //轮播图加载
        banner_1.initBanner(urlList_book, false)//关闭3D画廊效果
                .addPageMargin(10, 80)//参数1page之间的间距,参数2中间item距离边界的间距
                //.addPointMargin(6)//添加指示器
                .addStartTimer(3)//自动轮播5秒间隔
                .addPointBottom(7)
                .addRoundCorners(10)//圆角
                .finishConfig()//这句必须加
                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                    @Override
                    public void onBannerClick(int position) {
                        JSONObject OneDate = Z_DataJSONArray.getJSONObject(position);
                        Intent intent = new Intent(getActivity(), Fragment_exhabition_detail.class);
                        intent.putExtra("Z_Title", OneDate.getString("title"));
                        intent.putExtra("Z_Subtitle", OneDate.getString("describe"));
                        intent.putExtra("Z_Text", OneDate.getString("text"));
                        intent.putExtra("Z_Pic1", OneDate.getJSONArray("image").getString(0));
                        intent.putExtra("Z_Pic2", OneDate.getJSONArray("image").getString(1));
                        intent.putExtra("Z_Pic3", OneDate.getJSONArray("image").getString(2));
                        intent.putExtra("Z_Pic4", OneDate.getJSONArray("image").getString(3));
                        startActivity(intent);
                    }
                });
        banner_2.initBanner(urlList_wenwu, true)//关闭3D画廊效果
                .addPageMargin(0, 100)//参数1page之间的间距,参数2中间item距离边界的间距
                //.addPointMargin(6)//添加指示器
                .addStartTimer(3)//自动轮播5秒间隔
                .addPointBottom(1)
                .addRoundCorners(10)//圆角
                .finishConfig()//这句必须加
                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                    @Override
                    public void onBannerClick(int position) {
                        JSONObject OneDate = C_DataJSONArray.getJSONObject(position);
                        Intent intent = new Intent(getActivity(), Fragment_collection_detail.class);
                        intent.putExtra("C_Name",OneDate.getString("name"));
                        intent.putExtra("C_Introduce",OneDate.getString("introduction"));
                        intent.putExtra("C_Voice",OneDate.getJSONArray("video").getString(0));
                        intent.putExtra("C_Pic1",OneDate.getJSONArray("image").getString(0));
                        intent.putExtra("C_Pic2",OneDate.getJSONArray("image").getString(1));
                        intent.putExtra("C_Pic3",OneDate.getJSONArray("image").getString(2));
                        startActivity(intent);
                    }
                });
        banner_3.initBanner(urlList_home, false)//关闭3D画廊效果
                .addPageMargin(0, 0)//无间距
                .addPointMargin(6)//添加指示器
                .addStartTimer(5)//自动轮播5秒间隔
                .addPointBottom(7)
                .finishConfig()//这句必须加
                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                    @Override
                    public void onBannerClick(int position) {
                        //点击item
                        Log.i("test","--------------00x3");
                    }
                });

        //消息点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_news.class);
                startActivity(intent);
            }
        });

        //二维码点击事件
        scan_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentIntegrator intentIntegrator = new IntentIntegrator();
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(Fragment_Home.this);

                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.setPrompt("请扫描二维码");//底部的提示文字
                intentIntegrator.setCameraId(0);//前置或者后置摄像头
                intentIntegrator.setBeepEnabled(true); //扫描成功的提示音，默认开启
                intentIntegrator.setBarcodeImageEnabled(true);//是否保留扫码成功时候的截图
                intentIntegrator.initiateScan();
            }
        });
        // 搜索按钮点击事件
        Seach_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_seach.class);
                intent.putExtra("Collection", mParam1);
                intent.putExtra("Exhibit", mParam2);
                startActivity(intent);
            }
        });


        /**
         * 按钮矩阵点击事件
         */
        // 全景介绍
        CircleImageView1 = (CircleImageView) getActivity().findViewById(R.id.circleImageView11);
        CircleImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_quanjing.class);
                startActivity(intent);
            }
        });
        // 参观预约
        CircleImageView2 = (CircleImageView) getActivity().findViewById(R.id.circleImageView12);
        CircleImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_canguanyuyue.class);
                startActivity(intent);
            }
        });
        // 讲解导览
        CircleImageView3 = (CircleImageView) getActivity().findViewById(R.id.circleImageView13);
        CircleImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_guide_map.class);
                intent.putExtra("bit", "map");
                startActivity(intent);
            }
        });
         //建档活动
        CircleImageView4 = (CircleImageView) getActivity().findViewById(R.id.circleImageView3);
        CircleImageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_Dangjiang.class);
                startActivity(intent);
            }
        });
        // 公告咨询
        CircleImageView5 = (CircleImageView) getActivity().findViewById(R.id.circleImageView9);
        CircleImageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Fragment_Home_inform.class);
                startActivity(intent);
            }
        });
        // 周边文创
        CircleImageView6 = (CircleImageView) getActivity().findViewById(R.id.circleImageView4);
        CircleImageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_zhoubianwenchuang.class);
                startActivity(intent);
            }
        });
        // 便民服务
        CircleImageView7 = (CircleImageView) getActivity().findViewById(R.id.circleImageView6);
        CircleImageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_bianmin.class);
                startActivity(intent);
            }
        });
        // 教育培训
        CircleImageView8 = (CircleImageView) getActivity().findViewById(R.id.circleImageView7);
        CircleImageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_education.class);
                startActivity(intent);
            }
        });
        // 红色教育培训
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.home_jiaoyu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_education.class);
                startActivity(intent);
            }
        });
        // 精品文创
        ImageView imageView2 = (ImageView) getActivity().findViewById(R.id.home_wenchuang);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_zhoubianwenchuang.class);
                startActivity(intent);
            }
        });

    }

    // 扫码回调函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(scanResult!=null){
            String result = scanResult.getContents();
            if(result.equals("欢迎参观遵义会议")){
                Intent intent = new Intent(getActivity(), Fragment_guide_webview.class);
                startActivity(intent);
            }else {
                System.out.println("666666666666666666666666");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }

}