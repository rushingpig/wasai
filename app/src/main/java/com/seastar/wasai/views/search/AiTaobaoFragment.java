package com.seastar.wasai.views.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.URLUtil;
import com.seastar.wasai.views.common.AiTaobaoProductWebActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jamie on 2015/6/9.
 */
public class AiTaobaoFragment extends Fragment {
    private WebView mWebView;
    private String mKeyword;
    private final String searchURL = "http://ai.taobao.com/search/index.htm";

//    private Handler mMainHandler = null;
    private static final int MSG_DETAIL = 1;
    private Handler mMainHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aitaobao_search,container,false);
        mKeyword = SearchResultActivity.mKeyword;
        initWebView(view);
        mWebView.loadUrl(wrapSearchURL(searchURL, mKeyword));
        mMainHandler = new HanlderImp(getActivity());
//        mMainHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//                super.handleMessage(msg);
//            }
//        };

        return view;
    }
    private static class HanlderImp extends Handler{
        WeakReference<Activity> mActivityReference;

        public HanlderImp(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            if (activity != null){
                switch (msg.what) {
                    case MSG_DETAIL:
                        Item item = new Item();
                        item.setItemId(Long.parseLong(msg.getData().getString("id")));
                        item.setPlatform(Constant.SHOP_TYPE_TB);
                        CommonUtil.forwardToDetailPage(mActivityReference.get(),item );
                        break;
                }
            }
            super.handleMessage(msg);

        }
    }

    private String wrapSearchURL(String URL, String keyword) {
        StringBuffer sb = new StringBuffer(URL);
        sb.append("?").append("pid=").append(MyApplication.ALIMAMA_PID);
        if (MyApplication.isLogin() && MyApplication.getCurrentUser().getUnid() != null) {
            sb.append("&unid=").append(MyApplication.getCurrentUser().getUnid());
        }
        sb.append("&source_id=search");
        sb.append("&key=").append(keyword);
        return sb.toString();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initWebView(View view) {
        mWebView = (WebView) view.findViewById(R.id.search_result_web);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setBlockNetworkImage(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("ProductWebActivity","重定向 : " + url);
                if(url.startsWith("http://s.click")){
                    Message message = new Message();
                    message.what = MSG_DETAIL;
                    Bundle bundle = new Bundle();
                    Map<String, String> returnParamMap = URLUtil.URLRequest(url.toLowerCase());
                    bundle.putString("id",returnParamMap.get("id"));
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

    public void refreshProductPager(String mKeyword) {
        this.mKeyword = mKeyword;
        if (mWebView != null) {
            mWebView.loadUrl(wrapSearchURL(searchURL, mKeyword));
        }
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
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainHandler.removeCallbacksAndMessages(null);
    }
}
