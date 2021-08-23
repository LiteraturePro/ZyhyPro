package com.njupt.zyhy.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.njupt.zyhy.bean.Msg;
import com.njupt.zyhy.R;
import java.util.List;
public class MsgAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Msg> mDatas;

    public MsgAdapter(Context context, List<Msg> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Msg getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_msg, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mIvImg = convertView.findViewById(R.id.id_iv_img);
            viewHolder.mTvTitle = convertView.findViewById(R.id.id_tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Msg msg = mDatas.get(position);
        viewHolder.mIvImg.setImageBitmap(msg.getImgResId());
        viewHolder.mTvTitle.setText(msg.getTitle());
        return convertView;
    }

    public static class ViewHolder {
        ImageView mIvImg;
        TextView mTvTitle;
    }
}
