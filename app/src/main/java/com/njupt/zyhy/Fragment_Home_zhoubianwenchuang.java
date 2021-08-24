package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.Adapter.W_MsgAdapter;
import com.njupt.zyhy.bean.W_Msg;
import com.njupt.zyhy.bean.W_MsgLab;
import com.njupt.zyhy.unicloud.UnicloudApi;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Home_zhoubianwenchuang extends Activity implements View.OnClickListener{
    private ImageView back;
    private ImageView ding;
    private GridView mLvMsgList;
    private List<W_Msg> mDatas = new ArrayList<>();
    private W_MsgAdapter mAdapter;

    private Handler handler;
    private SharedPreferences sp;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_zhoubianwenchuang);
        back = (ImageView)findViewById(R.id.zhoubianback);
        back.setOnClickListener(this);
        ding = (ImageView)findViewById(R.id.wenchuang_ding);
        ding.setOnClickListener(this);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        mLvMsgList = (GridView) findViewById(R.id.w_lv_msgList);

        /**
         * 创建handler 初始化数据
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    mDatas.addAll(W_MsgLab.generateMockList(DataJSONObject.getJSONArray("data")));

                    mAdapter = new W_MsgAdapter(getBaseContext(), mDatas);
                    mLvMsgList.setAdapter(mAdapter);

                    ListAdapter listAdapter = mLvMsgList.getAdapter();
                    int totalHeight = 0;
                    if (listAdapter == null) {
                        return;
                    }
                    for (int index = 0, len = listAdapter.getCount(); index < len; index++) {
                        View listViewItem = listAdapter.getView(index , null, mLvMsgList);
                        // 计算子项View 的宽高
                        listViewItem.measure(0, 0);
                        // 计算所有子项的高度和
                        totalHeight += listViewItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = mLvMsgList.getLayoutParams();
                    // listView.getDividerHeight()获取子项间分隔符的高度
                    // params.height设置ListView完全显示需要的高度
                    mLvMsgList.setLayoutParams(params);

                    // 纵向的gridview的item的点击事件
                    mLvMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                    message.obj = GetData("uni-data-keepsake");
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
            case R.id.zhoubianback:
                finish();
                break;
            case R.id.wenchuang_ding:
                Intent intent = new Intent(this, Fragment_guide_map.class);
                intent.putExtra("bit", "wen");
                startActivity(intent);
            default:
                break;
        }
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }
}
