package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.URLUtil;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.LoadRebateView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.plugin.SharePlugin;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返利商品展示页
 */
@SuppressLint("SetJavaScriptEnabled")
public class AiTaobaoProductWebActivity extends BaseActivity implements View.OnClickListener{
    private NewProgressWebView mWebView;
    private View actionBack;
    private GuideActionCompactCounterView shareCompactCounterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_product_webview);
        initWebView();

        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText("商品详情");

        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        shareCompactCounterView.setVisibility(View.GONE);
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(this);

        initLoadRebateView();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String url = bundle.getString("url");
        mWebView.loadUrl(url);
        mWebView.startFlyInAnima();//开启加载动画
        findViewById(R.id.action_close).setOnClickListener(this);
    }

    private void initWebView() {
        mWebView = (NewProgressWebView) findViewById(R.id.product_web);
        mWebView.showLoadRebateViwRef();//针对重定向，显示返利的加载页面，会自动消失
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new MyWebViewClient(this));
    }

    private static class MyWebViewClient extends WebViewClient {
        private WeakReference<AiTaobaoProductWebActivity> mRef;

        public MyWebViewClient(AiTaobaoProductWebActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (MyApplication.isLogin()) {
                if(url.startsWith("tmall://tmallclient")){
                    Log.e("ProductWebActivity","错误的重定向");
                    return true;
                }
                if (url.startsWith("http://mclient.alipay.com") && (url.contains("/trade_pay.do?") || url.contains("/batch_payment.do?"))) {
                    Log.d("ProductWebActivity", "URL : " + url);
                    try {
                        String returnUrl = URLDecoder.decode(url, "UTF-8");
                        Map<String, String> returnParamMap = URLUtil.URLRequest(returnUrl.toLowerCase());
                        String orderIds = URLDecoder.decode(returnParamMap.get("pay_order_id"), "UTF-8");
                        Log.d("ProductWebActivity", "ORDER IDS: " + orderIds);
                        mRef.get().postOrders(orderIds);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mRef.get().mWebView.getSettings().setBlockNetworkImage(false);
        }
    }


    @Override
    public void finishActivity() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
    }

    @Override
    public void finish() {
        mWebView.destroy();
        super.finish();
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

    /**
     * 初始化设置加载信息
     */
    private void initLoadRebateView() {
        LoadRebateView loadRebateView = mWebView.getLoadRebateView();
        loadRebateView.setLogo(R.drawable.taobao);//logo
//        loadRebateView.priceFrame.setVisibility(View.GONE);
        loadRebateView.setPayPrice("");//设置价格
        loadRebateView.setRebatePrice("最高36%");//设置返利金额
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
    public void onClick(View v) {
        switch (v.getId()){
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
}
