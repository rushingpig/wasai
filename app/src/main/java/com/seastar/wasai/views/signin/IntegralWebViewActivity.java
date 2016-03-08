package com.seastar.wasai.views.signin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;

/**
 * Created by jamie on 2015/6/24.
 */
public class IntegralWebViewActivity extends BaseActivity{
    private TextView mTitleName;
    private LinearLayout mBack;
    private WebView mWebView;
    private String webUrl ;
    private LoadMessageView mloadView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle != null) {
            webUrl = bundle.getString(MyIntegral.RULE_URL);
            if (TextUtils.isEmpty(webUrl)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.integral_webview_activity);
        initView();

        initData();
    }
    private void initView(){
        mloadView = (LoadMessageView) findViewById(R.id.container_load);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mWebView = (WebView) findViewById(R.id.integral_webview);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initData(){
        mTitleName.setText("积分规则");
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.loadUrl(webUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webSettings.setBlockNetworkImage(false);
                mloadView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void finishActivity() {

    }
}
