package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.SideslipListView_order;
import com.njupt.zyhy.unicloud.UnicloudApi;

public class Fragment_Me_order extends Activity implements View.OnClickListener{
    private ImageView back;
    private SideslipListView_order mSideslipListView;


    private Handler handler;
    private SharedPreferences sp;
    private JSONArray DataJSONArray;

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
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_order);
        back = (ImageView) findViewById(R.id.order_back);
        back.setOnClickListener(this);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        /**
         * 初始化数据
         */

        //创建handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");
                    mSideslipListView = (SideslipListView_order) findViewById(R.id.order_sideslipListView);
                    mSideslipListView.setAdapter(new CustomAdapter());//设置适配器

                    mSideslipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i("TAG", DataJSONArray.getJSONObject(position).getString("name") + "被点击了");
                            }
                        }
                    });
                    //设置item长按事件
                    mSideslipListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i("TAG", DataJSONArray.getJSONObject(position).getString("name") + "被长按了");
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
                try {
                    message.obj = GetData("uni-data-order");
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
            return DataJSONArray.getJSONObject(position).getString("id");
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
                view = View.inflate(Fragment_Me_order.this, R.layout.item_order, null);
                viewHolder = new ViewHolder();
                viewHolder.text_number = (TextView) view.findViewById(R.id.text_number);
                viewHolder.text_name =  (TextView) view.findViewById(R.id.text_name);
                viewHolder.text_date =  (TextView) view.findViewById(R.id.text_date);
                viewHolder.text_idcard =  (TextView) view.findViewById(R.id.text_idcard);
                viewHolder.text_get =  (TextView) view.findViewById(R.id.text_get);
                viewHolder.txtv_delete = (TextView) view.findViewById(R.id.txtv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (Fragment_Me_order.ViewHolder) view.getTag();
            }
            if(sp.getString("id","").equals(OneDate.getString("id"))){
                viewHolder.text_number.setText(OneDate.getString("_id"));
                viewHolder.text_name.setText(OneDate.getString("name"));
                viewHolder.text_date.setText(OneDate.getString("diagTime")+" "+OneDate.getString("time_interval"));
                viewHolder.text_idcard.setText(OneDate.getString("idcard"));


                if(OneDate.getBoolean("been_to")){
                    viewHolder.text_get.setText("已过期");
                    viewHolder.text_get.setBackgroundResource(R.drawable.confirmdialog_text_lightgrey);
                }else{
                    viewHolder.text_get.setText("未过期");
                    viewHolder.text_get.setBackgroundResource(R.drawable.confirmdialog_text_green);
                }
            }

            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(OneDate.getBoolean("been_to")){
                        Toast.makeText(Fragment_Me_order.this,  "已过期,不能删除",
                                Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(Fragment_Me_order.this,  "已取消预约",
                                Toast.LENGTH_SHORT).show();
                        DataJSONArray.remove(pos);
                        try {
                            UnicloudApi.DeleteData(sp.getString("token",""),"uni-data-order",OneDate.getString("_id"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                        mSideslipListView.turnNormal();
                    }
                }
            });
            return view;
        }
    }
    class ViewHolder {
        public TextView text_number, text_name, text_date, text_idcard, txtv_delete ,text_get;
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
