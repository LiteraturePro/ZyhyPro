package com.njupt.zyhy.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.njupt.zyhy.unicloud.UnicloudApi;
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
    private JSONArray Z_DataJSONArray;
    private Handler handler;
    private SharedPreferences sp;

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

        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

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

        rv= view.findViewById(R.id.main_rv);

        //创建handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    Z_DataJSONArray = DataJSONObject.getJSONArray("data");
                    for (int i= 0; i< Z_DataJSONArray.size(); i++){
                        HistoryBean bean= new HistoryBean(Z_DataJSONArray.getJSONObject(i).getString("title"), Z_DataJSONArray.getJSONObject(i).getJSONArray("image").getString(0), Z_DataJSONArray.getJSONObject(i).getString("describe"));
                        historyList.add(bean);
                    }
                    LinearLayoutManager manager= new LinearLayoutManager(getActivity());
                    rv.setLayoutManager(manager);
                    HistoryAdapter adapter= new HistoryAdapter(historyList);
                    rv.setAdapter(adapter);

                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                //FIXME 这里直接更新ui是不行的
                Message C_message = Message.obtain();
                //还有其他更新ui方式,runOnUiThread()等
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
        public void onBindViewHolder(HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                    intent.putExtra("Z_Title", Z_DataJSONArray.getJSONObject(position).getString("title"));
                    intent.putExtra("Z_Subtitle", Z_DataJSONArray.getJSONObject(position).getString("describe"));
                    intent.putExtra("Z_Text", Z_DataJSONArray.getJSONObject(position).getString("text"));
                    intent.putExtra("Z_Pic1", Z_DataJSONArray.getJSONObject(position).getJSONArray("image").getString(0));
                    intent.putExtra("Z_Pic2", Z_DataJSONArray.getJSONObject(position).getJSONArray("image").getString(1));
                    intent.putExtra("Z_Pic3", Z_DataJSONArray.getJSONObject(position).getJSONArray("image").getString(2));
                    intent.putExtra("Z_Pic4", Z_DataJSONArray.getJSONObject(position).getJSONArray("image").getString(3));
                    startActivity(intent);
                }
            });

        }
        @Override
        public int getItemCount() {
            return historyList.size();
        }
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
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