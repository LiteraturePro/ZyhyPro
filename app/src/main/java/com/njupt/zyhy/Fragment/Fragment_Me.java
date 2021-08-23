package com.njupt.zyhy.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codbking.view.ItemView;
import com.google.android.material.snackbar.Snackbar;
import com.njupt.zyhy.Fragment_Me_collection;
import com.njupt.zyhy.Fragment_Me_feedback;
import com.njupt.zyhy.Fragment_Me_lost;
import com.njupt.zyhy.Fragment_Me_news;
import com.njupt.zyhy.Fragment_Me_order;
import com.njupt.zyhy.Fragment_Me_setting;
import com.njupt.zyhy.Fragment_Me_comment;
import com.njupt.zyhy.Fragment_Me_trip;
import com.njupt.zyhy.LoginActivity;
import com.njupt.zyhy.R;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.RegisterUser;

import cn.bmob.v3.BmobUser;

public class Fragment_Me extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView textView;
    private ImageView imageView;
    private Bitmap bitmap;

    public Fragment_Me() {
        // Required empty public constructor
    }

    /**
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Me.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Me newInstance(String param1, String param2) {
        Fragment_Me fragment = new Fragment_Me();
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

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__me, container, false);
        imageView = (ImageView) view.findViewById(R.id.headimg);
        textView = (TextView) view.findViewById(R.id.name);

        /**
         * 判定用户登录状态
         */
        if (BmobUser.isLogin()) {
            RegisterUser user = BmobUser.getCurrentUser(RegisterUser.class);
            Snackbar.make(container, "欢迎您：" + user.getUsername(), Snackbar.LENGTH_LONG).show();
            textView.setText(user.getUsername());

            String url = user.getAvatar();
            //得到可用的图片
            bitmap = GetHttpBitmap.getHttpBitmap(url);

            imageView.setImageBitmap(bitmap);

        } else {
            Snackbar.make(container, "尚未登录", Snackbar.LENGTH_LONG).show();
        }
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * Item列表点击事件
         */
        // 点击设置
        ImageView btn_send = (ImageView) getActivity().findViewById(R.id.iv_setting);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_setting.class);
                startActivity(intent);
            }
        });

        TextView textView_name = (TextView) getActivity().findViewById(R.id.name);
        textView_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BmobUser.isLogin()) {
                    return;
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        // 点击评论
        ItemView ItemView = (ItemView) getActivity().findViewById(R.id.item_comment);
        ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_comment.class);
                startActivity(intent);
            }
        });
        // 点击消息
        ItemView ItemView2 = (ItemView) getActivity().findViewById(R.id.item_message);
        ItemView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_news.class);
                startActivity(intent);
            }
        });
        // 点击反馈
        ItemView ItemView3 = (ItemView) getActivity().findViewById(R.id.item_feedback);
        ItemView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_feedback.class);
                startActivity(intent);
            }
        });
        // 点击失物招领
        ItemView ItemView4 = (ItemView) getActivity().findViewById(R.id.item_lost);
        ItemView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_lost.class);
                startActivity(intent);
            }
        });
        // 点击行程
        ItemView ItemView5 = (ItemView) getActivity().findViewById(R.id.item_trip);
        ItemView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_trip.class);
                startActivity(intent);
            }
        });

        // 点击收藏
        ItemView ItemView6 = (ItemView) getActivity().findViewById(R.id.item_collection);
        ItemView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_collection.class);
                startActivity(intent);
            }
        });
        // 点击预约
        ItemView ItemView7 = (ItemView) getActivity().findViewById(R.id.item_order);
        ItemView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_order.class);
                startActivity(intent);
            }
        });
    }
}