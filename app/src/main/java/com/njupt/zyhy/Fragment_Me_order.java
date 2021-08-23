package com.njupt.zyhy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.SideslipListViews;
import com.njupt.zyhy.bmob.restapi.Bmob;

import java.util.ArrayList;

public class Fragment_Me_order extends Activity implements View.OnClickListener{
    private ImageView back;
    private SideslipListViews mSideslipListView;
    private ArrayList<String> mDataList,mDataList2;
    private ArrayList<String> ID;
    private Handler handler;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_back:
                finish();
                break;
            default:
                break;
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_order);
        back = (ImageView) findViewById(R.id.order_back);
        back.setOnClickListener(this);

        /**
         * 初始化数据
         */
        ID = new ArrayList<String>();
        mDataList2 = new ArrayList<String>();
        mDataList = new ArrayList<String>();

        //创建handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    String info = (String) msg.obj;
                    inindate(info);
                    mSideslipListView = (SideslipListViews) findViewById(R.id.order_sideslipListView);
                    mSideslipListView.setAdapter(new CustomAdapter());//设置适配器
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                //FIXME 这里直接更新ui是不行的
                Message message = Message.obtain();
                //还有其他更新ui方式,runOnUiThread()等
                message.obj = getdata();
                message.what = 0x11;
                handler.sendMessage(message);
            }
        }).start();


    }
    /**
     * 自定义ListView适配器
     */
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (null == view) {
                view = View.inflate(Fragment_Me_order.this, R.layout.item_order, null);
                viewHolder = new ViewHolder();
                viewHolder.text_title = (TextView) view.findViewById(R.id.text_title);
                viewHolder.text_text =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.order_iamge);
                viewHolder.txtv_delete = (TextView) view.findViewById(R.id.txtv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (Fragment_Me_order.ViewHolder) view.getTag();
            }
            viewHolder.text_title.setText(mDataList2.get(position));
            viewHolder.text_text.setText(mDataList.get(position));

            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Fragment_Me_order.this, mDataList.get(pos) + "已删除",
                            Toast.LENGTH_SHORT).show();
                    Bmob.delete("Order",ID.get(pos));
                    mDataList.remove(pos);
                    mDataList2.remove(pos);
                    notifyDataSetChanged();
                    mSideslipListView.turnNormal();
                }
            });
            return view;
        }
    }
    class ViewHolder {
        public ImageView imageView;
        public TextView text_text;
        public TextView text_title;
        public TextView txtv_delete;
    }
    private String getdata(){
        String re;
        re = Bmob.findAll("Order");
        return re;
    }
    private void inindate(String re){
        InitBmob.Initbmob();
        String text,title;;
        String id;
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
                text = jsonFirst.getString("objectId");
                if (text != null) {
                    mDataList2.add(text);
                }
                //取出这个json中的值
                id = jsonFirst.getString("id");
                if (id != null) {
                    ID.add(id);
                }
                //取出这个json中的值
                title = jsonFirst.getString("Time");
                if (title != null) {
                    mDataList.add(title);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
