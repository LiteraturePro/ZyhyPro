package com.njupt.zyhy.unicloud;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.HttpGet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;

public class UnicloudApi {
    private static final String CONTENT_TYPE_TAG = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String UTF8 = "UTF-8";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";

    private static final String URL_Main = "https://4a2224fd-4003-4b8c-b165-abef8c34b228.bspapp.com";

    private static final String URL_Register = URL_Main + "/http/user_register?";
    private static final String URL_Login = URL_Main + "/http/user_login?";
    private static final String URL_Logout = URL_Main + "/http/user_logout?";
    private static final String URL_ResetPwd = URL_Main + "/http/resetPwd?";

    private static final String URL_data_Add = URL_Main + "/http/adddata?";
    private static final String URL_data_Delete = URL_Main + "/http/dedata?";
    private static final String URL_data_Get = URL_Main + "/http/getdata?";
    private static final String URL_data_Getbycondition = URL_Main + "/http/getdatabycondition?";
    private static final String URL_data_Update = URL_Main + "/http/updata?";

    private static final String URL_file_Upload = URL_Main + "/http/uploadfile?";

    private static final String URL_GetUserInfoByToken = URL_Main + "/http/getUserInfoByToken?";
    private static final String URL_GetUserInfo = URL_Main + "/http/getUserInfo?";
    private static final String URL_GetAppVersion = URL_Main + "/http/getAppVersion?";
    private static final String URL_Updateavatar = URL_Main + "/http/updateavatar?";

    /**
     * 服务唤醒
     * 由于后端服务是以云函数的形式处理数据，如果第一次没有唤醒服务，将获取不到数据，这也是用云函数做后端的一个弊端
     */
    public static void wakeup() throws Exception {
        URL_Post(URL_Register);
        URL_Post(URL_Login);
        URL_Post(URL_Logout);
        URL_Post(URL_ResetPwd);
        URL_Post(URL_data_Add);
        URL_Post(URL_data_Delete);
        URL_Post(URL_data_Get);
        URL_Post(URL_data_Getbycondition);
        URL_Post(URL_data_Update);
        URL_Post(URL_file_Upload);
        URL_Post(URL_GetUserInfoByToken);
        URL_Post(URL_GetUserInfo);
        URL_Post(URL_GetAppVersion);
        URL_Post(URL_Updateavatar);
    }

    /**
     * 用户注册
     * @param Username 用户名
     * @param Password 密码
     * @param number 手机号
     * @return String格式结果
     */
    public static String Register(String Username, String Password ,String number) throws Exception {
        String Url = URL_Register +"username="+Username+"&password="+Password+"&number="+number;
        JSONObject Json = URL_Post(Url);
        //获取当前嵌套下的属性
        String code = Json.getString("code");
        if (code.equals("0")){
            //code = 0 注册成功,
            Log.d("Sngin",Json.getString("msg"));
            return Json.getString("msg");

        }else if(code.equals("20102")){
            //用户名存在，手机号存在
            return Json.getString("msg");
        }else{
            return "注册失败！";
        }

    }
    /**
     * 用户注销
     * @param token
     * @return String格式结果
     */
    public static String Logout(String token) throws Exception {
        String Url = URL_Logout +"token="+token;
        JSONObject Json = URL_Post(Url);

        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }
    /**
     * 用户登录
     * @param Username 用户名
     * @param Password 密码
     * @return String格式结果
     */
    public static String Login(String Username, String Password) throws Exception {
        String Url = URL_Login +"username="+Username+"&password="+Password;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code.equals("0")){
            //code = 0 登陆成功,
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }

    /**
     * 用户登录
     * @param Username 用户名
     * @param Password 密码
     * @return JSON格式结果
     */
    public static JSONObject LoginJson(String Username, String Password) throws Exception {
        String Url = URL_Login +"username="+Username+"&password="+Password;
        JSONObject Json = URL_Post(Url);
        // JSON格式数据解析对象
        JSONObject jo = new JSONObject();
        String code = Json.getString("code");
        if (code.equals("0")){
            //code = 0 登陆成功,
            Log.d("Login",Json.getString("token"));
            // 构造Json数据，包括一个map和一个含Employee对象的Json数据
            jo.put("code", Json.getString("code"));
            jo.put("uid", Json.getString("uid"));
            jo.put("token",Json.getString("token"));
            jo.put("tokenExpired",Json.getString("tokenExpired"));
            return jo;
        }else {
            jo.put("code", Json.getString("code"));
            jo.put("msg", Json.getString("msg"));
            return jo;
        }

    }

