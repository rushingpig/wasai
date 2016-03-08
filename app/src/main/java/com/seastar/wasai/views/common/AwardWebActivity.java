package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seastar.wasai.Entity.Award;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.plugin.SharePlugin;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class AwardWebActivity extends BaseActivity {
    private NewProgressWebView mWebView;
    private View actionBack;
//    private LoadMessageView loadMessageView;
    private SimpleMessageView errorView;
    private long awardId;
    private GuideActionCompactCounterView shareCompactCounterView;
    private Award award;
    private SharePlugin sharePlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_product_webview);
        initWebView();

//        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
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
        awardId = bundle.getLong("awardId");
        award = new Award();
        award.setId(awardId);
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(Constant.TITLE_AWARD);

        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        shareCompactCounterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (award != null) {
                    sharePlugin.showPop();
                }

            }
        });
        sharePlugin = new SharePlugin(this, this.findViewById(R.id.product_web));
        sharePlugin.init();
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getItemDetail();
    }

    private void initWebView() {
        mWebView = (NewProgressWebView) findViewById(R.id.product_web);
        mWebView.showmLoadMessageView();//针对没有重定向，显示奶瓶加载等待页面，需要关闭
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.dismissLoadMessageView();//隐藏奶瓶等待加载页面
                mWebView.getSettings().setBlockNetworkImage(false);
                String removeHeaderJs = "";
                if (Constant.SHOP_TYPE_TB.equals(AwardWebActivity.this.award.getPlatform())) {
                    // do nothing
                } else if (Constant.SHOP_TYPE_TM.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "setTimeout(function(){document.getElementById('J_BottomSmartBanner').style.display = 'none'},1000);";
                } else if (Constant.SHOP_TYPE_JD.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('header').hide()";
                } else if (Constant.SHOP_TYPE_YHD.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('.touchweb-com_header').hide()";
                } else if (Constant.SHOP_TYPE_SN.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('.sn-nav').hide();$('.detail-download').hide();";
                } else if (Constant.SHOP_TYPE_MYBB.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('.header').hide();$('.header_line').hide();$('.appdownload.downloadTop.newD_keyunfeng').hide();$('.appdownload.downloadBtm').hide()";
                } else if (Constant.SHOP_TYPE_YMX.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('nav').hide();$('#smart-app-banner').hide();";
                } else if (Constant.SHOP_TYPE_DD.equals(AwardWebActivity.this.award.getPlatform())) {
                    removeHeaderJs = "$('.app_list').hide();";
                }
                if (!removeHeaderJs.equals("")) {
                    mWebView.loadUrl("javascript:" + removeHeaderJs);
                }
            }
        });
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    public void finishActivity() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharePlugin.dealResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取商品详情
     */
    private void getItemDetail() {
        String url = InterfaceConstant.AWARD + "/" + awardId;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Award award = gson.fromJson(dataJsonStr, Award.class);
                    if(award != null){
                        AwardWebActivity.this.award = award;
                        sharePlugin.setShareObject(award);
                        mWebView.loadUrl(award.getUrl().getMobileUrl());
                    }
//                    loadMessageView.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "获取奖品详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
