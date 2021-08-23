package com.njupt.zyhy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.njupt.zyhy.R;
import com.njupt.zyhy.bean.Msg;
import com.njupt.zyhy.bean.W_Msg;

import java.util.List;

public class W_MsgAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<W_Msg> mDatas;

    public W_MsgAdapter(Context context, List<W_Msg> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public W_Msg getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_w_msg, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mIvImg = convertView.findViewById(R.id.id_iv_img);
            viewHolder.mTvTitle = convertView.findViewById(R.id.id_tv_title);
            viewHolder.mPing = convertView.findViewById(R.id.id_ping);
            viewHolder.mMater = convertView.findViewById(R.id.id_mater);
            viewHolder.mGuge = convertView.findViewById(R.id.id_guge);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        W_Msg msg = mDatas.get(position);
        viewHolder.mIvImg.setImageBitmap(msg.getImgResId());
        viewHolder.mTvTitle.setText(msg.getTitle());
        viewHolder.mPing.setText("价格: "+msg.getPring());
        viewHolder.mMater.setText("材质: "+msg.getMater());
        viewHolder.mGuge.setText("规格: "+msg.getGuge());


        return convertView;
    }

    public static class ViewHolder {
        ImageView mIvImg;
        TextView mTvTitle;
        TextView mPing;
        TextView mMater;
        TextView mGuge;

    }
}
