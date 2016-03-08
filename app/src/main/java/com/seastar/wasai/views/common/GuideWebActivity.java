package com.seastar.wasai.views.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ObjectType;
import com.seastar.wasai.Entity.SearchHistoryDataEntity;
import com.seastar.wasai.Entity.ShoppingGuideArticlesEntity;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.NewProgressWebView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;
import com.seastar.wasai.views.guide.GuideCommentsActivity;
import com.seastar.wasai.views.guide.TagGuideListActivity;
import com.seastar.wasai.views.login.LoginActivity;
import com.seastar.wasai.views.plugin.SharePlugin;
import com.seastar.wasai.views.search.SearchResultActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressLint({"JavascriptInterface", "InflateParams", "SetJavaScriptEnabled"})
public class GuideWebActivity extends BaseActivity {

    private Guide guide;
    private NewProgressWebView mWebView;
    private GuideActionCompactCounterView favoriteCompactCounterView;
    private GuideActionCompactCounterView shareCompactCounterView;
    private GuideActionCompactCounterView commentCompactCounterView;
    private SimpleMessageView errorView;
    private int CODE_COMMENT_LIST = 0;
    private int CODE_LOGIN = 1;
    private long lastClickTime;
    private SharePlugin sharePlugin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);

        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        mWebView = (NewProgressWebView) findViewById(R.id.guide_web);
        mWebView.showmLoadMessageView();//针对没有重定向，显示奶瓶加载等待页面，需要关闭
        favoriteCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_fav);
        favoriteCompactCounterView.setImageResource(R.drawable.ic_action_compact_favourite_normal);
        commentCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_comment);
        commentCompactCounterView.setImageResource(R.drawable.ic_action_compact_comment);
        shareCompactCounterView = (GuideActionCompactCounterView) findViewById(R.id.article_action_compact_share);
        shareCompactCounterView.setImageResource(R.drawable.ic_action_compact_share);
        initWebView();

        findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("guideId", GuideWebActivity.this.guide.getGuideId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getGuideDetail();
            }
        });

        sharePlugin = new SharePlugin(this, this.findViewById(R.id.guide_page_container));
        sharePlugin.init();
        bindActionBarEvents();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        long guideId = bundle.getLong("guideId");
        String pageTitle = bundle.getString("pageTitle");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(pageTitle);

        this.guide = new Guide();
        this.guide.setGuideId(guideId);

        getGuideDetail();
        saveGuideLog("start");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_COMMENT_LIST) {
            refreshCommentsCount();
        } else if (requestCode == CODE_LOGIN) {
            mWebView.loadUrl(guide.getDetailUrl());
        } else {
            sharePlugin.dealResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 刷新评论数
     */
    private void refreshCommentsCount() {
        String url = InterfaceConstant.GUIDE + "/" + this.guide.getGuideId();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Guide guide = gson.fromJson(dataJsonStr, Guide.class);
                    GuideWebActivity.this.guide.setCommentCount(guide.getCommentCount());
                    commentCompactCounterView.setCountViewText(String.valueOf(guide.getCommentCount()));
                    Log.d(TAG, "刷新评论数成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("guideId", this.guide.getGuideId());
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取导购详情
     */
    private void getGuideDetail() {
        String url = InterfaceConstant.GUIDE + "/" + this.guide.getGuideId();
        Log.d("GET GUIDE DETAIL", "GUIDE ID : " + this.guide.getGuideId());
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Guide guide = gson.fromJson(dataJsonStr, Guide.class);

                    GuideWebActivity.this.guide = guide;
                    sharePlugin.setShareObject(guide);
                    favoriteCompactCounterView.setCountViewText(String.valueOf(guide.getFavoriteCount()));
                    commentCompactCounterView.setCountViewText(String.valueOf(guide.getCommentCount()));
                    Log.d(TAG, "导购详情URL : " + guide.getDetailUrl());
                    mWebView.loadUrl(guide.getDetailUrl());
                    if (MyApplication.isLogin()) {
                        getFavoriteData();
                    }
                    Log.d(TAG, "获取导购详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, errorView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void getFavoriteData() {
        String url = InterfaceConstant.FAVORITE_GUIDE + "/" + guide.getGuideId();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        guide.setFavoriteId(resultList.get(0).getFavoriteId());
                        if (resultList.get(0).getFavoriteId() > 0) {
                            favoriteCompactCounterView.setImageResource(R.drawable.ic_action_compact_favourite_selected);
                        }
                    }
                    Log.d(TAG, "获取导购是否被喜欢成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    private void postFavorite(final int actionType) {
        String url = InterfaceConstant.FAVORITE;
        int method = Request.Method.POST;
        if (actionType == 0) {
            url = InterfaceConstant.FAVORITE + "/" + guide.getGuideId();
            method = Request.Method.DELETE;
        }
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                Guide tempGuide = gson.fromJson(dataJsonStr, Guide.class);
                guide.setFavoriteId(actionType == 0 ? 0l : 1l);
                guide.setFavoriteCount(tempGuide.getFavoriteCount());
                favoriteCompactCounterView.setCountViewText(tempGuide.getFavoriteCount() + "");
                Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(method, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("guide_id", guide.getGuideId() + "");
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    private void initWebView() {
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        mWebView.addJavascriptInterface(new JavaScriptMethods(), "nativeObj");
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

    private void bindActionBarEvents() {
        favoriteCompactCounterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.isLogin()) {
                    if (CommonUtil.checkNetWork()) {
                        if (guide.getFavoriteId() > 0) {
                            favoriteCompactCounterView.setImageResource(R.drawable.ic_action_compact_favourite_normal);
                            favoriteCompactCounterView.setCountViewText((guide.getFavoriteCount() - 1) + "");
                            postFavorite(0);
                        } else {
                            favoriteCompactCounterView
                                    .setImageResource(R.drawable.ic_action_compact_favourite_selected);
                            favoriteCompactCounterView.setCountViewText((guide.getFavoriteCount() + 1) + "");
                            postFavorite(1);
                        }
                    } else {
                        GeneralUtil.showToastShort(GuideWebActivity.this, ToastMessage.NET_WORK_NOT_WORK);
                    }
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        commentCompactCounterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, GuideCommentsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("guideId", GuideWebActivity.this.guide.getGuideId());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CODE_COMMENT_LIST);
                }
                lastClickTime = currentTime;
            }
        });

        shareCompactCounterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guide != null) {
                    sharePlugin.showPop();
                }
            }
        });
    }

    private void saveGuideLog(String operation) {
        UserActionOfGuide myAction = new UserActionOfGuide();
        myAction.session_id = MyApplication.getSessionId();
        myAction.target = "guide";
        myAction.target_id = this.guide.getGuideId() + "";
        myAction.operation = operation;
        myAction.timestamp = new Date().getTime();
        WasaiContentProviderUtils.getInstance(this).addUserAtionMessage(myAction);
    }


    private void savForwardLog(long itemId) {
        ShoppingGuideArticlesEntity entity = new ShoppingGuideArticlesEntity();
        entity.session_id = MyApplication.getSessionId();
        entity.guide_id = guide.getGuideId() + "";
        entity.item_id = itemId + "";
        entity.timestamp = new Date().getTime() + "";
        WasaiContentProviderUtils.getInstance(this).addUserAtionOfShoppingGuideArticlesData(entity);
    }

    @Override
    public void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("guideId", this.guide.getGuideId());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finish() {
        saveGuideLog("end");
        sharePlugin.closePop();
        super.finish();
    }

    class JavaScriptMethods {
        @JavascriptInterface
        public void forwardToProduct(String platform,String itemId) {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000 && itemId != null) {
                Item product = new Item();
                product.setPlatform(platform);
                if(Constant.SHOP_TYPE_TB.equals(platform) || Constant.SHOP_TYPE_TM.equals(platform)){
                    product.setItemId(1l);
                    product.setOpid(itemId);
                }else{
                    product.setItemId(Long.parseLong(itemId));
                }
                savForwardLog(Long.parseLong(itemId));
                CommonUtil.forwardToDetailPage(GuideWebActivity.this, product);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void goToArticle(String articleId, int type) {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                ObjectType objType = MyApplication.getObjectType(type);
                Intent intent = new Intent(GuideWebActivity.this, MyApplication.getTemplate(objType.getTemplateId()));
                Bundle bundle = new Bundle();
                bundle.putLong("guideId", Long.parseLong(articleId));
                bundle.putString("pageTitle", objType.getTitle());
                intent.putExtras(bundle);
                GuideWebActivity.this.startActivity(intent);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void toGuideListByTag(String tagId) {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Intent intent = new Intent(GuideWebActivity.this, TagGuideListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("tagId", Long.parseLong(tagId));
                intent.putExtras(bundle);
                GuideWebActivity.this.startActivity(intent);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void toGuideListSearch(String keyword) {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                insertOrUpdateData(keyword);
                Intent reIntent = new Intent(GuideWebActivity.this, SearchResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("keyword", keyword);
                bundle.putInt("position",2);
                reIntent.putExtras(bundle);
                startActivity(reIntent);
            }
            lastClickTime = currentTime;
        }

        @JavascriptInterface
        public void login() {
            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                Context context = GuideWebActivity.this;
                Intent intent = new Intent(context, LoginActivity.class);
                startActivityForResult(intent, CODE_LOGIN);
            }
            lastClickTime = currentTime;
        }


    }

    private void insertOrUpdateData(String currentText) {
        SearchHistoryDataEntity entity = new SearchHistoryDataEntity();
        entity.search_name = currentText;
        entity.search_time = String.valueOf(new Date().getTime());
        boolean flag = false;
        List<SearchHistoryDataEntity> mList = WasaiContentProviderUtils.getInstance(getApplicationContext()).querySearchHistoryData();
        for (SearchHistoryDataEntity tempEntity : mList) {
            if (tempEntity.search_name.equals(currentText)) {
                WasaiContentProviderUtils.getInstance(getApplicationContext()).updateSearchHistoryData(entity);
                flag = true;
                break;
            }
        }
        if (!flag) {
            WasaiContentProviderUtils.getInstance(getApplicationContext()).addSearchHistoryData(entity);
        }
    }
}
