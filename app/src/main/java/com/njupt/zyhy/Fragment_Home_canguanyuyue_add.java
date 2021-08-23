package com.njupt.zyhy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.njupt.zyhy.bean.InitBmob;
import com.njupt.zyhy.bean.Order;
import com.njupt.zyhy.bean.RegisterUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Fragment_Home_canguanyuyue_add extends Activity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private static String TipInfo="";// 记录错误信息
    private Spinner sp = null;
    private Button btn_yes;
    private Calendar cal;
    private EditText etUserName,etPassWord;
    private String etusername,IDStr,zjlx = "身份证",TIME = null;
    private TextView t_time;
    private int year,month,day;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_home_canguanyuyue_add);
        btn_yes = (Button)findViewById(R.id.btn_yes);
        etUserName =(EditText)findViewById(R.id.et_username);
        etPassWord =(EditText)findViewById(R.id.et_password);
        sp = (Spinner)findViewById(R.id.spin);
        String[] arr = {"身份证","护照","港澳居民来往内地通行证","台湾居民来往内地通行证"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,arr);
        //设置Spinner Adapter
        sp.setAdapter(adapter);
        //事件监听
        sp.setOnItemSelectedListener(this);
        btn_yes.setOnClickListener(this::onClick);
        etPassWord.setOnClickListener(this);
        etUserName.setOnClickListener(this);
        getDate();
        t_time=(TextView) findViewById(R.id.t_time);
        t_time.setOnClickListener(this);


    }
    private void getDate() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        switch (parent.getId()){
            case R.id.spin:
                zjlx = content;
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_yes:
                InitBmob.Initbmob();
                Order order = new Order();
                etusername = etUserName.getText().toString();
                IDStr = etPassWord.getText().toString();
                if (TextUtils.isEmpty(etusername) || TextUtils.isEmpty(IDStr) ) {
                    showToast("姓名或身份证不能为空");
                    return;
                }
                else if(isNumeric(etusername)){
                    showToast("姓名格式错误");
                    return;
                }
                else if(!isIdCard(IDStr)){
                    showToast(TipInfo);
                    return;
                }
                else if (TextUtils.isEmpty(TIME)) {
                    showToast("请选择时间");
                    return;
                }else{

                    RegisterUser user = BmobUser.getCurrentUser(RegisterUser.class);
                    order.setId(user.getObjectId());
                    order.setName(etusername);
                    order.setNumber(IDStr);
                    order.setTime(TIME);
                    order.setCertificates(zjlx);
                    order.save(new SaveListener<String>() {
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
                break;
            case R.id.t_time:
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        t_time.setText(year+"-"+(++month)+"-"+day);
                        t_time.setTextSize(25);//将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                        TIME= t_time.getText().toString();
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(Fragment_Home_canguanyuyue_add.this, DatePickerDialog.THEME_HOLO_LIGHT,listener,year,month,day);//主题在这里！后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
                break;
            default:
                break;
        }
    }
    /**
     * 验证身份证号
     */
    public static boolean isIdCard(String IDStr) {

        String Ai = "";
        // 判断号码的长度 15位或18位
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            TipInfo = "身份证号码长度应该为15位或18位";
            return false;
        }


        // 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            TipInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字";
            return false;
        }


        // 判断出生年月是否有效
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 日期
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            TipInfo = "身份证出生日期无效";
            return false;
        }



        // 判断地区码是否有效
        Hashtable areacode = GetAreaCode();
        //如果身份证前两位的地区码不在Hashtable，则地区码有误
        if (areacode.get(Ai.substring(0, 2)) == null) {
            TipInfo = "身份证地区编码错误";
            return false;
        }

        if (isVarifyCode(Ai, IDStr) == false) {
            TipInfo = "身份证校验码无效，不是合法的身份证号码";
            return false;
        }

        return true;
    }


    /*
     * 判断第18位校验码是否正确
    * 第18位校验码的计算方式：
       　　1. 对前17位数字本体码加权求和
       　　公式为：S = Sum(Ai * Wi), i = 0, ... , 16
       　　其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
       　　2. 用11对计算结果取模
       　　Y = mod(S, 11)
       　　3. 根据模的值得到对应的校验码
       　　对应关系为：
       　　 Y值：     0  1  2  3  4  5  6  7  8  9  10
       　　校验码： 1  0  X  9  8  7  6  5  4  3   2
    */
    private static boolean isVarifyCode(String Ai, String IDStr) {
        String[] VarifyCode = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = sum % 11;
        String strVerifyCode = VarifyCode[modValue];
        Ai = Ai + strVerifyCode;
        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                return false;

            }
        }
        return true;
    }
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
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

