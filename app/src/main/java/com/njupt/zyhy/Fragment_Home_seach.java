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
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzj.gallery.library.views.BannerViewPager;
import com.njupt.zyhy.Adapter.MyAdapter;
import com.njupt.zyhy.bean.FilterListener;
import com.njupt.zyhy.bean.HistoryBean;
import com.njupt.zyhy.unicloud.UnicloudApi;

import androidx.annotation.Nullable;

public class Fragment_Home_seach extends Activity implements View.OnClickListener {
    TextView textView;
    private EditText et_ss;
    private ListView lsv_ss;
    private List<String> list = new ArrayList<String>();
    private MyAdapter adapter = null;
    private SharedPreferences sp;
    private Handler handler;
    private JSONArray C_DataJSONArray;
    private JSONArray Z_DataJSONArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_search);
        textView =(TextView)findViewById(R.id.tv_search_exit);
        textView.setOnClickListener(this);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        //创建handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    Z_DataJSONArray = DataJSONObject.getJSONArray("data");
                    for (int i= 0; i< Z_DataJSONArray.size(); i++){
                        String bean = Z_DataJSONArray.getJSONObject(i).getString("title");
                        list.add(bean);
                    }
                    try {
                        JSONObject DataJSONObject2 = GetData("uni-data-collection");
                        C_DataJSONArray = DataJSONObject2.getJSONArray("data");
                        for (int i= 0; i< C_DataJSONArray.size(); i++){
                            String bean = C_DataJSONArray.getJSONObject(i).getString("name");
                            list.add(bean);
                        }
                        setViews();// 控件初始化
                        setData();// 给listView设置adapter
                        setListeners();// 设置监听

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    C_message.obj = GetData("uni-data-exhibit");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();

    }
    private void setData() {

        // 这里创建adapter的时候，构造方法参数传了一个接口对象，回调接口中的方法来实现对过滤后的数据的获取
        adapter = new MyAdapter(list, this, new FilterListener() {
            // 回调方法获取过滤后的数据
            public void getFilterData(List<String> list) {
                // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                Log.e("TAG", "接口回调成功");
                Log.e("TAG", list.toString());
                setItemClick(list);
            }
        });
        lsv_ss.setAdapter(adapter);
    }


    /**
     * 给listView添加item的单击事件
     *  filter_lists  过滤后的数据集
     */
    protected void setItemClick(final List<String> filter_lists) {
        lsv_ss.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for (int i = 0; i < Z_DataJSONArray.size(); i++) {
                    if (countStr(Z_DataJSONArray.getJSONObject(i).getString("title"),filter_lists.get(position))) {
                        JSONObject OneDate = Z_DataJSONArray.getJSONObject(i);
                        Intent intent = new Intent(getApplicationContext(), Fragment_exhabition_detail.class);
                        intent.putExtra("Z_Title", OneDate.getString("title"));
                        intent.putExtra("Z_Subtitle", OneDate.getString("describe"));
                        intent.putExtra("Z_Text", OneDate.getString("text"));
                        intent.putExtra("Z_Pic1", OneDate.getJSONArray("image").getString(0));
                        intent.putExtra("Z_Pic2", OneDate.getJSONArray("image").getString(1));
                        intent.putExtra("Z_Pic3", OneDate.getJSONArray("image").getString(2));
                        intent.putExtra("Z_Pic4", OneDate.getJSONArray("image").getString(3));
                        startActivity(intent);
                        break;
                    }
                }
                for (int i = 0; i < C_DataJSONArray.size(); i++) {
                    if (countStr(C_DataJSONArray.getJSONObject(i).getString("name"),filter_lists.get(position))) {
                        JSONObject OneDate = C_DataJSONArray.getJSONObject(i);
                        Intent intent = new Intent(getApplicationContext(), Fragment_collection_detail.class);

                        //可以把要传递的参数放到一个bundle里传递过去，bumdle可以看做一个特殊的map。
                        Bundle bundle = new Bundle() ;
                        String Datas = "材质："+OneDate.getString("texture") +"\n"+"登记号:"+OneDate.getString("registration_number")+"\n"+"文物级别："+OneDate.getString("registration_number")+"\n"+"年份："+OneDate.getString("years")+"\n"+"规格："+OneDate.getString("size")+"\n";

                        bundle.putString("C_Name",OneDate.getString("name"));
                        bundle.putString("C_Introduce",Datas+"\n"+"\u3000"+OneDate.getString("introduction"));
                        bundle.putString("C_Voice",OneDate.getJSONArray("video").getString(0));
                        bundle.putString("C_Pic1",OneDate.getJSONArray("image").getString(0));
                        bundle.putString("C_Pic2",OneDate.getJSONArray("image").getString(1));
                        bundle.putString("C_Pic3",OneDate.getJSONArray("image").getString(2));
                        intent.putExtras(bundle) ;
                        startActivity(intent);
                        break;
                    } else {
                        // 点击对应的item时，弹出toast提示所点击的内容
                        System.out.println(i);
//                        System.out.println(Z_Title.equals(filter_lists.get(position)));
//                        System.out.println(C_Name.equals(filter_lists.get(position)));
                        //Toast.makeText(Fragment_Home_seach.this, filter_lists.get(position), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    /**
     * 判断str1中包含str2的个数
     * @param str1
     * @param str2
     * @return counter
     */
    public static boolean countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return false;
        } else if (str1.indexOf(str2) != -1) {
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return true;
        }
        return false;
    }
    private void setListeners() {
        // 没有进行搜索的时候，也要添加对listView的item单击监听
        setItemClick(list);

        /**
         * 对编辑框添加文本改变监听，搜索的具体功能在这里实现
         * 文本该变的时候进行搜索。关键方法是重写的onTextChanged（）方法。
         */
        et_ss.addTextChangedListener(new TextWatcher() {

            /**
             *
             * 编辑框内容改变的时候会执行该方法
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 如果adapter不为空的话就根据编辑框中的内容来过滤数据
                if(adapter != null){
                    adapter.getFilter().filter(s);
                }
                if (s.length() != 0) {
                    //显示Listview
                    lsv_ss.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                lsv_ss.setVisibility(View.GONE);

            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * 控件初始化
     */
    private void setViews() {
        et_ss = (EditText) findViewById(R.id.fragment_home_search);// EditText控件
        lsv_ss = (ListView)findViewById(R.id.list_sv);// ListView控件

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_exit:
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
