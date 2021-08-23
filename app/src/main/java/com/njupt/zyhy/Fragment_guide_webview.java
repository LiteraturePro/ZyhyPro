package com.njupt.zyhy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Fragment_guide_webview extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置去除ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_guide_webview);
        init();
    }
    public void init() {
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
        webView.loadUrl("http://dctt.gog.cn/360/20160805/");

    }

    /**
     * 防止返回到之前的 Activity
     *
     * @param keyCode 按键
     * @param event 事件
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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