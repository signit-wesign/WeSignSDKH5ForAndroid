package cn.signit.www.wesignsdkdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;


public class WebViewActivity extends AppCompatActivity {

    public String url;
    private String blackUrl = "about:blank";//浏览器空白页
    private HideBarTimeTask hideBarTimeTask;            //隐藏隐藏加载状态进度条Task
    private Timer hideProgressBarTimer;                //隐藏加载状态进度条计时器
    private WebView web;
    private WSNoNetworkStatusView noNetworkStatusView; //网络标记
    private ProgressBar progressbar;


    protected void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        //加载webview
        web = (WebView) findViewById(R.id.webview);
        WebSettings setting = web.getSettings();
        setting.setSupportZoom(true);           //设置支持缩放
        setting.setUseWideViewPort(true);       //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        setting.setBuiltInZoomControls(true);
        setting.setLoadWithOverviewMode(true); //设置网页缩放至手机大小
        setting.setJavaScriptEnabled(true); //JavaScript开启
        setting.setDefaultTextEncodingName("utf-8"); //设置默认字符集

        setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); //允许HTTP链接

        //开启LocalStorage存储支持
        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 8); //8MB
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        web.getSettings().setAppCachePath(appCachePath);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setAppCacheEnabled(true);

        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.setWebContentsDebuggingEnabled(true); //允许调试
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.loadUrl(url);
        web.setWebViewClient(new WSWebViewClient());
        web.setDownloadListener(new DownloadListener());

        //设置进度条，网络状态指示
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        noNetworkStatusView = (WSNoNetworkStatusView) findViewById(R.id.nonetworkview);
        noNetworkStatusView.setVisibility(View.GONE);
        noNetworkStatusView.setRefreshListener(new WSNoNetworkStatusView.HtmlReloadListener() {
            @Override
            public void triggerRefresh() {
                if (netIsAvailable()) {
                    noNetworkStatusView.setVisibility(View.GONE);
                    web.setVisibility(View.VISIBLE);
                }
                loadWebView();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    protected void loadWebView() {
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
        web.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        loadWebView();
        Log.i("onCreate", "called onCreate ");

    }

    public boolean netIsAvailable() {
        //检测网络是否可用
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cwjManager.getActiveNetworkInfo() != null && cwjManager.getActiveNetworkInfo().isAvailable();
    }

    private void runTimer(int delay) {
        stopTimer();
        hideProgressBarTimer = new Timer(true);
        hideBarTimeTask = new HideBarTimeTask();
        hideProgressBarTimer.schedule(hideBarTimeTask, delay);
    }

    private void stopTimer() {
        if (hideBarTimeTask != null) {
            hideBarTimeTask.cancel();
            hideBarTimeTask = null;
        }

        if (hideProgressBarTimer != null) {
            hideProgressBarTimer.cancel();
            hideProgressBarTimer.purge();
            hideProgressBarTimer = null;
        }
    }

    private WSProgressBarController progressBarController = new WSProgressBarController(new WSProgressBarController.ControllerListener() {

        @Override
        public void stop() {
            runTimer(500);
        }

        @Override
        public void setProgress(int progress) {
            progressbar.setProgress(progress);
        }

        @Override
        public void start() {
            if (progressbar.getVisibility() == View.GONE) {
                progressbar.setVisibility(View.VISIBLE);
            }
            stopTimer();
        }

    });


    class HideBarTimeTask extends TimerTask {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 10000;
            webviewHandler.sendMessage(msg);
        }
    }

    private Handler webviewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10000) {
                progressbar.setVisibility(View.GONE);
                progressbar.setProgress(0);
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        Log.i("onConfigureationChanged", "onConfigurationChanged called");
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Nothing need to be done here

        } else {
            // Nothing need to be done here
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnHome(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void returnHome(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (web != null && web.canGoBack() && checkBackUrl(blackUrl)) {
                web.goBack();
            } else {
                web.stopLoading();
                finish();
            }

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean checkBackUrl(String url) {
        WebBackForwardList mWebBackForwardList = web.copyBackForwardList();
        String backUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
        //判断是否是空白页
        if (backUrl != null && backUrl.equalsIgnoreCase(url)) {
            return false;
        }
        return true;
    }

    private class WSWebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //开始加载，显示进度
            progressBarController.preloading();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (failingUrl.equalsIgnoreCase(url) == false) {
                return;
            }

            web.loadUrl(blackUrl);
            //只有加载完毕才应该调用clearHistory()
            web.postDelayed(new Runnable() {
                @Override
                public void run() {
                    web.clearHistory();
                    noNetworkStatusView.setVisibility(View.VISIBLE);
                    web.setVisibility(View.GONE);
                }
            }, 500);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //忽略SSL证书错误检测,使用SslErrorHandler.proceed()来继续加载
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //通知程序当前页面加载进度
            progressBarController.setCurrentValue(newProgress);
        }
    }

    private class DownloadListener implements android.webkit.DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            //需要Webview开启下载监听,否则点击下载连接，没有反应
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}

