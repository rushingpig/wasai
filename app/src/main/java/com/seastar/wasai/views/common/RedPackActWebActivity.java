package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.Activity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.Encrypt;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.MainActivity;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;
import com.seastar.wasai.views.order.OrdersActivity;
import com.seastar.wasai.views.plugin.SharePlugin;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


@SuppressLint({"SetJavaScriptEnabled", "InflateParams"})
public class RedPackActWebActivity extends BaseActivity implements OnClickListener {
    private NewProgressWebView mWebView = null;
    private SimpleMessageView errorView = null;
    private static SharePlugin sharePlugin;
    private Activity act;
    private Handler mMainHandler = null;
    private static final int MSG_SHARE = 1;
    private static final int MSG_HOME = 2;
    private static final int MSG_LOGIN = 3;
    private static final int MSG_ADD_MOBILE_NO = 4;
    private static final int MSG_RED_PACK = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_pack_webview);
        sharePlugin = new SharePlugin(this, this.findViewById(R.id.activity_webview));
        sharePlugin.init();
        Bundle bundle = getIntent().getExtras();
        long actId = bundle.getLong("actId");
        act = new Activity();
        act.setActivityId(actId);

        initView();
        initWebView();
        initData();
        setListener();

        getActDetail();
        mMainHandler = new HanlderImp(this);
    }

    private void initView() {
        mWebView = (NewProgressWebView) findViewById(R.id.activity_webview);
        mWebView.showmLoadMessageView();//针对没有重定向，显示奶瓶加载等待页面，需要关闭
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        findViewById(R.id.action_back).setOnClickListener(this);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
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
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
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
        super.onActivityResult(requestCode, resultCode, data);
        sharePlugin.dealResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
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
                    RedPackActWebActivity.this.act = act;
                    sharePlugin.setShareObject(act);
                    String actUrl = act.getDetailUrl();
                    if (MyApplication.isLogin()) {
                        actUrl += "&mobile=" + MyApplication.getCurrentUser().getMobile();
                    }
                    mWebView.loadUrl(actUrl);
                    Log.d(TAG, "获取活动详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    class JavaScriptMethods {
        @JavascriptInterface
        public void goToShare() {
            Message message = new Message();
            message.what = MSG_SHARE;
            mMainHandler.sendMessage(message);
        }

        @JavascriptInterface
        public void goToRedPack() {
            Message message = new Message();
            message.what = MSG_RED_PACK;
            mMainHandler.sendMessage(message);
        }

        @JavascriptInterface
        public void addMobileNo(String mobileNo) {
            Message message = new Message();
            message.what = MSG_ADD_MOBILE_NO;
            Bundle bundle = new Bundle();
            bundle.putString("mobileNo", mobileNo);
            message.setData(bundle);
            mMainHandler.sendMessage(message);
        }

        @JavascriptInterface
        public void goToLogin(String mobile, String password) {
            Message message = new Message();
            message.what = MSG_LOGIN;
            Bundle bundle = new Bundle();
            bundle.putString("mobile", mobile);
            bundle.putString("password", password);
            message.setData(bundle);
            mMainHandler.sendMessage(message);
        }

        @JavascriptInterface
        public void goToHome() {
            Message message = new Message();
            message.what = MSG_HOME;
            mMainHandler.sendMessage(message);
        }
    }

    private void login(String username, String password, String loginType) {
        String url = InterfaceConstant.LOGIN;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    User user = gson.fromJson(dataJsonStr, User.class);
                    if (user != null) {
                        MyApplication.setCurrentUser(user);
                        MyApplication.setJpushAlias(true);
                        Toast.makeText(getApplicationContext(), ToastMessage.SUCCESS_TO_LOGIN,
                                Toast.LENGTH_SHORT).show();
                        mWebView.loadUrl("javascript:common.loginSuccessCallback()");
                    }
                }
                Log.d(TAG, "手机登录成功：" + dataJsonStr);
            }

            @Override
            public void doErrorResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), ToastMessage.MOBILE_OR_PWD_WRONG,
                        Toast.LENGTH_SHORT).show();
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("identify_id", username);
        requestBody.put("identify_type", loginType);
        requestBody.put("password", StringUtil.isNotEmpty(password) ? Encrypt.MD5(password) : null);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    static class HanlderImp extends Handler {
        WeakReference<android.app.Activity> mActivityReference;

        HanlderImp(android.app.Activity activity) {
            mActivityReference = new WeakReference<android.app.Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final android.app.Activity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_SHARE:
                        sharePlugin.showPop();
                        break;
                    case MSG_HOME: {
                        mActivityReference.get().finish();
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(activity, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", TypeConstant.FORWARD_PRODUCT);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        break;
                    }
                    case MSG_LOGIN: {
                        Bundle bundle = msg.getData();
                        String mobile = bundle.getString("mobile");
                        String password = bundle.getString("password");
                        ((RedPackActWebActivity) mActivityReference.get()).login(mobile, password, "phone");
                        break;
                    }
                    case MSG_ADD_MOBILE_NO: {
                        Bundle bundle = msg.getData();
                        String mobile = bundle.getString("mobileNo");
                        User currentUser = MyApplication.getCurrentUser();
                        currentUser.setMobile(mobile);
                        MyApplication.setCurrentUser(currentUser);
                        ((RedPackActWebActivity) mActivityReference.get()).mWebView.loadUrl("javascript:common.addMobileSuccessCallback()");
                        break;
                    }
                    case MSG_RED_PACK: {
                        Intent intent = new Intent();
                        int redPackPageIndex = 2;
                        intent.setClass(mActivityReference.get(), OrdersActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("displayPosition", redPackPageIndex);
                        intent.putExtras(bundle);
                        mActivityReference.get().startActivity(intent);
                        break;
                    }
                }
            }
            super.handleMessage(msg);

        }
    }
}