    /**
     * 添加数据
     * @param Token 验证码
     * @param Table 表名
     * @param Params 字段串
     * @return JSON格式结果
     */
    public static String Adddata(String Token, String Table ,String Params) throws Exception {
        String Url = URL_data_Add +"token="+Token+"&DbName="+Table + Params;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }
    /**
     * 删除数据
     * @param Token 验证码
     * @param Table 表名
     * @param _id 数据id
     * @return JSON格式结果
     */
    public static String DeleteData(String Token, String Table,String _id) throws Exception {
        String Url = URL_data_Delete +"token="+Token+"&DbName="+Table +"&_id="+_id;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }
    /**
     * 获取数据
     * @param Token 验证码
     * @param Table 表名
     * @return JSON格式结果
     */
    public static JSONObject GetData(String Token, String Table) throws Exception {
        String Url = URL_data_Get +"token="+Token+"&DbName="+Table;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("affectedDocs");
        if (Integer.parseInt(code) > 0){
            return Json;
        }else {
            return Json;
        }

    }

    /**
     * 获取数据
     * @param Token 验证码
     * @param Table 表名
     * @param condition
     * @return JSON格式结果
     */
    public static JSONObject GetDatabycondition(String Token, String Table ,String condition) throws Exception {
        String Url = URL_data_Getbycondition +"token="+Token+"&DbName="+Table +"&condition="+condition;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("affectedDocs");
        if (Integer.parseInt(code) > 0){
            return Json;
        }else {
            return Json;
        }

    }

    /**
     * 获取数据
     * @param Token 验证码
     * @param Table 表名
     * @return JSON格式结果
     */
    public static JSONObject GetLostData(String Token, String Table) throws Exception {
        String Url = URL_data_Get +"token="+Token+"&DbName="+Table;
        JSONObject Json = URL_Post(Url);
        return Json;
    }
    /**
     * 更新数据
     * @param Token 验证码
     * @param Table 表名
     * @param Params 字段串
     * @return JSON格式结果
     */
    public static String UpdateData(String Token, String Table, String Params) throws Exception {
        String Url = URL_data_Update +"token="+Token+"&DbName="+Table + Params;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }

