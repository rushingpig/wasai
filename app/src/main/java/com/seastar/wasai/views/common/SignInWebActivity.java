package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.Activity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;
import com.seastar.wasai.views.login.LoginActivity;
import com.seastar.wasai.views.plugin.SharePlugin;
import com.seastar.wasai.views.signin.MyIntegral;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * 签到的web view
 */
@SuppressLint({"SetJavaScriptEnabled", "InflateParams"})
public class SignInWebActivity extends BaseActivity implements OnClickListener {
//    private LoadMessageView loadMessageView = null;
    private NewProgressWebView mWebView = null;
    private SimpleMessageView errorView = null;
    private GuideActionCompactCounterView shareCompactCounterView = null;
    private static SharePlugin sharePlugin;
    private Activity act;
    private long lastClickTime;
    private Handler mMainHandler = null;
    private static final int MSG_SHARE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        sharePlugin = new SharePlugin(this, this.findViewById(R.id.activity_webview));
        sharePlugin.init();
        Bundle bundle = getIntent().getExtras();
        long actId = bundle.getLong("actId");
        String pageTitle = bundle.getString("pageTitle");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(pageTitle);
        act = new Activity();
        act.setActivityId(actId);

        initView();
        initWebView();
        initData();
        setListener();

        getActDetail();
        mMainHandler = new HanlderImp(this);

//        mMainHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//                super.handleMessage(msg);
//            }
//        };
    }
    static class HanlderImp extends Handler{
        WeakReference<android.app.Activity> mActivityReference;

        HanlderImp(android.app.Activity activity) {
            mActivityReference= new WeakReference<android.app.Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final android.app.Activity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_SHARE:
                        sharePlugin.showPop();
                        break;
                }
            }
            super.handleMessage(msg);

        }
    }


    private void initView() {
//        loadMessageView = (LoadMessageView) findViewById(R.id.activity_container_load);
        mWebView = (NewProgressWebView) findViewById(R.id.activity_webview);
        mWebView.showmLoadMessageView();//针对没有重定向，显示奶瓶加载等待页面，需要关闭
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        findViewById(R.id.action_back).setOnClickListener(this);
        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        shareCompactCounterView.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setBlockNetworkImage(true);
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
        mWebView.addJavascriptInterface(new JavaScriptMethods(), "nativeObj");
    }


    private void initData() {

    }

    private void setListener() {
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getActDetail();
            }
        });
        shareCompactCounterView.setOnClickListener(this);
    }

    @Override
    public void finishActivity() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_back:
                if(mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    finish();
                }
                break;
            case R.id.article_action_compact_share:
                sharePlugin.showPop();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharePlugin.dealResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish(){
        sharePlugin.closePop();
        super.finish();
    }

    private void getActDetail() {
        String url = InterfaceConstant.ACTIVITY_DETAIL + "/" + this.act.getActivityId();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Activity act = gson.fromJson(dataJsonStr, Activity.class);
                    SignInWebActivity.this.act = act;
                    sharePlugin.setShareObject(act);
                    mWebView.loadUrl(act.getDetailUrl());
//                    loadMessageView.setVisibility(View.GONE);
                    Log.d(TAG, "获取活动详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if(mWebView.canGoBack()){
                mWebView.goBack();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class JavaScriptMethods{
        @JavascriptInterface
        public void goToExchange() {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Intent intent = new Intent(SignInWebActivity.this, MyIntegral.class);
                Bundle bundle = new Bundle();
                bundle.putLong("activityId",act.getActivityId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void goToShare() {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Message message = new Message();
                message.what = MSG_SHARE;
                mMainHandler.sendMessage(message);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void login() {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Context context = SignInWebActivity.this;
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void forwardToProduct(long itemId) {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Item product = new Item();
                product.setItemId(itemId);
                CommonUtil.forwardToDetailPage(SignInWebActivity.this, product);
            }
            lastClickTime = currentTime;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
