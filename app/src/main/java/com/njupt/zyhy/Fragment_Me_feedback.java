package com.njupt.zyhy;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.njupt.zyhy.unicloud.UnicloudApi;

public class Fragment_Me_feedback extends Activity implements View.OnClickListener {

    private EditText editText;
    private Button button;
    private String edittext;
    private ImageView back;
    private SharedPreferences sp;
    private JSONArray DataJSONArray;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_me_opinion);

        editText = findViewById(R.id.et_advice_content);
        button = findViewById(R.id.put);

        editText.setOnClickListener(this);
        button.setOnClickListener(this);

        back = (ImageView) findViewById(R.id.opinion_back);
        back.setOnClickListener(this);

        // 获取用户缓存信息
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        //创建handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11) {
                    JSONObject DataJSONObject = (JSONObject) msg.obj;
                    DataJSONArray = DataJSONObject.getJSONArray("data");
                    System.out.println(DataJSONArray.toString());

                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {
                //FIXME 这里直接更新ui是不行的
                Message C_message = Message.obtain();
                //还有其他更新ui方式,runOnUiThread()等
                try {
                    C_message.obj = GetData("uni-data-feedback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                C_message.what = 0x11;
                handler.sendMessage(C_message);
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.put:
                edittext = editText.getText().toString();
                if (TextUtils.isEmpty(edittext)) {
                    showToast("反馈内容不能为空!");
                    return;
                }
                else{
                    // 意见反馈
                    try {
                        UnicloudApi.Add_Feedback("uni-data-feedback",sp.getString("token", ""),sp.getString("id", ""),edittext);
                        showToast("您的反馈已收到!");
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("反馈失败！"+e.toString());
                    }
                }
                break;
            case R.id.opinion_back:
                finish();
                break;
            default:
                break;
        }

    }
    private JSONObject GetData(String Table) throws Exception {
        return UnicloudApi.GetData(sp.getString("token",""),Table);
    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}