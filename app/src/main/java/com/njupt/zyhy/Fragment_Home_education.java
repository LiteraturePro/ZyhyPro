package com.njupt.zyhy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.SideslipListView;
import com.njupt.zyhy.bean.SideslipListViews;
import com.njupt.zyhy.bmob.restapi.Bmob;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Fragment_Home_education extends Activity implements View.OnClickListener{
    private ImageView back;
    private SideslipListViews mSideslipListView;
    private static final String TAG = "MainActivity";
    private ArrayList<Bitmap> Lost_bit;
    private ArrayList<String> Text_address;
    private ArrayList<String> Text_title;
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_education);
        back = (ImageView)findViewById(R.id.canguanback);
        back.setOnClickListener(this);

        Lost_bit = new ArrayList<Bitmap>();
        Text_address = new ArrayList<String>();
        Text_title = new ArrayList<String>();
        //创建handler

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    String info = (String) msg.obj;
                    inindate(info);
                    mSideslipListView = (SideslipListViews) findViewById(R.id.lost_sideslipListView);

                    mSideslipListView.setAdapter(new Fragment_Home_education.CustomAdapter());//设置适配器
                    //设置item点击事件
                    ListAdapter listAdapter = mSideslipListView.getAdapter();
                    int totalHeight = 0;
                    if (listAdapter == null) {
                        return;
                    }
                    for (int index = 0, len = listAdapter.getCount(); index < len; index++) {
                        View listViewItem = listAdapter.getView(index , null, mSideslipListView);
                        // 计算子项View 的宽高
                        listViewItem.measure(0, 0);
                        // 计算所有子项的高度和
                        totalHeight += listViewItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = mSideslipListView.getLayoutParams();
                    // listView.getDividerHeight()获取子项间分隔符的高度
                    // params.height设置ListView完全显示需要的高度
                    params.height = totalHeight+ (mSideslipListView.getDividerHeight() * (listAdapter.getCount() - 1));
                    mSideslipListView.setLayoutParams(params);
                    mSideslipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i(TAG, Text_title.get(position) + "被点击了");
                            }
                        }
                    });
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.canguanback:
                finish();
                break;
            default:
                break;
        }
    }
    /**
     * 自定义ListView适配器
     */
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Text_title.size();
        }

        @Override
        public Object getItem(int position) {
            return Text_title.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Fragment_Home_education.ViewHolder viewHolder;
            View view = convertView;
            if (null == view) {
                view = View.inflate(Fragment_Home_education.this, R.layout.item_dangjian, null);
                viewHolder = new ViewHolder();
                viewHolder.text_title = (TextView) view.findViewById(R.id.text_title);
                viewHolder.text_text =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.lost_image = (ImageView) view.findViewById(R.id.lost_iamge);
                view.setTag(viewHolder);
            } else {
                viewHolder = (Fragment_Home_education.ViewHolder) view.getTag();
            }
            viewHolder.text_title.setText(Text_title.get(position));
            viewHolder.text_text.setText(Text_address.get(position));
            viewHolder.lost_image.setImageBitmap(Lost_bit.get(position));
            return view;
        }
    }

    class ViewHolder {
        public ImageView lost_image;
        public TextView text_text;
        public TextView text_title;
        public TextView txtv_delete;
    }
    private String getdata(){
        String re;
        re = Bmob.findAll("Dangjian");
        return re;
    }

    private void inindate(String re){
        InitBmob.Initbmob();
        String text,title;;
        String id;
        JSONObject jsonObject = null;
        //re = Bmob.findAll("Lostinformation");
        jsonObject = JSON.parseObject(re);
        System.out.println(jsonObject);
        //获取当前嵌套下的属性
        String status = jsonObject.getString("results");
        if (status!=null){
            //获取嵌套中的json串,细心观察 content为json数组，里面可放多个json对象
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            System.out.println(jsonArray);

            for(int i =0;i < jsonArray.size(); i++) {
                JSONObject jsonFirst = jsonArray.getJSONObject(i);

                //取出这个json中的值
                text = jsonFirst.getString("title");
                if (text != null) {
                    Text_title.add(text);
                }
                //取出这个json中的值
                id = jsonFirst.getString("text");
                if (id != null) {
                    Text_address.add(id);
                }
                //取出这个json中的值
                title = jsonFirst.getString("pic");
                if (title != null) {

                    Lost_bit.add(GetHttpBitmap.getHttpBitmap(title));
                }
            }
        }
    }

}
