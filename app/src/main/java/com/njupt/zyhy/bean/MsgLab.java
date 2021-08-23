package com.njupt.zyhy.bean;

import java.util.ArrayList;
import java.util.List;
public class MsgLab {

    public static List<Msg> generateMockList(ArrayList<String> C_Name,ArrayList<String> C_Pic1) {
        List<Msg> msgList = new ArrayList<>();
        //利用泛型，以及上一个文件来读取出我们所需要的内容
        for(int i = 0; i< C_Name.size();i++) {
            Msg msg = new Msg(i,
                    GetHttpBitmap.getHttpBitmap(C_Pic1.get(i)),
                    C_Name.get(i));
            msgList.add(msg);
        }

        return msgList;
    }

}
