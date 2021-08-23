package com.njupt.zyhy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.Lostinformation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class Fragment_Me_lost_add  extends Activity implements View.OnClickListener{


    private ImageView back;
    private ImageView camereIv;
    private ImageView photoIv;
    private ImageView lsot_add_pic;
    private EditText Name;
    private EditText address;
    private Bitmap bitmap_upload;
    private Button lost_put;
    private String TAG = "tag";
    //需要的权限数组 读/写/相机
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lost_add_back:
                Intent intent = new Intent(this, Fragment_Me_lost.class);
                startActivity(intent);
                finish();
                break;
            case R.id.lsot_add_pic:
                choose();
                break;
            case R.id.lost_put:
                add_lost();
            default:
                break;
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_lost_add);
        back = (ImageView) findViewById(R.id.lost_add_back);
        back.setOnClickListener(this);
        lsot_add_pic = (ImageView)findViewById(R.id.lsot_add_pic);
        lsot_add_pic.setOnClickListener(this);


        Name = (EditText) findViewById(R.id.lost_name);
        address = (EditText) findViewById(R.id.lost_address);

        camereIv = (ImageView) findViewById(R.id.lsot_add_pic);
        photoIv = (ImageView) findViewById(R.id.lsot_add_pic);

        lost_put = (Button) findViewById(R.id.lost_put);
        lost_put.setOnClickListener(this);

        //跳转相机动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

    }

    private Uri ImageUri;
    public static final int TAKE_PHOTO = 101;
    public static final int TAKE_CAMARA = 100;

    private void choose() {

        ActionSheetDialog dialog = new ActionSheetDialog(this).builder().setTitle("请选择")
                .addSheetItem("相册", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        toPicture();

                    }
                }).addSheetItem("拍照", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        //检查是否已经获得相机的权限
                        if (verifyPermissions(Fragment_Me_lost_add.this, PERMISSIONS_STORAGE[2]) == 0) {
                            Log.i(TAG, "提示是否要授权");
                            ActivityCompat.requestPermissions(Fragment_Me_lost_add.this, PERMISSIONS_STORAGE, 3);
                        } else {
                            //已经有权限
                            toCamera();  //打开相机
                        }
                    }
                });
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(ImageUri));
                        bitmap_upload = bitmap;
                        camereIv.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_CAMARA:
                if (resultCode == RESULT_OK) {
                    try {
                        //将相册的照片显示出来
                        Uri uri_photo = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri_photo));
                        bitmap_upload = bitmap;
                        photoIv.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * 检查是否有对应权限
     *
     * @param activity   上下文
     * @param permission 要检查的权限
     * @return 结果标识
     */
    public int verifyPermissions(Activity activity, java.lang.String permission) {
        int Permission = ActivityCompat.checkSelfPermission(activity, permission);
        if (Permission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "已经同意权限");
            return 1;
        } else {
            Log.i(TAG, "没有同意权限");
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "用户授权");
            toCamera();
        } else {
            Log.i(TAG, "用户未授权");
        }
    }

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_CAMARA);
        Log.i(TAG, "跳转相册成功");
    }

    //跳转相机
    private void toCamera() {
        //创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        } else {
            try {
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //判断SDK版本高低，ImageUri方法不同
        if (Build.VERSION.SDK_INT >= 24) {
            ImageUri = FileProvider.getUriForFile(Fragment_Me_lost_add.this, "com.njupt.zyhy.fileprovider", outputImage);
        } else {
            ImageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    private void add_lost() {
        String NameValue = Name.getText().toString().trim();
        String addressValue = address.getText().toString().trim();
        String bit = GetHttpBitmap.bitmaptoString(bitmap_upload,100);

        if(bit.isEmpty()){
            showToast("请上传图片");
            return;
        }
        InitBmob.Initbmob();

        Lostinformation lostinformation = new Lostinformation();
        if (TextUtils.isEmpty(NameValue) || TextUtils.isEmpty(addressValue) ) {
            showToast("反馈内容不能为空!");
            return;
        }
        else{
            if(bit.isEmpty()){
                showToast("请上传图片");
                return;
            }else{
                lostinformation.setTitle(NameValue);
                lostinformation.setAddress(addressValue);
                lostinformation.setBit(bit);
                lostinformation.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        showToast("提交成功!");
                        finish();
                    } else {
                        showToast("提交失败！");
                    }
                }
            });
        }
        }
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
