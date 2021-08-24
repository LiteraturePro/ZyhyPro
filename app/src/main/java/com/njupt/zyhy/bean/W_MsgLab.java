package com.njupt.zyhy.bean;

import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class W_MsgLab {

    public static List<W_Msg> generateMockList(JSONArray DataJSONArray) {
        List<W_Msg> msgList = new ArrayList<>();
        //利用泛型，以及上一个文件来读取出我们所需要的内容
        for(int i = 0; i< DataJSONArray.size();i++) {
            W_Msg msg = new W_Msg(i,
                    DataJSONArray.getJSONObject(i).getString("name"),
                    GetHttpBitmap.getHttpBitmap(DataJSONArray.getJSONObject(i).getJSONArray("image").getString(0)),
                    DataJSONArray.getJSONObject(i).getString("pring"),
                    DataJSONArray.getJSONObject(i).getString("material"),
                    DataJSONArray.getJSONObject(i).getString("describe"));
            msgList.add(msg);
        }

        return msgList;
    }
}
