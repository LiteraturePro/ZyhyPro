package com.njupt.zyhy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.njupt.zyhy.bean.ImageUtil;
import com.njupt.zyhy.unicloud.UnicloudApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


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
    private SharedPreferences sp;
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
                try {
                    add_lost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

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


    private void add_lost() throws Exception {
        String NameValue = Name.getText().toString().trim();
        String addressValue = address.getText().toString().trim();
        String base64 = ImageUtil.Bitmap2Base64(bitmap_upload);

        String url = UnicloudApi.Uploadfile(sp.getString("token",""),base64);

        if(url.isEmpty()){
            showToast("请上传图片");
            return;
        }

        if (TextUtils.isEmpty(NameValue) || TextUtils.isEmpty(addressValue) ) {
            showToast("反馈内容不能为空!");
            return;
        }
        else{
            if(url.isEmpty()){
                showToast("请上传图片");
                return;
            }else{
                //上传意见反馈
                if (Integer.parseInt(  UnicloudApi.Add_Lost("uni-data-lost",sp.getString("token",""),url,NameValue,sp.getString("id",""),addressValue)) > 0) {
                    showToast("提交成功!");
                    finish();
                } else {
                    showToast("提交失败！");
                }

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
