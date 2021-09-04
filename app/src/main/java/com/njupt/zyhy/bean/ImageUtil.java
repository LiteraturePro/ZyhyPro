package com.njupt.zyhy.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

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
        if(getBitmapSize(bitmap) >= 8388608 && getBitmapSize(bitmap) <= 16777216){
            //参数2：压缩率，40表示压缩掉60%; 如果不压缩是100，表示压缩率为0
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        }else if(getBitmapSize(bitmap) >= 16777216 && getBitmapSize(bitmap) <= 41943040){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        }else if(getBitmapSize(bitmap) >= 41943040){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos);
        }else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        }
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

}
