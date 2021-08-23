package com.njupt.zyhy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.lzj.gallery.library.views.BannerViewPager;
import com.njupt.zyhy.bean.GetHttpBitmap;
import java.util.ArrayList;
import java.util.List;

public class Fragment_exhabition_detail extends Activity implements View.OnClickListener{

    private ImageView back;
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView1;
    private BannerViewPager banner;
    private List<String> urlList;
    private WebView webView;

    private int maxDescripLine = 6; //TextView默认最大展示行数
    private View layoutView ,expandView; //LinearLayout布局和ImageView


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**加载xml文件*/
        setContentView(R.layout.fragment_exhabition_detail);
        /**
         * 获取数据
         */
        Intent intent=getIntent();
        String Z_Title=intent.getStringExtra("Z_Title");
        String Z_Text=intent.getStringExtra("Z_Text");
        String Z_Pic1=intent.getStringExtra("Z_Pic1");
        String Z_Pic2=intent.getStringExtra("Z_Pic2");
        String Z_Pic3=intent.getStringExtra("Z_Pic3");
        String Z_Pic4=intent.getStringExtra("Z_Pic4");

        back = (ImageView) findViewById(R.id.zhanlan_back);
        back.setOnClickListener(this);

        textView1 =(TextView) findViewById(R.id.text_zhanlan_1);
        textView1.setText(Z_Title);

        expandView = findViewById(R.id.expand_view);
        layoutView = findViewById(R.id.description_layout);
        textView2 =(TextView) findViewById(R.id.text_zhanlan_2);
        textView2.setText("\u3000\u3000"+Z_Text);
        //descriptionView设置默认显示高度
        textView2.setHeight(textView2.getLineHeight() * maxDescripLine);
        //根据高度来判断是否需要再点击展开
        textView2.post(new Runnable() {
            @Override
            public void run() {
                expandView.setVisibility(textView2.getLineCount() > maxDescripLine ? View.VISIBLE : View.GONE);
            }
        });

        imageView1 =(ImageView) findViewById(R.id.image_zhanlan);

        imageView1.setImageBitmap(GetHttpBitmap.getHttpBitmap(Z_Pic1));

        banner = (BannerViewPager) findViewById(R.id.banner);

        urlList = new ArrayList<>();
        urlList.add(Z_Pic2);
        urlList.add(Z_Pic3);
        urlList.add(Z_Pic4);
        //轮播图加载
        banner.initBanner(urlList, true)//关闭3D画廊效果
                .addPageMargin(10, 20)//无间距
                .addPointMargin(6)//添加指示器
                .addStartTimer(2)//自动轮播5秒间隔
                .addPointBottom(7)
                .finishConfig()//这句必须加
                .addBannerListener(new BannerViewPager.OnClickBannerListener() {
                    @Override
                    public void onBannerClick(int position) {
                        //点击item
                        Log.i("test","--------------00x3");
                    }
                });
        // 文字折叠
        layoutView.setOnClickListener(new View.OnClickListener() {
            boolean isExpand;//是否已展开的状态

            @Override
            public void onClick(View v) {
                isExpand = !isExpand;
                textView2.clearAnimation();//清楚动画效果
                final int deltaValue;//默认高度，即前边由maxLine确定的高度
                final int startValue = textView2.getHeight();//起始高度
                int durationMillis = 350;//动画持续时间
                if (isExpand) {
                    /**
                     * 折叠动画
                     * 从实际高度缩回起始高度
                     */
                    deltaValue = textView2.getLineHeight() * textView2.getLineCount() - startValue;
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    expandView.startAnimation(animation);
                } else {
                    /**
                     * 展开动画
                     * 从起始高度增长至实际高度
                     */
                    deltaValue = textView2.getLineHeight() * maxDescripLine - startValue;
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    expandView.startAnimation(animation);
                }
                Animation animation = new Animation() {
                    protected void applyTransformation(float interpolatedTime, Transformation t) { //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
                        textView2.setHeight((int) (startValue + deltaValue * interpolatedTime));
                    }
                };
                animation.setDuration(durationMillis);
                textView2.startAnimation(animation);
            }
        });

        webView = (WebView) findViewById(R.id.iv_webview);

        WebSettings webSettings= webView.getSettings();
        //支持js
        webSettings.setJavaScriptEnabled(true);
        //缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //自适应页面
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //支持缩放
        webSettings.setSupportZoom(true);
        //关闭缩放按钮
        webSettings.setBuiltInZoomControls(false);
        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);
        //启用h5缓存 （webview存在两种缓存：网页数据缓存（存储打开过的页面及资源）、H5缓存（即appcache））
        webSettings.setAppCacheEnabled(true);
        //h5缓存大小
        //webSettings.setAppCacheMaxSize(size);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        //h5缓存路径
        webSettings.setAppCachePath(appCachePath);
        //​设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置开启Dom Storage机制
        webSettings.setDomStorageEnabled(true);
        // 防止所有后打开默认浏览器
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.loadUrl("https://www.wxiou.cn/zyhy.html");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zhanlan_back:
                finish();
                break;
            default:
                break;
        }

    }
    static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());

            return super.shouldOverrideUrlLoading(view, request);
        }
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //弹出提示框，让用户决定是否继续加载
            //1.用户选择继续加载
            handler.proceed();
            //2.用户取消
            handler.cancel();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("WebViewClient", "Page started...");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("WebViewClient", "Page Finished...");
        }
    }

    /**
     * 添加所有记录和 Title
     */
    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }

}