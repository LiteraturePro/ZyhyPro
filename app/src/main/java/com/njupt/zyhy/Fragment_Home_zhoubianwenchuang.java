package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.Adapter.MsgAdapter;
import com.njupt.zyhy.Adapter.W_MsgAdapter;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.Msg;
import com.njupt.zyhy.bean.MsgLab;
import com.njupt.zyhy.bean.W_Msg;
import com.njupt.zyhy.bean.W_MsgLab;
import com.njupt.zyhy.bmob.restapi.Bmob;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Home_zhoubianwenchuang extends Activity implements View.OnClickListener{
    private ImageView back;
    private ImageView ding;
    private GridView mLvMsgList;

    private ArrayList<String> W_name,W_pring,W_Material,W_Specifications,W_Pic;
    private List<W_Msg> mDatas = new ArrayList<>();
    private W_MsgAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_zhoubianwenchuang);
        back = (ImageView)findViewById(R.id.zhoubianback);
        back.setOnClickListener(this);
        ding = (ImageView)findViewById(R.id.wenchuang_ding);
        ding.setOnClickListener(this);

        mLvMsgList = (GridView) findViewById(R.id.w_lv_msgList);

        W_name = new ArrayList<String>();
        W_pring = new ArrayList<String>();
        W_Material = new ArrayList<String>();
        W_Specifications = new ArrayList<String>();
        W_Pic = new ArrayList<String>();

        inindate();
        // 设置纵向数据
        mDatas.addAll(W_MsgLab.generateMockList(W_name,W_Pic,W_pring,W_Material,W_Specifications));

        mAdapter = new W_MsgAdapter(this, mDatas);
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
    private void inindate(){
        InitBmob.Initbmob();
        String re,id;
        String Class,Voice,Name,Introduce;
        re = Bmob.findAll("Cultural_Creation");
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
                id = jsonFirst.getString("Name");
                if (id != null) {
                    W_name.add(id);
                }
                //取出这个json中的值
                Class = jsonFirst.getString("Pring");
                if (Class != null) {
                    W_pring.add(Class);
                }
                //取出这个json中的值
                Voice = jsonFirst.getString("Material");
                if (Voice != null) {

                    W_Material.add(Voice);
                }
                //取出这个json中的值
                Name = jsonFirst.getString("Specifications");
                if (Name != null) {

                    W_Specifications.add(Name);
                }
                //取出这个json中的值
                Introduce = jsonFirst.getString("Pic");
                if (Introduce != null) {

                    W_Pic.add(Introduce);
                }
            }
        }
    }
}
