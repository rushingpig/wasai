package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.Store;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.URLUtil;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.ProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.plugin.SharePlugin;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class StoreWebActivity extends BaseActivity implements View.OnClickListener{
    private NewProgressWebView mWebView;
    private View actionBack;
    private SimpleMessageView errorView;
    private long storeId;
    private GuideActionCompactCounterView shareCompactCounterView;
    private Store store;
    private SharePlugin sharePlugin;
    private Handler mMainHandler = null;
    private static final int MSG_DETAIL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_store_webview);
        initWebView();

        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getStoreDetail();
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        storeId = bundle.getLong("storeId");
        store = new Store();
        store.setId(storeId);
        String pageTitle = bundle.getString("pageTitle");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(pageTitle);

        saveStoreLog(storeId, "start");

        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        shareCompactCounterView.setOnClickListener(this);
        sharePlugin = new SharePlugin(this, this.findViewById(R.id.product_web));
        sharePlugin.init();
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(this);
        findViewById(R.id.action_close).setOnClickListener(this);
        mMainHandler = new HandlerImp(this);
//        mMainHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MSG_DETAIL:
//                        Intent intent = new Intent();
//                        intent.setClass(StoreWebActivity.this, AiTaobaoProductWebActivity.class);
//                        intent.putExtras(msg.getData());
//                        startActivity(intent);
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };
        getStoreDetail();
    }
    static class HandlerImp extends Handler{
        WeakReference<Activity> weakReference;

        public HandlerImp(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = weakReference.get();
            if(activity != null){
                switch (msg.what) {
                    case MSG_DETAIL:
                        Item item = new Item();
                        item.setOpid(msg.getData().getString("id"));
                        item.setPlatform(Constant.SHOP_TYPE_TM);
                        CommonUtil.forwardToDetailPage(weakReference.get(), item);
                        break;
                }
            }
        }
    }

    private void initWebView() {
        mWebView = (NewProgressWebView) findViewById(R.id.product_web);
        mWebView.showLoadMessageViewRef();//针对重定向加载奶瓶等待页，会自动消失
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("https://detail.m.tmall.com/item.htm?")){
                    Message message = new Message();
                    message.what = MSG_DETAIL;
                    Bundle bundle = new Bundle();
                    Map<String, String> returnParamMap = URLUtil.URLRequest(url.toLowerCase());
                    bundle.putString("id", returnParamMap.get("id"));
                    message.setData(bundle);
                    mMainHandler.sendMessage(message);
                    return true;
                }
                if(url.startsWith("tmall://tmallclient")){
                    Log.e("ProductWebActivity","错误的重定向");
                    return true;
                }
                view.loadUrl(url);
                if (MyApplication.isLogin()) {
                    if (url.startsWith("http://mclient.alipay.com") && (url.contains("/trade_pay.do?") || url.contains("/batch_payment.do?"))) {
                        Log.d("ProductWebActivity", "URL : " + url);
                        try {
                            String returnUrl = URLDecoder.decode(url, "UTF-8");
                            Map<String, String> returnParamMap = URLUtil.URLRequest(returnUrl.toLowerCase());
                            String orderIds = URLDecoder.decode(returnParamMap.get("pay_order_id"), "UTF-8");
                            Log.d("ProductWebActivity", "ORDER IDS: " + orderIds);
                            postOrders(orderIds);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.getSettings().setBlockNetworkImage(false);

            }
        });
    }

    @Override
    public void finishActivity() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
    }

    @Override
    public void finish() {
        saveStoreLog(storeId, "end");
        super.finish();
    }

    /**
     * 记录店铺日志
     *
     * @param storeId
     * @param operation
     */
    private void saveStoreLog(long storeId, String operation) {
        UserActionOfGuide myAction = new UserActionOfGuide();
        myAction.session_id = MyApplication.getSessionId();
        myAction.target = "store";
        myAction.target_id = storeId + "";
        myAction.operation = operation;
        myAction.timestamp = new Date().getTime();
        WasaiContentProviderUtils.getInstance(this).addUserAtionMessage(myAction);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharePlugin.dealResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取店铺详情
     */
    private void getStoreDetail() {
        String url = InterfaceConstant.STORE_DETAIL + "/" + this.store.getId();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Store store = gson.fromJson(dataJsonStr, Store.class);

                    StoreWebActivity.this.store = store;
                    sharePlugin.setShareObject(store);
                    if(store.getStoreUrl() != null){
                        String storeUrl = store.getStoreUrl().getMobileUrl();
                        if (MyApplication.isLogin()) {
                            storeUrl = storeUrl + "&unid=" + MyApplication.getCurrentUser().getUnid();
                        }
                        mWebView.loadUrl(storeUrl);
                    }else{
                        Toast.makeText(StoreWebActivity.this, ToastMessage.NOT_FOUND_STORE,Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "获取店铺详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void postOrders(String orderIds) {
        String url = InterfaceConstant.POST_ORDERS;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                //do something
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("uuid", MyApplication.getCurrentUser().getUuid());
        requestBody.put("unid", MyApplication.getCurrentUser().getUnid());
        requestBody.put("order_id", orderIds);
        Log.d("ProductWebActivity", "uuid : " + MyApplication.getCurrentUser().getUuid());
        Log.d("ProductWebActivity", "unid : " + MyApplication.getCurrentUser().getUnid());
        Log.d("ProductWebActivity", "order_id : " + orderIds);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.article_action_compact_share:
                if (store != null) {
                    sharePlugin.showPop();
                }
                break;
            case R.id.action_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }else{
                    finish();
                }
                break;
            case R.id.action_close:
                finish();
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
