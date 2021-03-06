package com.njupt.zyhy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.SideslipListView_news;
import com.njupt.zyhy.unicloud.UnicloudApi;

public class Fragment_Home_Dangjiang extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;

    private SideslipListView_news mSideslipListView;
    private static final String TAG = "MainActivity";
    private Handler handler;
    private SharedPreferences sp;
    private JSONArray DataJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_dangjiang);
        back = (ImageView)findViewById(R.id.dangjian_back);
        back.setOnClickListener(this);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //创建handler

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");

                    mSideslipListView = (SideslipListView_news) findViewById(R.id.dj_sideslipListView);
                    mSideslipListView.setAdapter(new Fragment_Home_Dangjiang.CustomAdapter());//设置适配器
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
                                Log.i(TAG, DataJSONArray.getJSONObject(position).getString("title") + "被点击了");
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
                try {
                    message.obj = GetData("uni-data-party");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                message.what = 0x11;
                handler.sendMessage(message);
            }
        }).start();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dangjian_back:
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
            Fragment_Home_Dangjiang.ViewHolder viewHolder;
            View view = convertView;
            JSONObject OneDate = DataJSONArray.getJSONObject(position);
            if (null == view) {
                view = View.inflate(Fragment_Home_Dangjiang.this, R.layout.item_dangjian, null);
                viewHolder = new ViewHolder();
                viewHolder.text_title = (TextView) view.findViewById(R.id.text_title);
                viewHolder.text_text =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.lost_image = (ImageView) view.findViewById(R.id.lost_iamge);
                view.setTag(viewHolder);
            } else {
                viewHolder = (Fragment_Home_Dangjiang.ViewHolder) view.getTag();
            }
            viewHolder.text_title.setText(OneDate.getString("title"));
            viewHolder.text_text.setText(OneDate.getString("text"));
            viewHolder.lost_image.setImageBitmap(GetHttpBitmap.getHttpBitmap(OneDate.getJSONArray("image").getString(0)));
            return view;
        }
    }

    class ViewHolder {
        public ImageView lost_image;
        public TextView text_text;
        public TextView text_title;
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }

}