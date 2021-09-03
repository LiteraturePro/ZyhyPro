package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.SideslipListView_news;
import com.njupt.zyhy.unicloud.UnicloudApi;


public class Fragment_Me_news extends Activity implements View.OnClickListener{
    private ImageView back;
    private SideslipListView_news mSideslipListView;
    private Handler handler;
    private SharedPreferences sp;
    private JSONArray DataJSONArray;

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

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //创建handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");
                    mSideslipListView = (SideslipListView_news) findViewById(R.id.news_sideslipListView);
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
                try {
                    message.obj = GetData("uni-data-notice");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            return DataJSONArray.size();
        }

        @Override
        public Object getItem(int position) {
            return DataJSONArray.getJSONObject(position).getString("title");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            JSONObject OneDate = DataJSONArray.getJSONObject(position);

            if (null == view) {
                view = View.inflate(Fragment_Me_news.this, R.layout.item_news, null);
                viewHolder = new ViewHolder();
                viewHolder.news_text = (TextView) view.findViewById(R.id.news_text);
                viewHolder.news_title =  (TextView) view.findViewById(R.id.news_title);
                viewHolder.news_img =  (ImageView) view.findViewById(R.id.news_img);
                viewHolder.news_date =  (TextView) view.findViewById(R.id.news_date);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.news_date.setText(OneDate.getString("_add_time_str"));
            viewHolder.news_title.setText(OneDate.getString("title"));
            viewHolder.news_text.setText(OneDate.getString("excerpt"));
            viewHolder.news_img.setImageBitmap(GetHttpBitmap.getHttpBitmap(OneDate.getJSONArray("image").getString(0)));
            return view;
        }
    }

    class ViewHolder {
        public ImageView news_img;
        public TextView news_text,news_title, news_date;
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
