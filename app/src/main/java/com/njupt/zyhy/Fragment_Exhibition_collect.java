package com.njupt.zyhy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.njupt.zyhy.bean.ImageUtil;
import com.njupt.zyhy.unicloud.UnicloudApi;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Fragment_Exhibition_collect extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private ImageView back,photoIv,camereIv,add_pic;
    private EditText Description,Source,Ways,Name, address;
    private String TAG = "Exhibition_collect";
    private Bitmap bitmap_upload;
    private SharedPreferences sp;
    private Uri ImageUri;
    public static final int TAKE_PHOTO = 101;
    public static final int TAKE_CAMARA = 100;
    //需要的权限数组 读/写/相机
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exhibition_collect);

        back = (ImageView) findViewById(R.id.collect_back);
        back.setOnClickListener(this);

        add_pic = (ImageView)findViewById(R.id.add_pics);
        add_pic.setOnClickListener(this);

        camereIv = (ImageView) findViewById(R.id.add_pics);
        photoIv = (ImageView) findViewById(R.id.add_pics);

        Description = (EditText) findViewById(R.id.c_miaosu);
        Source = (EditText) findViewById(R.id.c_laiyuan);
        Name = (EditText) findViewById(R.id.c_name);
        Ways = (EditText) findViewById(R.id.c_lainxi);
        address = (EditText) findViewById(R.id.c_address);

        button = (Button) findViewById(R.id.coo_put);
        button.setOnClickListener(this);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_back:
                finish();
                break;
            case R.id.add_pics:
                choose();
                break;
            case R.id.coo_put:
                try {
                    add_collect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private void add_collect() throws Exception {
        String C_Description = Description.getText().toString().trim();
        String C_Source = Source.getText().toString().trim();
        String C_Name = Name.getText().toString().trim();
        String C_Ways = Ways.getText().toString().trim();
        String C_address = address.getText().toString().trim();

        /**
         * 提交登记的文物信息
         */
        if (TextUtils.isEmpty(C_Description) || TextUtils.isEmpty(C_Source) || TextUtils.isEmpty(C_Name) || TextUtils.isEmpty(C_Ways) || TextUtils.isEmpty(C_address)) {
            showToast("内容不能为空!");
            return;
        }
        else if(!isMobileNO(C_Ways)){
            showToast("请输入正确的联系方式！");
            return;
        }
        else{
            String url = UnicloudApi.Uploadfile(sp.getString("token",""), ImageUtil.byte2Base64(ImageUtil.bitmap2Byte(bitmap_upload)));
            //UnicloudApi.Add_Collect("uni-data-collect",sp.getString("token",""),url,C_Description,C_Name,C_Ways,C_Ways);

            if (Integer.parseInt(  UnicloudApi.Add_Collect("uni-data-collect",sp.getString("token",""),url,C_Description,C_Name,C_Ways,C_address)) > 0) {
                showToast("提交成功!");
                finish();
            } else {
                showToast("提交失败！");
            }
        }
    }
    private boolean isMobileNO(String mobiles) {
        String telRegex = "^((1[3,5,7,8][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

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
                        if (verifyPermissions(Fragment_Exhibition_collect.this, PERMISSIONS_STORAGE[2]) == 0) {
                            Log.i(TAG, "提示是否要授权");
                            ActivityCompat.requestPermissions(Fragment_Exhibition_collect.this, PERMISSIONS_STORAGE, 3);
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
            ImageUri = FileProvider.getUriForFile(this, "com.njupt.zyhy.fileprovider", outputImage);
        } else {
            ImageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



}