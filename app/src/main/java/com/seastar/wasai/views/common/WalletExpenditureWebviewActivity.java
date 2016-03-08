package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.seastar.wasai.R;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

/**
 * Created by Jamie on 2015/7/25.
 */
public class WalletExpenditureWebviewActivity extends BaseActivity {
    private NewProgressWebView mWebView = null;
    private SimpleMessageView errorView = null;
    private String urlStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle != null) {
            urlStr = bundle.getString("web_url");
        }
        setContentView(R.layout.activity_taking_out);
        initView();
        initWebView();
    }
    private void initView() {
//        loadMessageView = (LoadMessageView) findViewById(R.id.activity_container_load);
        mWebView = (NewProgressWebView) findViewById(R.id.activity_webview);
        mWebView.showmLoadMessageView();//针对没有重定向，显示奶瓶加载等待页面，需要关闭
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    finish();
                }
            }
        });

        errorView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
//                getActDetail();
            }
        });
    }
    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.loadUrl(urlStr);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.getSettings().setBlockNetworkImage(false);
                mWebView.dismissLoadMessageView();//隐藏奶瓶等待加载页面
            }
        });
    }


    @Override
    public void finishActivity() {

    }
}
