package com.njupt.zyhy;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.bean.SideslipListView;
import com.njupt.zyhy.unicloud.UnicloudApi;

public class Fragment_Me_feedback extends Activity implements View.OnClickListener {

    private EditText editText;
    private Button button;
    private String edittext;
    private ImageView back;
    private SharedPreferences sp;
    private JSONArray DataJSONArray;
    private SideslipListView mSideslipListView;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_opinion);

        editText = findViewById(R.id.et_advice_content);
        button = findViewById(R.id.put);

        editText.setOnClickListener(this);
        button.setOnClickListener(this);

        back = (ImageView) findViewById(R.id.opinion_back);
        back.setOnClickListener(this);

        // 获取用户缓存信息
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
                    mSideslipListView = (SideslipListView) findViewById(R.id.fd_sideslipListView);
                    mSideslipListView.setAdapter(new CustomAdapter());//设置适配器
                    //设置item点击事件
                    mSideslipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mSideslipListView.isAllowItemClick()) {
                                Log.i("", DataJSONArray.getJSONObject(position).getString("title") + "被点击了");
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
                Message C_message = Message.obtain();
                //还有其他更新ui方式,runOnUiThread()等
                try {
                    C_message.obj = GetData("uni-data-feedback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.put:
                edittext = editText.getText().toString();
                if (TextUtils.isEmpty(edittext)) {
                    showToast("反馈内容不能为空!");
                    return;
                }
                else{
                    // 意见反馈
                    try {
                        UnicloudApi.Add_Feedback("uni-data-feedback",sp.getString("token", ""),sp.getString("id", ""),edittext);
                        showToast("您的反馈已收到!");
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("反馈失败！"+e.toString());
                    }
                }
                break;
            case R.id.opinion_back:
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
            return DataJSONArray.get(position);
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
                view = View.inflate(Fragment_Me_feedback.this, R.layout.item_feed, null);
                viewHolder = new ViewHolder();
                viewHolder.text_number = (TextView) view.findViewById(R.id.text_number);
                viewHolder.text_text =  (TextView) view.findViewById(R.id.text_text);
                viewHolder.text_adopt =  (TextView) view.findViewById(R.id.text_adopt);
                viewHolder.text_time =  (TextView) view.findViewById(R.id.text_time);
                viewHolder.txtv_delete = (TextView) view.findViewById(R.id.txtv_delete);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.text_number.setText(OneDate.getString("_id"));
            viewHolder.text_text.setText(OneDate.getString("message"));
            viewHolder.text_time.setText(OneDate.getString("time"));

            if(OneDate.getInteger("adopt").equals(1)){
                viewHolder.text_adopt.setText("已采纳");
                viewHolder.text_adopt.setBackgroundResource(R.drawable.confirmdialog_text_green);
            }else if(OneDate.getInteger("adopt").equals(0)) {
                viewHolder.text_adopt.setText("未采纳");
                viewHolder.text_adopt.setBackgroundResource(R.drawable.confirmdialog_text_red);
            }else{
                viewHolder.text_adopt.setText("审核中");
                viewHolder.text_adopt.setBackgroundResource(R.drawable.confirmdialog_text_lightgrey);
            }

            final int pos = position;
            viewHolder.txtv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Fragment_Me_feedback.this, "已删除",
                            Toast.LENGTH_SHORT).show();
                    DataJSONArray.remove(pos);
                    try {
                        UnicloudApi.DeleteData(sp.getString("token",""),"uni-data-feedback",OneDate.getString("_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                    mSideslipListView.turnNormal();
                }
            });
            return view;
        }
    }

    class ViewHolder {
        public TextView text_time,text_adopt, text_number,text_text, txtv_delete;
    }



    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}