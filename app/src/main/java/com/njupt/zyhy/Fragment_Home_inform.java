package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.unicloud.UnicloudApi;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Home_inform extends Activity implements View.OnClickListener{
    private TextView tv_inform;
    private ImageView back;
    private ListView lv_inform;
    private List<String> list = new ArrayList<String>();
    private InformAdapter adapter = null;
    private ArrayList<String> Title;
    private Handler handler;
    private SharedPreferences sp;

    private JSONArray DataJSONArray;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        /**加载xml文件*/
        setContentView(R.layout.fragment_home_inform);

        lv_inform = (ListView)findViewById(R.id.lv_inform);
        back = (ImageView)findViewById(R.id.informback);
        back.setOnClickListener(this);

        //创建handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");
                    for(int i = 0; i < DataJSONArray.size(); i++){
                        list.add(DataJSONArray.getJSONObject(i).getString("title"));
                    }
                    adapter = new InformAdapter(list, getBaseContext() );
                    lv_inform.setAdapter(adapter);

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
                    C_message.obj = GetData("uni-data-notice");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();

    }

    public class InformAdapter extends BaseAdapter {

        private List<String> list = new ArrayList<String>();
        private Context context;

        public InformAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inform, null);
            tv_inform= (TextView) convertView.findViewById(R.id.tv_inform);
            tv_inform.setText(list.get(position));
            return convertView;
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.informback:
                finish();
                break;
            default:
                break;
        }
    }
    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }
}