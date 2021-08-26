package com.njupt.zyhy.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.Adapter.MsgAdapter;
import com.njupt.zyhy.Fragment_exhibition_collect;
import com.njupt.zyhy.Fragment_Home_seach;
import com.njupt.zyhy.Fragment_collection_detail;
import com.njupt.zyhy.R;
import com.njupt.zyhy.bean.Msg;
import com.njupt.zyhy.bean.MsgLab;
import com.njupt.zyhy.unicloud.UnicloudApi;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Collection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Collection extends Fragment implements AdapterView.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView deng;

    private List<CItem> CnameList;
    private GridView gridView;
    private ImageView imageView;

    private GridView mLvMsgList;
    private List<Msg> mDatas = new ArrayList<>();
    private MsgAdapter mAdapter;


    private JSONArray C_DataJSONArray;
    private Handler handler;
    private SharedPreferences sp;

    public Fragment_Collection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Collection.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Collection newInstance(String param1, String param2) {
        Fragment_Collection fragment = new Fragment_Collection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__collection, container, false);

        gridView = (GridView) view.findViewById(R.id.grid);

        imageView = (ImageView) view.findViewById(R.id.imageView6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Home_seach.class);
                startActivity(intent);
            }
        });

        // 点击登记
        deng = (ImageView) view.findViewById(R.id.imageView17);
        deng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_exhibition_collect.class);
                startActivity(intent);
            }
        });


        //创建handler
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    C_DataJSONArray = DataJSONObject.getJSONArray("data");

                    setData();
                    setGridView();

                    mLvMsgList = view.findViewById(R.id.id_lv_msgList);

                    // 设置纵向数据
                    //mDatas.addAll(MsgLab.generateMockList(C_Name,C_Pic1));
                    mDatas.addAll(MsgLab.generateMockList(C_DataJSONArray));

                    mAdapter = new MsgAdapter(getActivity(), mDatas);
                    mLvMsgList.setAdapter(mAdapter);
                    // 纵向的gridview的item的点击事件
                    mLvMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.e("onItemClick", mDatas.get(position).getTitle());
                            //可以把要传递的参数放到一个bundle里传递过去，bumdle可以看做一个特殊的map。
                            Bundle bundle = new Bundle() ;

                            Intent intent = new Intent(getActivity(), Fragment_collection_detail.class);
                            JSONObject Data = C_DataJSONArray.getJSONObject(position);
                            bundle.putString("C_Name",Data.getString("name"));
                            bundle.putString("C_Introduce",Data.getString("introduction"));
                            bundle.putString("C_Voice",Data.getJSONArray("video").getString(0));
                            bundle.putString("C_Pic1",Data.getJSONArray("image").getString(0));
                            bundle.putString("C_Pic2",Data.getJSONArray("image").getString(1));
                            bundle.putString("C_Pic3",Data.getJSONArray("image").getString(2));
                            intent.putExtras(bundle) ;

                            startActivity(intent);
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
                    C_message.obj = GetData("uni-data-collection");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();

        return view;
    }

    /**设置横向GirdView参数，绑定数据*/
    private void setGridView() {
        int size = CnameList.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 2) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(getActivity().getApplicationContext(),
                CnameList);
        gridView.setAdapter(adapter);
        // 横向的gridview的item的点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 设置纵向数据
                    mDatas.clear();
                    mDatas.addAll(MsgLab.generateMockList(C_DataJSONArray));
                    mAdapter.notifyDataSetChanged();
                    Log.e("onItemClick", CnameList.get(position).getCName());
                    System.out.println(position);
                } else {
                    try {
                        mDatas.clear();
                        mDatas.addAll(MsgLab.generateMockList(UnicloudApi.GetDatabycondition(sp.getString("token",""),"uni-data-collection",String.valueOf(position)).getJSONArray("data")));
                        mAdapter.notifyDataSetChanged();
                        Log.e("onItemClick", CnameList.get(position).getCName());
                        System.out.println(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**横向GirdView 数据适配器*/
    public class GridViewAdapter extends BaseAdapter {
        Context context;
        List<CItem> list;
        public GridViewAdapter(Context _context, List<CItem> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.hroizontal_list_item, null);
            TextView tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            CItem city = list.get(position);
            tvCity.setText(city.getCName());
            return convertView;
        }
    }
    // 横向数据
    public class CItem {
        private String cName;

        public String getCName() {
            return cName;
        }

        public void setCName(String cName) {
            this.cName = cName;
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
    /**横向设置数据*/
    private void setData() {
        CnameList = new ArrayList<CItem>();
        CItem item = new CItem();
        item.setCName("全部");
        CnameList.add(item);
        item = new CItem();
        item.setCName("古籍图书");
        CnameList.add(item);
        item = new CItem();
        item.setCName("文件宣传品");
        CnameList.add(item);
        item = new CItem();
        item.setCName("名人遗物");
        CnameList.add(item);
        item = new CItem();
        item.setCName("书法绘画");
        CnameList.add(item);
        item = new CItem();
        item.setCName("家具");
        CnameList.add(item);
        item = new CItem();
        item.setCName("武器弹药");
        CnameList.add(item);
        item = new CItem();
        item.setCName("玺印符牌");
        CnameList.add(item);
        item = new CItem();
        item.setCName("档案文书");
        CnameList.add(item);
        item = new CItem();
        item.setCName("钱币");
        CnameList.add(item);
        item = new CItem();
        item.setCName("度量衡器");
        CnameList.add(item);
        item = new CItem();
        item.setCName("织绣");
        CnameList.add(item);
        CnameList.addAll(CnameList);
    }

    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }

}