    /**
     * 上传图片
     * @param Token 验证码
     * @param base64 base64编码
     * @return JSON格式结果
     */
    public static String Uploadfile(String Token, String base64) throws Exception {
        Object Json = setImgByStr(Token,base64,URL_file_Upload);
        JSONObject jsonObject = JSONObject.parseObject(Json.toString());
        return jsonObject.getString("fileID");

    }
    /**
     * 根据token获取用户信息
     * @param Token 验证码
     * @return JSON格式结果
     */
    public static JSONObject GetUserInfoByToken(String Token) throws Exception {
        String Url = URL_GetUserInfoByToken +"token="+Token;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json;
        }else {
            return Json;
        }
    }

    /**
     * 根据Uid获取用户信息
     * @param uid 用户唯一标识码
     * @return JSON格式结果
     */
    public static JSONObject GetUserInfo(String uid) throws Exception {
        String Url = URL_GetUserInfo +"uid="+uid;
        return URL_Post(Url);
    }

    /**
     * 更改密码
     * @param tag 标记
     * @param uidorusername 用户唯一标识码
     * @param password 新密码
     * @return JSON格式结果
     */
    public static String resetPwd(String tag, String uidorusername, String password) throws Exception {
        String Url = null;
        if (tag.equals("0")){
            Url = URL_ResetPwd +"tag="+tag+"&username="+uidorusername+"&password"+password;
        }else{
            Url = URL_ResetPwd +"tag="+tag+"&uid="+uidorusername+"&password"+password;
        }
        JSONObject Json = URL_Post(Url);
        return Json.getString("msg");
    }


    /**
     * 添加反馈数据
     * @param tag 接口标记
     * @param Token 验证码
     * @param id 用户标识
     * @param feedback 字段串
     * @return JSON格式结果
     */
    public static String Add_Feedback(String tag, String Token, String id ,String feedback) throws Exception {
        String Url = URL_data_Add +"tag="+tag+"&token="+Token+"&user_id="+id+"&message="+feedback;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("msg");
        }else {
            return Json.getString("msg");
        }

    }

    /**
     * 添加文物登记数据
     * @param tag 接口标记
     * @param Token 验证码
     * @param image
     * @param describe
     * @param user_name
     * @param mobile
     * @param address
     * @return JSON格式结果
     */
    public static String Add_Collect(String tag, String Token,String image, String describe,String user_name,String mobile ,String address) throws Exception {
        String Url = URL_data_Add +"tag="+tag+"&token="+Token+"&image="+image+"&describe="+describe+"&user="+user_name+"&mobile="+mobile+"&address="+address;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("inserted");
        }else {
            return Json.getString("inserted");
        }

    }

    /**
     * 添加失误登记数据
     * @param tag 接口标记
     * @param Token 验证码
     * @param image
     * @param title
     * @param user_id
     * @param address
     * @return JSON格式结果
     */
    public static String Add_Lost(String tag, String Token,String image, String title,String user_id, String address) throws Exception {
        String Url = URL_data_Add +"tag="+tag+"&token="+Token+"&image="+image+"&title="+title+"&user_id="+user_id+"&address="+address;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("inserted");
        }else {
            return Json.getString("inserted");
        }

    }

    /**
     * 添加预约登记数据
     * @param tag 接口标记
     * @param Token 验证码
     * @param name
     * @param diagTime
     * @param user_id
     * @param classs
     * @param idcard
     * @param time_interval
     * @return JSON格式结果
     */
    public static String Add_Order(String tag, String Token, String user_id, String name, String diagTime, String classs ,String idcard, String time_interval) throws Exception {
        String Url = URL_data_Add +"tag="+tag+"&token="+Token+"&id="+user_id+"&name="+name+"&diagTime="+diagTime+"&classs="+classs+"&idcard="+idcard+"&time_interval="+time_interval;
        JSONObject Json = URL_Post(Url);
        String code = Json.getString("code");
        if (code != "0"){
            return Json.getString("inserted");
        }else {
            return Json.getString("inserted");
        }

    }


    /**
     * 获取版本号
     * @return JSON格式结果
     */
    public static String GetAppVersion() throws Exception {
        String Url = URL_GetAppVersion;
        JSONObject Json = URL_Post(Url);
        return Json.getString("Version");
    }
    /**
     * @param uid 用户唯一标识码
     * @param imagesrc 新头像地址
     * @return String格式结果
     */
    public static String Updateavatar(String uid, String imagesrc) throws Exception {
        String Url = URL_Updateavatar+"uid="+uid+"&imagesrc="+imagesrc;
        JSONObject Json = URL_Post(Url);
        return Json.getString("updated");
    }

    private static JSONObject streamToJson(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,UTF8));
        String temp = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        JSONObject json = JSON.parseObject(stringBuilder.toString().trim());
        return json;
    }
    private static JSONObject URL_Post(String Url) throws Exception {
        URL url = new URL(Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(METHOD_POST);
        conn.setConnectTimeout(5000);
        // 处理请求的数据
        InputStream inputStream = conn.getInputStream();
        JSONObject json = streamToJson(inputStream); // 从响应流中提取 JSON
        return json;
    }
    public static String getContent(String url) throws Exception {

        StringBuilder sb = new StringBuilder();

        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        // 设置网络超时参数
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);

        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    entity.getContent(), "UTF-8"), 8192);

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();

        }

        return sb.toString();
    }
    public static HttpResponse post(Map<String, Object> params, String url) {

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("charset", "UTF-8");
        httpPost.setHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8");
        HttpResponse response = null;
        if (params != null && params.size() > 0) {
            String string = JSON.toJSONString(params);
            StringEntity entity = new StringEntity(string,ContentType.APPLICATION_JSON);
            try {
                httpPost.setEntity(entity);
                response = client.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response = client.execute(httpPost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;

    }
    public static Object getValues(Map<String, Object> params, String url) {
        String token = "";
        HttpResponse response = post(params, url);
        if (response != null) {
            try {
                token = EntityUtils.toString(response.getEntity());
                response.removeHeaders("operator");
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token;
    }
    public static Object setImgByStr(String token, String img,String url ){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("buffer", img);
        params.put("token", token);
        return getValues(params, url);
    }
}
