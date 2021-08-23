package com.njupt.zyhy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.njupt.zyhy.Adapter.MyAdapter;
import com.njupt.zyhy.bean.FilterListener;

import androidx.annotation.Nullable;

public class Fragment_Home_seach extends Activity implements View.OnClickListener {
    TextView textView;
    private EditText et_ss;
    private ListView lsv_ss;
    private List<String> list = new ArrayList<String>();
    private MyAdapter adapter = null;
    private String Collection,Exhibit;
    private ArrayList<String> C_id,C_Class,C_Voice,C_Name,C_Introduce,C_Pic1,C_Pic2,C_Pic3;
    private ArrayList<String> Z_Title,Z_Subtitle,Z_Text,Z_Pic1,Z_Pic2,Z_Pic3,Z_Pic4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_search);
        textView =(TextView)findViewById(R.id.tv_search_exit);
        textView.setOnClickListener(this);

        Intent intent=getIntent();
        Collection =intent.getStringExtra("Collection");
        Exhibit =intent.getStringExtra("Exhibit");

        setViews();// 控件初始化
        setData();// 给listView设置adapter
        setListeners();// 设置监听
    }
    private void setData() {
        initData(Exhibit,Collection);// 初始化数据

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
                for (int i = 0; i < Z_Title.size(); i++) {
                    if (countStr(Z_Title.get(i),filter_lists.get(position))) {
                        Intent intent = new Intent(getApplicationContext(), Fragment_exhabition_detail.class);
                        intent.putExtra("Z_Title", Z_Title.get(i));
                        intent.putExtra("Z_Subtitle", Z_Subtitle.get(i));
                        intent.putExtra("Z_Text", Z_Text.get(i));
                        intent.putExtra("Z_Pic1", Z_Pic1.get(i));
                        intent.putExtra("Z_Pic2", Z_Pic2.get(i));
                        intent.putExtra("Z_Pic3", Z_Pic3.get(i));
                        intent.putExtra("Z_Pic4", Z_Pic4.get(i));
                        startActivity(intent);
                        break;
                    }
                }
                for (int i = 0; i < C_Name.size(); i++) {
                    if (countStr(C_Name.get(i),filter_lists.get(position))) {
                        Intent intent = new Intent(getApplicationContext(), Fragment_collection_detail.class);
                        intent.putExtra("C_Name", C_Name.get(i));
                        intent.putExtra("C_Voice", C_Voice.get(i));
                        intent.putExtra("C_Introduce", C_Introduce.get(i));
                        intent.putExtra("C_Pic1", C_Pic1.get(i));
                        intent.putExtra("C_Pic2", C_Pic2.get(i));
                        intent.putExtra("C_Pic3", C_Pic3.get(i));
                        startActivity(intent);
                        break;
                    } else {
                        // 点击对应的item时，弹出toast提示所点击的内容
                        System.out.println(i);
                        System.out.println(Z_Title.equals(filter_lists.get(position)));
                        System.out.println(C_Name.equals(filter_lists.get(position)));
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

    /**
     * 简单的list集合添加一些测试数据
     */
    private void initData( String exhibit,String collection) {
        C_id = new ArrayList<String>();
        C_Class = new ArrayList<String>();
        C_Voice = new ArrayList<String>();
        C_Name = new ArrayList<String>();
        C_Introduce = new ArrayList<String>();
        C_Pic1 = new ArrayList<String>();
        C_Pic2 = new ArrayList<String>();
        C_Pic3 = new ArrayList<String>();

        // 初始化数据
        Z_Title  = new ArrayList<String>();
        Z_Subtitle  = new ArrayList<String>();
        Z_Text  = new ArrayList<String>();
        Z_Pic1 = new ArrayList<String>();
        Z_Pic2 = new ArrayList<String>();
        Z_Pic3 = new ArrayList<String>();
        Z_Pic4 = new ArrayList<String>();

        inindate2(exhibit);
        inindate(collection);
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
    private void inindate(String re){

        String id;
        String Class,Voice,Name,Introduce,Pic1,Pic2,Pic3;;
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
                id = jsonFirst.getString("id");
                if (id != null) {
                    C_id.add(id);
                }
                //取出这个json中的值
                Class = jsonFirst.getString("Class");
                if (Class != null) {
                    C_Class.add(Class);
                }
                //取出这个json中的值
                Voice = jsonFirst.getString("Voice");
                if (Voice != null) {

                    C_Voice.add(Voice);
                }
                //取出这个json中的值
                Name = jsonFirst.getString("Name");
                if (Name != null) {

                    C_Name.add(Name);
                    list.add(Name);
                }
                //取出这个json中的值
                Introduce = jsonFirst.getString("Introduce");
                if (Introduce != null) {

                    C_Introduce.add(Introduce);
                }
                //取出这个json中的值
                Pic1 = jsonFirst.getString("Pic1");
                if (Pic1 != null) {

                    C_Pic1.add(Pic1);
                }
                //取出这个json中的值
                Pic2 = jsonFirst.getString("Pic2");
                if (Pic2 != null) {

                    C_Pic2.add(Pic2);
                }
                //取出这个json中的值
                Pic3 = jsonFirst.getString("Pic3");
                if (Pic3 != null) {

                    C_Pic3.add(Pic3);
                }
            }
        }
    }
    private void inindate2(String re){
        String Title,Subtitle,Text,Pic1,Pic2,Pic3,Pic4;
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
                Title = jsonFirst.getString("Title");
                if (Title != null) {
                    Z_Title.add(Title);
                    list.add(Title);
                }
                //取出这个json中的值
                Subtitle = jsonFirst.getString("Subtitle");
                if (Subtitle != null) {
                    Z_Subtitle.add(Subtitle);
                }
                //取出这个json中的值
                Text = jsonFirst.getString("Text");
                if (Text != null) {
                    Z_Text.add(Text);
                }
                //取出这个json中的值
                Pic1 = jsonFirst.getString("Pic1");
                if (Pic1 != null) {
                    Z_Pic1.add(Pic1);
                }
                //取出这个json中的值
                Pic2 = jsonFirst.getString("Pic2");
                if (Pic2 != null) {
                    Z_Pic2.add(Pic2);
                }
                //取出这个json中的值
                Pic3 = jsonFirst.getString("Pic3");
                if (Pic3 != null) {
                    Z_Pic3.add(Pic3);
                }
                //取出这个json中的值
                Pic4 = jsonFirst.getString("Pic4");
                if (Pic4 != null) {
                    Z_Pic4.add(Pic4);
                }
            }
        }
    }
}
