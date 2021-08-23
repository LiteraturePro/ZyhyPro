package com.njupt.zyhy.bean;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.njupt.zyhy.R;

public class ConfirmDialogQuit extends Dialog {
    private Context context;
    private TextView titleTv,contentTv;
    private View okBtn,cancelBtn;
    private OnDialogClickListener dialogClickListener;

    public ConfirmDialogQuit(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    //初始化View
    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirm_dialog_quit, null);
        setContentView(view);
        initWindow();

        titleTv = (TextView) findViewById(R.id.title_name);
        contentTv = (TextView) findViewById(R.id.text_view);
        okBtn = findViewById(R.id.btn_ok);
        cancelBtn = findViewById(R.id.btn_cancel);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if(dialogClickListener != null){
                    try {
                        dialogClickListener.onOKClick();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if(dialogClickListener != null){
                    dialogClickListener.onCancelClick();
                }
            }
        });
    }

    /**
     *添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.8); //宽度为屏幕80%
        lp.gravity = Gravity.CENTER;  //中央居中
        dialogWindow.setAttributes(lp);
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener){
        dialogClickListener = clickListener;
    }

    /**
     *添加按钮点击事件
     */
    public interface OnDialogClickListener{
        void onOKClick() throws Exception;
        void onCancelClick();
    }
}
