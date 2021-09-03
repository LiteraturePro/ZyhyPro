package com.njupt.zyhy.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtil {


    /**
     * 将图片转成byte数组
     *
     * @param bitmap 图片
     * @return 图片的字节数组
     */
    public static byte[] bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //把bitmap100%高质量压缩 到 output对象里
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }
    /**
     * 将图片转成byte数组
     *
     * @param imageByte 图片
     * @return Base64 String
     */
    public static String byte2Base64(byte[] imageByte) {
        if(null == imageByte) return null;
        return Base64.encodeToString(imageByte, Base64.DEFAULT);
    }

    /**
     * Base64转Bitmap
     *
     * @param base64 base64数据流
     * @return Bitmap 图片
     */
    private static Bitmap base642Bitmap(String base64) {
        byte[] decode = Base64.decode(base64.split(",")[1],Base64.DEFAULT);
        Bitmap mBitmap = BitmapFactory.decodeByteArray(decode,0,decode.length);
        return mBitmap;
    }


    /**
     * 由本地路径获取图片
     * 再将Bitmap转换成Base64字符串
     * @param bitmap 本地图片路径
     * @return
     */
    public static String Bitmap2Base64(Bitmap bitmap){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //参数2：压缩率，40表示压缩掉60%; 如果不压缩是100，表示压缩率为0
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
