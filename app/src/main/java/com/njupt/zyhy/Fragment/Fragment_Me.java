package com.njupt.zyhy.Fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.codbking.view.ItemView;
import com.google.android.material.snackbar.Snackbar;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.njupt.zyhy.Fragment_Me_collection;
import com.njupt.zyhy.Fragment_Me_feedback;
import com.njupt.zyhy.Fragment_Me_lost;
import com.njupt.zyhy.Fragment_Me_news;
import com.njupt.zyhy.Fragment_Me_order;
import com.njupt.zyhy.Fragment_Me_setting;
import com.njupt.zyhy.Fragment_Me_comment;
import com.njupt.zyhy.Fragment_Me_trip;
import com.njupt.zyhy.LoginActivity;
import com.njupt.zyhy.R;
import com.njupt.zyhy.bean.GetHttpBitmap;
import com.njupt.zyhy.bean.ImageUtil;
import com.njupt.zyhy.unicloud.UnicloudApi;
import com.shehuan.niv.NiceImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Fragment_Me extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView textView;
    private NiceImageView imageView;
    private Bitmap bitmap;
    private Bitmap bitmap_upload;
    private String TAG = "tag";
    private SharedPreferences sp;
    private static String path = "/sdcard/myHead/";// sd路径
    //需要的权限数组 读/写/相机
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public Fragment_Me() {
        // Required empty public constructor
    }

    /**
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Me.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Me newInstance(String param1, String param2) {
        Fragment_Me fragment = new Fragment_Me();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //跳转相机动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__me, container, false);
        imageView = (NiceImageView) view.findViewById(R.id.headimg);
        textView = (TextView) view.findViewById(R.id.name);
        String uid = sp.getString("id","");
        imageView.isCircle(true);
        imageView.isCoverSrc(true);


        /**
         * 判定用户登录状态
         */
        if (sp.getString("token","").equals("")) {
            Snackbar.make(container, "尚未登录", Snackbar.LENGTH_LONG).show();

        } else {
            try {
                JSONObject UserInfo = UnicloudApi.GetUserInfo(uid);
                Snackbar.make(container, "欢迎您：" + UserInfo.getString("username"), Snackbar.LENGTH_LONG).show();
                textView.setText(UserInfo.getString("username"));
                String url = UserInfo.getString("avatar");
                //得到可用的图片
                bitmap = GetHttpBitmap.getHttpBitmap(url);
                imageView.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //头像点击事件
        ImageView headimg = (ImageView) getActivity().findViewById(R.id.headimg);
        headimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });

        /**
         * Item列表点击事件
         */
        // 点击设置
        ImageView btn_send = (ImageView) getActivity().findViewById(R.id.iv_setting);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_setting.class);
                startActivity(intent);
            }
        });

        TextView textView_name = (TextView) getActivity().findViewById(R.id.name);
        textView_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sp.getString("token","").equals("")) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    return;
                }
            }
        });
        // 点击评论
        ItemView ItemView = (ItemView) getActivity().findViewById(R.id.item_comment);
        ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_comment.class);
                startActivity(intent);
            }
        });
        // 点击消息
        ItemView ItemView2 = (ItemView) getActivity().findViewById(R.id.item_message);
        ItemView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_news.class);
                startActivity(intent);
            }
        });
        // 点击反馈
        ItemView ItemView3 = (ItemView) getActivity().findViewById(R.id.item_feedback);
        ItemView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_feedback.class);
                startActivity(intent);
            }
        });
        // 点击失物招领
        ItemView ItemView4 = (ItemView) getActivity().findViewById(R.id.item_lost);
        ItemView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_lost.class);
                startActivity(intent);
            }
        });
        // 点击行程
        ItemView ItemView5 = (ItemView) getActivity().findViewById(R.id.item_trip);
        ItemView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_trip.class);
                startActivity(intent);
            }
        });

        // 点击收藏
        ItemView ItemView6 = (ItemView) getActivity().findViewById(R.id.item_collection);
        ItemView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_collection.class);
                startActivity(intent);
            }
        });
        // 点击预约
        ItemView ItemView7 = (ItemView) getActivity().findViewById(R.id.item_order);
        ItemView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Fragment_Me_order.class);
                startActivity(intent);
            }
        });
    }
    private Uri ImageUri;
    public static final int TAKE_PHOTO = 101;
    public static final int TAKE_CAMARA = 100;

    private void choose() {

        ActionSheetDialog dialog = new ActionSheetDialog(getActivity()).builder().setTitle("请选择")
                .addSheetItem("相册", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        toPicture();

                    }
                }).addSheetItem("拍照", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        //检查是否已经获得相机的权限
                        if (verifyPermissions(getActivity(), PERMISSIONS_STORAGE[2]) == 0) {
                            Log.i(TAG, "提示是否要授权");
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 3);
                        } else {
                            //已经有权限
                            toCamera();  //打开相机
                        }
                    }
                });
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(ImageUri));
                        bitmap_upload = bitmap;
                        String url = UnicloudApi.Uploadfile(sp.getString("token",""), ImageUtil.byte2Base64(ImageUtil.bitmap2Byte(bitmap_upload)));
                        if(UnicloudApi.Updateavatar(sp.getString("id",""),url).equals("1")){
                            showToast("更新头像成功");
                        }else{
                            showToast("更新头像失败");
                        }
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case TAKE_CAMARA:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        //将相册的照片显示出来
                        Uri uri_photo = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri_photo));
                        bitmap_upload = bitmap;
                        String url = UnicloudApi.Uploadfile(sp.getString("token",""),ImageUtil.byte2Base64(ImageUtil.bitmap2Byte(bitmap_upload)));
                        if(UnicloudApi.Updateavatar(sp.getString("id",""),url).equals("1")){
                            showToast("更新头像成功");
                        }else{
                            showToast("更新头像失败");
                        }
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
        File outputImage = new File(getActivity().getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
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
            ImageUri = FileProvider.getUriForFile(getActivity(), "com.njupt.zyhy.fileprovider", outputImage);
        } else {
            ImageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}