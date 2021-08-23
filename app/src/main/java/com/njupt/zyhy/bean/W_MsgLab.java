package com.njupt.zyhy.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class W_MsgLab {

    public static List<W_Msg> generateMockList(ArrayList<String> C_Name,ArrayList<String> C_Pic1,ArrayList<String> C_P,ArrayList<String> C_m,ArrayList<String> C_g) {
        List<W_Msg> msgList = new ArrayList<>();
        //利用泛型，以及上一个文件来读取出我们所需要的内容
        for(int i = 0; i< C_Name.size();i++) {
            W_Msg msg = new W_Msg(i,
                    C_Name.get(i),
                    getHttpBitmap(C_Pic1.get(i)),
                    C_P.get(i),
                    C_m.get(i),
                    C_g.get(i));
            msgList.add(msg);
        }

        return msgList;
    }
    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;

    }

}
