package com.njupt.zyhy;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.njupt.zyhy.bean.Feedbacks;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Fragment_Me_feedback extends Activity implements View.OnClickListener {

    private EditText editText;
    private Button button;
    private String edittext;
    private ImageView back;

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

    }
    /**
     * @version 1.0
     * @param msg 打印信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.put:

                edittext = editText.getText().toString();
                Feedbacks feedbacks = new Feedbacks();
                if (TextUtils.isEmpty(edittext)) {
                    showToast("反馈内容不能为空!");
                    return;
                }
                else{
                    feedbacks.setMessages(edittext);
                    feedbacks.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                showToast("您的反馈已收到!");
                                finish();
                            } else {
                                showToast("反馈失败！");
                            }
                        }
                    });
                }
                break;
            case R.id.opinion_back:
                finish();
                break;
            default:
                break;
        }

    }

}