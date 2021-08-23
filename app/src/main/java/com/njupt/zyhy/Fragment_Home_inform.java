package com.njupt.zyhy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Home_inform extends Activity implements View.OnClickListener{
    private TextView tv_inform;
    private ImageView back;
    private ListView lv_inform;
    private List<String> list = new ArrayList<String>();
    private InformAdapter adapter = null;
    private ArrayList<String> Title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**加载xml文件*/
        setContentView(R.layout.fragment_home_inform);

        Intent intent=getIntent();
        Title = intent.getStringArrayListExtra("inform");
        lv_inform = (ListView)findViewById(R.id.lv_inform);
        back = (ImageView)findViewById(R.id.informback);
        back.setOnClickListener(this);
        setData(Title);// 给listView设置adapter
    }
    private void setData(ArrayList<String> Title) {
        initData(Title);// 初始化数据
        adapter = new InformAdapter(list, this );
        lv_inform.setAdapter(adapter);
    }

    private void initData(ArrayList<String> Title) {
        for(int i= 0 ; i < Title.size(); i++) {
            list.add(Title.get(i));
        }

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
}