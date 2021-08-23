package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.SideslipListView_lost;
import com.njupt.zyhy.bmob.restapi.Bmob;
import com.njupt.zyhy.unicloud.UnicloudApi;


public class Fragment_Me_lost extends Activity implements View.OnClickListener{
    private ImageView back;
    private static final String TAG = "MainActivity";
    private SideslipListView_lost mSideslipListView;
    private ImageButton imageButton;
    private JSONArray DataJSONArray;
    private Handler handler;
    private SharedPreferences sp;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lost_back:
                finish();
                break;
            case R.id.add_lost:
                Intent intent = new Intent(this, Fragment_Me_lost_add.class);
                startActivity(intent);
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
        setContentView(R.layout.fragment_me_lost);
        back = (ImageView) findViewById(R.id.lost_back);
        back.setOnClickListener(this);
        imageButton = (ImageButton) findViewById(R.id.add_lost);
        imageButton.setOnClickListener(this);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        //创建handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");
                    mSideslipListView = (SideslipListView_lost) findViewById(R.id.lost_sideslipListView);
                    mSideslipListView.setAdapter(new CustomAdapter());//设置适配器
                    //设置item点击事件
                    mSideslipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i(TAG, DataJSONArray.getJSONObject(position).getString("title") + "被点击了");
                            }
                        }
                    });
                    //设置item长按事件
                    mSideslipListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i("TAG", DataJSONArray.getJSONObject(position).getString("title") + "被长按了");
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
                    message.obj = GetData("uni-data-lost");
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
                view = View.inflate(Fragment_Me_lost.this, R.layout.item_lost, null);
                viewHolder = new ViewHolder();
                viewHolder.text_title = (TextView) view.findViewById(R.id.text_title);
                viewHolder.text_address =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.lost_image = (ImageView) view.findViewById(R.id.lost_iamge);
                viewHolder.text_date = (TextView) view.findViewById(R.id.text_date);
                viewHolder.text_user = (TextView) view.findViewById(R.id.text_user);
                viewHolder.text_get = (TextView) view.findViewById(R.id.text_get);
                viewHolder.txtv_delete = (TextView) view.findViewById(R.id.txtv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.text_title.setText(OneDate.getString("title"));
            viewHolder.text_address.setText(OneDate.getString("address"));

            viewHolder.lost_image.setImageBitmap(GetHttpBitmap.getHttpBitmap(OneDate.getString("image")));
            viewHolder.text_date.setText(OneDate.getString("add_time"));

            if(sp.getString("id","").equals(OneDate.getString("user_id"))){
                viewHolder.text_user.setText("我发布的");
                viewHolder.text_user.setBackgroundResource(R.drawable.confirmdialog_text_orange);
                viewHolder.text_user.setTextSize(10);
            }else{
                viewHolder.text_user.setText(OneDate.getString("user_id"));
                viewHolder.text_user.setTextSize(6);
                viewHolder.text_user.setBackgroundResource(R.drawable.confirmdialog_text_orange2);
            }
            if(OneDate.getString("get").equals("false")){
                viewHolder.text_get.setText("未领取");
                viewHolder.text_get.setBackgroundResource(R.drawable.confirmdialog_text_red);
            }else{
                viewHolder.text_get.setText("已领取");
                viewHolder.text_get.setBackgroundResource(R.drawable.confirmdialog_text_green);
            }

            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(OneDate.getBoolean("been_to")){
                        Toast.makeText(Fragment_Me_lost.this,  "不能操作",
                                Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(Fragment_Me_lost.this,  "已取消发布",
                                Toast.LENGTH_SHORT).show();
                        DataJSONArray.remove(pos);
                        try {
                            UnicloudApi.DeleteData(sp.getString("token",""),"uni-data-lost",OneDate.getString("_id"));
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
        public ImageView lost_image;
        public TextView text_address, text_title, text_date, text_user, text_get, txtv_delete;
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