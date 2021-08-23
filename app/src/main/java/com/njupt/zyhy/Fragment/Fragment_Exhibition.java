package com.njupt.zyhy.Fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dogecloud.support.DogeInclude;
import com.dogecloud.support.DogeManager;
import com.dogecloud.support.DogeMediaPlayer;
import com.dogecloud.support.DogePlayer;
import com.njupt.zyhy.Fragment_Home_seach;
import com.njupt.zyhy.Fragment_exhabition_detail;
import com.njupt.zyhy.R;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.HistoryBean;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bmob.restapi.Bmob;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Exhibition#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Exhibition extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;

    private RecyclerView rv;
    private List<HistoryBean> historyList= new ArrayList<>();

    private ArrayList<String> Z_Title,Z_Subtitle,Z_Text,Z_Pic1,Z_Pic2,Z_Pic3,Z_Pic4;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Exhibition() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Exhibition.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Exhibition newInstance(String param1, String param2) {
        Fragment_Exhibition fragment = new Fragment_Exhibition();
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
        View view = inflater.inflate(R.layout.fragment__exhibition, container, false);

        // 点击搜索
        imageView = (ImageView) view.findViewById(R.id.imageView6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_seach.class);
                startActivity(intent);
            }
        });

        // 设置字体
        textView = (TextView)view.findViewById(R.id.zyhy_text);
        AssetManager mgr = getActivity().getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "font/仓耳舒圆体W05.ttf");
        textView.setTypeface(tf);

        // 多吉云视频插件
        DogePlayer playerview = view.findViewById(R.id.player);
        DogeMediaPlayer dgplayer = playerview.getMediaPlayer();
        dgplayer.setSources(1670,"44184a6a5f5efa98")
                .setConfig(DogeInclude.CONFIG_TITLE,"这里是遵义!")
                .setConfig(DogeInclude.UI_CONFIG_FULLSCREENBTN, String.valueOf(1))
                .Init();

        // 初始化数据
        Z_Title  = new ArrayList<String>();
        Z_Subtitle  = new ArrayList<String>();
        Z_Text  = new ArrayList<String>();
        Z_Pic1 = new ArrayList<String>();
        Z_Pic2 = new ArrayList<String>();
        Z_Pic3 = new ArrayList<String>();
        Z_Pic4 = new ArrayList<String>();
        inindate();
        rv= view.findViewById(R.id.main_rv);
        initData();
        LinearLayoutManager manager= new LinearLayoutManager(getActivity());
        rv.setLayoutManager(manager);
        HistoryAdapter adapter= new HistoryAdapter(historyList);
        rv.setAdapter(adapter);

        return view;
    }

    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
        public HistoryAdapter(List<HistoryBean> historyBeanList) {
            historyList= historyBeanList;
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            private final LinearLayout ll;
            private final TextView time;
            private final ImageView pic;
            private final TextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                ll= itemView.findViewById(R.id.main_ll);
                time= itemView.findViewById(R.id.main_time);
                pic= itemView.findViewById(R.id.main_pic);
                title= itemView.findViewById(R.id.main_title);
            }
        }

        @Override
        public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.item_zhanlan, null);
            ViewHolder holder= new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
            //得到可用的图片
            bitmap = GetHttpBitmap.getHttpBitmap(historyList.get(position).getPic());
            holder.pic.setImageBitmap(bitmap);
            holder.title.setText(historyList.get(position).getTitle());
            holder.time.setText(historyList.get(position).getTime());
            if (position== 0){
                holder.ll.setVisibility(View.VISIBLE);
            } else if (historyList.get(position).getTime().equals(historyList.get(position- 1).getTime())){
                holder.ll.setVisibility(View.GONE);
            }else {
                holder.ll.setVisibility(View.VISIBLE);
            }
            //添加点击监听事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Fragment_exhabition_detail.class);
                    intent.putExtra("Z_Title", Z_Title.get(position));
                    intent.putExtra("Z_Subtitle", Z_Subtitle.get(position));
                    intent.putExtra("Z_Text", Z_Text.get(position));
                    intent.putExtra("Z_Pic1", Z_Pic1.get(position));
                    intent.putExtra("Z_Pic2", Z_Pic2.get(position));
                    intent.putExtra("Z_Pic3", Z_Pic3.get(position));
                    intent.putExtra("Z_Pic4", Z_Pic4.get(position));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }
    }

    private void initData() {
        for (int i= 0; i< Z_Title.size(); i++){
            HistoryBean bean= new HistoryBean(Z_Title.get(i), Z_Pic1.get(i), Z_Subtitle.get(i));
            historyList.add(bean);        }
    }

    private void inindate(){
        InitBmob.Initbmob();
        String re;
        String Title,Subtitle,Text,Pic1,Pic2,Pic3,Pic4;
        re = Bmob.findAll("Exhibit");
        JSONObject jsonObject = JSON.parseObject(re);
        //获取当前嵌套下的属性
        String status = jsonObject.getString("results");
        if (status!=null){
            //获取嵌套中的json串,细心观察 content为json数组，里面可放多个json对象
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            System.out.println(jsonArray);

            for(int i =0;i < jsonArray.size(); i++) {
                JSONObject jsonFirst = jsonArray.getJSONObject(i);

                //取出这个json中的值
                Title = jsonFirst.getString("Title");
                if (Title != null) {
                    Z_Title.add(Title);
                }
                //取出这个json中的值
                Subtitle = jsonFirst.getString("Subtitle");
                if (Subtitle != null) {
                    Z_Subtitle.add(Subtitle);
                }
                //取出这个json中的值
                Text = jsonFirst.getString("Text");
                if (Text != null) {
                    Z_Text.add(Text);
                }
                //取出这个json中的值
                Pic1 = jsonFirst.getString("Pic1");
                if (Pic1 != null) {
                    Z_Pic1.add(Pic1);
                }
                //取出这个json中的值
                Pic2 = jsonFirst.getString("Pic2");
                if (Pic2 != null) {
                    Z_Pic2.add(Pic2);
                }
                //取出这个json中的值
                Pic3 = jsonFirst.getString("Pic3");
                if (Pic3 != null) {
                    Z_Pic3.add(Pic3);
                }
                //取出这个json中的值
                Pic4 = jsonFirst.getString("Pic4");
                if (Pic4 != null) {
                    Z_Pic4.add(Pic4);
                }
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            DogeManager.onResume(getActivity());
        } else {
            //
            DogeManager.onPause(getActivity());
        }

    }

}