package com.njupt.zyhy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.SideslipListView;
import com.njupt.zyhy.bmob.restapi.Bmob;
import java.util.ArrayList;

public class Fragment_Me_news extends Activity implements View.OnClickListener{
    private ImageView back;
    private static final String TAG = "MainActivity";
    private SideslipListView mSideslipListView;
    private ArrayList<String> mDataList,mDataList2;
    private ArrayList<String> ID;
    private Handler handler;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.news_back:
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
        setContentView(R.layout.fragment_me_news);
        back = (ImageView) findViewById(R.id.news_back);
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
                    mSideslipListView = (SideslipListView) findViewById(R.id.news_sideslipListView);
                    mSideslipListView.setAdapter(new CustomAdapter());//设置适配器
                    //设置item点击事件
                    mSideslipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i(TAG, mDataList.get(position) + "被点击了");
                            }
                        }
                    });
                    //设置item长按事件
                    mSideslipListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                return true;//返回true表示本次事件被消耗了，若返回
                            }
                            return false;
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
                view = View.inflate(Fragment_Me_news.this, R.layout.item_news, null);
                viewHolder = new ViewHolder();
                viewHolder.text_title = (TextView) view.findViewById(R.id.text_title);
                viewHolder.text_text =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.txtv_delete = (TextView) view.findViewById(R.id.txtv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.text_title.setText(mDataList2.get(position));
            viewHolder.text_text.setText(mDataList.get(position));

            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Fragment_Me_news.this, mDataList.get(pos) + "已删除",
                            Toast.LENGTH_SHORT).show();
                    Bmob.delete("News",ID.get(pos));
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
        public TextView text_text;
        public TextView text_title;
        public TextView txtv_delete;
    }
    private String getdata(){
        String re;
        re = Bmob.findAll("News");
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
            //System.out.println(jsonArray);

            for(int i =0;i < jsonArray.size(); i++) {
                JSONObject jsonFirst = jsonArray.getJSONObject(i);

                //取出这个json中的值
                text = jsonFirst.getString("title");
                if (text != null) {
                    mDataList2.add(text);
                }
                //取出这个json中的值
                id = jsonFirst.getString("objectId");
                if (id != null) {
                    ID.add(id);
                }
                //取出这个json中的值
                title = jsonFirst.getString("title");
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
