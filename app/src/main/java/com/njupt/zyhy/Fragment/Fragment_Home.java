package com.njupt.zyhy.Fragment;

import android.annotation.SuppressLint;
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
    private CircleImageView CircleImageView1,CircleImageView2,CircleImageView3,CircleImageView4;
    private CircleImageView CircleImageView5,CircleImageView6,CircleImageView7,CircleImageView8;

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

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__home, container, false);

        //??????handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    Z_DataJSONArray = DataJSONObject.getJSONArray("data");
                    try {
                        JSONObject DataJSONObject2 = GetData("uni-data-collection");
                        C_DataJSONArray = DataJSONObject2.getJSONArray("data");
                        /**
                        * ????????????
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
                        //???????????????
                        banner_1.initBanner(urlList_book, false)//??????3D????????????
                                .addPageMargin(10, 80)//??????1page???????????????,??????2??????item?????????????????????
                                //.addPointMargin(6)//???????????????
                                .addStartTimer(3)//????????????5?????????
                                .addPointBottom(7)
                                .addRoundCorners(10)//??????
                                .finishConfig()//???????????????
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
                        banner_2.initBanner(urlList_wenwu, true)//??????3D????????????
                                .addPageMargin(0, 100)//??????1page???????????????,??????2??????item?????????????????????
                                //.addPointMargin(6)//???????????????
                                .addStartTimer(3)//????????????5?????????
                                .addPointBottom(1)
                                .addRoundCorners(10)//??????
                                .finishConfig()//???????????????
                                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                                    @Override
                                    public void onBannerClick(int position) {
                                        JSONObject OneDate = C_DataJSONArray.getJSONObject(position);
                                        Intent intent = new Intent(getActivity(), Fragment_collection_detail.class);
                                        Bundle bundle = new Bundle() ;
                                        String Datas = "?????????"+OneDate.getString("texture") +"\n"+"?????????:"+OneDate.getString("registration_number")+"\n"+"???????????????"+OneDate.getString("registration_number")+"\n"+"?????????"+OneDate.getString("years")+"\n"+"?????????"+OneDate.getString("size")+"\n";

                                        bundle.putString("C_Name",OneDate.getString("name"));
                                        bundle.putString("C_Introduce",Datas+"\n"+"\u3000"+OneDate.getString("introduction"));
                                        bundle.putString("C_Voice",OneDate.getJSONArray("video").getString(0));
                                        bundle.putString("C_Pic1",OneDate.getJSONArray("image").getString(0));
                                        bundle.putString("C_Pic2",OneDate.getJSONArray("image").getString(1));
                                        bundle.putString("C_Pic3",OneDate.getJSONArray("image").getString(2));
                                        intent.putExtras(bundle) ;
                                        startActivity(intent);
                                    }
                                });
                        banner_3.initBanner(urlList_home, false)//??????3D????????????
                                .addPageMargin(0, 0)//?????????
                                .addPointMargin(6)//???????????????
                                .addStartTimer(5)//????????????5?????????
                                .addPointBottom(7)
                                .finishConfig()//???????????????
                                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                                    @Override
                                    public void onBannerClick(int position) {
                                        //??????item
                                        Log.i("test","--------------00x3");
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //FIXME ??????????????????ui????????????
                Message C_message = Message.obtain();
                //??????????????????ui??????,runOnUiThread()???
                try {
                    C_message.obj = GetData("uni-data-exhibit");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();
        return view;

    }
    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * ??????????????????
         */
        banner_1 = (BannerViewPager) getActivity().findViewById(R.id.banner_1);
        banner_2 = (BannerViewPager) getActivity().findViewById(R.id.banner_2);
        banner_3 = (BannerViewPager) getActivity().findViewById(R.id.banner_3);
        /**
         * ????????????????????????
         */
        imageView = (ImageView) getActivity().findViewById(R.id.ivMsg);
        scan_imageView = (ImageView) getActivity().findViewById(R.id.ivqr);
        Seach_imageView = (ImageView) getActivity().findViewById(R.id.ivSearch);

        //??????????????????
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_news.class);
                startActivity(intent);
            }
        });

        //?????????????????????
        scan_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(Fragment_Home.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.setPrompt("??????????????????");//?????????????????????
                intentIntegrator.setCameraId(0);//???????????????????????????
                intentIntegrator.setBeepEnabled(true); //???????????????????????????????????????
                intentIntegrator.setBarcodeImageEnabled(true);//???????????????????????????????????????
                intentIntegrator.initiateScan();
            }
        });
        // ????????????????????????
        Seach_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Fragment_Home_seach.class);
                startActivity(intent);
            }
        });

        /**
         * ????????????????????????
         */
        // ????????????
        CircleImageView1 = (CircleImageView) getActivity().findViewById(R.id.circleImageView11);
        CircleImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_quanjing.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView2 = (CircleImageView) getActivity().findViewById(R.id.circleImageView12);
        CircleImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_canguanyuyue.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView3 = (CircleImageView) getActivity().findViewById(R.id.circleImageView13);
        CircleImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_guide_map.class);
                intent.putExtra("bit", "map");
                startActivity(intent);
            }
        });
         //????????????
        CircleImageView4 = (CircleImageView) getActivity().findViewById(R.id.circleImageView3);
        CircleImageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_Dangjiang.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView5 = (CircleImageView) getActivity().findViewById(R.id.circleImageView9);
        CircleImageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Fragment_Home_inform.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView6 = (CircleImageView) getActivity().findViewById(R.id.circleImageView4);
        CircleImageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_zhoubianwenchuang.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView7 = (CircleImageView) getActivity().findViewById(R.id.circleImageView6);
        CircleImageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_bianmin.class);
                startActivity(intent);
            }
        });
        // ????????????
        CircleImageView8 = (CircleImageView) getActivity().findViewById(R.id.circleImageView7);
        CircleImageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_education.class);
                startActivity(intent);
            }
        });
        // ??????????????????
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.home_jiaoyu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_education.class);
                startActivity(intent);
            }
        });
        // ????????????
        ImageView imageView2 = (ImageView) getActivity().findViewById(R.id.home_wenchuang);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_zhoubianwenchuang.class);
                startActivity(intent);
            }
        });

    }

    // ??????????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(scanResult!=null){
            String result = scanResult.getContents();
            if(result.equals("????????????????????????")){
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