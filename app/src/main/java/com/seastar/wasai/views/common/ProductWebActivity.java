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
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.trade.ItemService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
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
import com.taobao.tae.sdk.webview.TaeWebViewUiSettings;

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
public class ProductWebActivity extends BaseActivity implements View.OnClickListener {
    private NewProgressWebView mWebView;
    private View actionBack;
    private SimpleMessageView errorView;
    private long itemId;
    private GuideActionCompactCounterView shareCompactCounterView;
    private Item item;
    private SharePlugin sharePlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_product_webview);
        initWebView();
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getItemDetail();
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        itemId = bundle.getLong("itemId");
        item = new Item();
        item.setItemId(itemId);
        String pageTitle = bundle.getString("pageTitle");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(pageTitle);
        saveProductLog(itemId, "start");

        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        shareCompactCounterView.setOnClickListener(this);
        sharePlugin = new SharePlugin(this, findViewById(R.id.product_web));
        sharePlugin.init();
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(this);
        findViewById(R.id.action_close).setOnClickListener(this);
        getItemDetail();
    }

    private void initWebView() {
        mWebView = (NewProgressWebView) findViewById(R.id.product_web);
        mWebView.showLoadRebateViwRef();//针对重定向，显示返利的加载页面，会自动消失
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new MyWebViewClient(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.article_action_compact_share:
                if (item != null) {
                    sharePlugin.showPop();
                }
                break;
            case R.id.action_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.action_close:
                finish();
                break;
        }
    }

    private static class MyWebViewClient extends WebViewClient {
        private WeakReference<ProductWebActivity> mRef;

        public MyWebViewClient(ProductWebActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (MyApplication.isLogin()) {
                if (url.startsWith("tmall://tmallclient")) {
                    Log.e("ProductWebActivity", "错误的重定向");
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
            String removeHeaderJs = "";
            if (Constant.SHOP_TYPE_TB.equals(mRef.get().item.getPlatform())) {
                // do nothing
            } else if (Constant.SHOP_TYPE_TM.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "setTimeout(function(){document.getElementById('J_BottomSmartBanner').style.display = 'none'},1000);";
            } else if (Constant.SHOP_TYPE_JD.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('header').hide()";
            } else if (Constant.SHOP_TYPE_YHD.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('.touchweb-com_header').hide()";
            } else if (Constant.SHOP_TYPE_SN.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('.sn-nav').hide();$('.detail-download').hide();";
            } else if (Constant.SHOP_TYPE_MYBB.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('.header').hide();$('.header_line').hide();$('.appdownload.downloadTop.newD_keyunfeng').hide();$('.appdownload.downloadBtm').hide()";
            } else if (Constant.SHOP_TYPE_YMX.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('nav').hide();$('#smart-app-banner').hide();";
            } else if (Constant.SHOP_TYPE_DD.equals(mRef.get().item.getPlatform())) {
                removeHeaderJs = "$('.app_list').hide();";
            }
            if (!removeHeaderJs.equals("")) {
                mRef.get().mWebView.loadUrl("javascript:" + removeHeaderJs);
            }
        }
    }


    @Override
    public void finishActivity() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
    }

    @Override
    public void finish() {
        saveProductLog(itemId, "end");
        mWebView.destroy();
        super.finish();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: saveLog
     * @Description: 记录商品日志
     */
    private void saveProductLog(long itemId, String operation) {
        UserActionOfGuide myAction = new UserActionOfGuide();
        myAction.session_id = MyApplication.getSessionId();
        myAction.target = "item";
        myAction.target_id = itemId + "";
        myAction.operation = operation;
        myAction.timestamp = new Date().getTime();
        WasaiContentProviderUtils.getInstance(this).addUserAtionMessage(myAction);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharePlugin.dealResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class GetDetailSuccessListener extends MyReqSucessListener {
        private WeakReference<ProductWebActivity> mRef;

        public GetDetailSuccessListener(ProductWebActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        public void doResponse(String dataJsonStr) {
            if (dataJsonStr != null) {
                ProductWebActivity activity = mRef.get();
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                if (String.valueOf(mRef.get().itemId).startsWith("9")) {
                    Item item = gson.fromJson(dataJsonStr, Item.class);
                    if (item != null) {
                        activity.item = item;
                    }
                } else {
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        activity.item = resultList.get(0);
                    }
                }
                activity.sharePlugin.setShareObject(activity.item);
                activity.initLoadRebateView();//初始化加载页
                String itemUrl = activity.item.getShopUrl().getMobileUrl();
                if (MyApplication.isLogin()) {
                    if(itemUrl.indexOf("?") != -1){
                        itemUrl = itemUrl + "&unid=" + MyApplication.getCurrentUser().getUnid();
                    }else{
                        itemUrl = itemUrl + "?unid=" + MyApplication.getCurrentUser().getUnid();
                    }
                }
                activity.mWebView.loadUrl(itemUrl);
                activity.mWebView.startFlyInAnima();//开启加载动画
                Log.d(TAG, "获取商品详情成功：" + dataJsonStr);
            }
        }
    }

    /**
     * 获取商品详情
     */
    private void getItemDetail() {
        String url = InterfaceConstant.ITEM + "/" + itemId;
        if (String.valueOf(itemId).startsWith("9")) {
            url = InterfaceConstant.ITEM_SUPER + "/" + itemId;
        }
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(new GetDetailSuccessListener(this)));
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
        String platform = ProductWebActivity.this.item.getPlatform();
        int resId = -1;
        if (Constant.SHOP_TYPE_TB.equals(platform)) {
            resId = R.drawable.taobao;
        } else if (Constant.SHOP_TYPE_TM.equals(platform)) {
            resId = R.drawable.tmall;
        } else if (Constant.SHOP_TYPE_JD.equals(platform)) {
            resId = R.drawable.jdlogo;
        } else if (Constant.SHOP_TYPE_YHD.equals(platform)) {
            resId = R.drawable.yhd;
        } else if (Constant.SHOP_TYPE_SN.equals(platform)) {
            resId = R.drawable.suning;
        } else if (Constant.SHOP_TYPE_MYBB.equals(platform)) {
            resId = R.drawable.miyababy;
        } else if (Constant.SHOP_TYPE_YMX.equals(platform)) {
            resId = R.drawable.amazon;
        } else if (Constant.SHOP_TYPE_DD.equals(platform)) {
            resId = R.drawable.dangdang;
        }
        if (resId != -1) {
            loadRebateView.setLogo(resId);//logo
        }
        loadRebateView.setPayPrice(item.getNowPrice());//设置价格
        loadRebateView.setRebatePrice(item.getUnit() + item.getRate());//设置返利金额
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
