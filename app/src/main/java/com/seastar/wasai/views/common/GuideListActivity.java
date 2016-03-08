package com.seastar.wasai.views.common;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.TagGuideListAdapter;
import com.seastar.wasai.views.base.BaseListActivity;
import com.seastar.wasai.views.extendedcomponent.EmptyMessageView;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class GuideListActivity extends BaseListActivity {
    private PullToRefreshListView mPullRefreshListView;
    private TagGuideListAdapter guideListAdapter;
    private List<Guide> guideList = new ArrayList<Guide>();
    private Long lastId = 0l;
    private View actionBack;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private EmptyMessageView emptyView;

    private String apiUrl;

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.isLogin()) {
            if (guideList.size() > 0) {
                getFavoriteData(guideList);
            }
        } else {
            for (Guide guide : guideList) {
                guide.setFavoriteId(0);
            }
        }
        if (guideList.size() > 0) {
            getGuideCounters(guideList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_guide_list);

        Bundle bundle = this.getIntent().getExtras();
        apiUrl = bundle.getString("apiUrl");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(bundle.getString("pageTitle"));

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setMode(Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getGuideList(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getGuideList(false);
            }

        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Guide guide = guideList.get(arg2 - 1);
                CommonUtil.forwardToGuideDetail(GuideListActivity.this, guide);
            }
        });

        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ListView actualListView = mPullRefreshListView.getRefreshableView();
        registerForContextMenu(actualListView);
        guideListAdapter = new TagGuideListAdapter(this);
        actualListView.setAdapter(guideListAdapter);
        actualListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                if (CommonUtil.checkNetWork()) {
                    errorView.setVisibility(View.INVISIBLE);
                    loadMessageView.setVisibility(View.VISIBLE);
                    getGuideList(true);
                }
            }
        });
        emptyView = (EmptyMessageView) findViewById(R.id.container_empty);
        findViewById(R.id.titleBar).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualListView.setSelection(0);
            }
        });
        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        getGuideList(true);
    }

    /**
     * 获取导购列表
     */
    private void getGuideList(final boolean isRefresh) {
        if (isRefresh) {
            lastId = 0l;
        }
        String url = InterfaceConstant.INTERFACE_HOST + apiUrl + "/" + lastId + "/" + 2 + "/" + 20;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        if (isRefresh) {
                            guideList.clear();
                        }
                        lastId = resultList.get(resultList.size() - 1).getGuideId();
                        guideList.addAll(resultList);
                        guideListAdapter.setmData(guideList);
                        guideListAdapter.notifyDataSetChanged();
                        if (MyApplication.isLogin()) {
                            getFavoriteData(resultList);
                        }
                        getGuideCounters(resultList);
                        emptyView.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "获取导购数据成功：" + dataJsonStr);
                } else {
                    if (!isRefresh) {
                        Toast.makeText(GuideListActivity.this, ToastMessage.NOT_FOUND_GUIDE_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        guideList.clear();
                        guideListAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                loadMessageView.setVisibility(View.INVISIBLE);
                mPullRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, isRefresh ? errorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取用户是否喜欢
     *
     * @param guideList
     */
    private void getFavoriteData(final List<Guide> guideList) {
        StringBuffer guideIds = new StringBuffer();
        for (int i = 0; i < guideList.size(); i++) {
            Guide guide = guideList.get(i);
            if (i < guideList.size() - 1) {
                guideIds.append(guide.getGuideId() + "_");
            } else {
                guideIds.append(guide.getGuideId());
            }
        }
        String url = InterfaceConstant.FAVORITE_GUIDE + "/" + guideIds;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        for (Guide guide : guideList) {
                            for (Guide resultGuide : resultList) {
                                if (guide.getGuideId() == resultGuide.getGuideId()) {
                                    guide.setFavoriteId(resultGuide.getFavoriteId());
                                    guide.setFavoriteCount(resultGuide.getFavoriteCount());
                                }
                            }
                        }
                        guideListAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取喜欢数据成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取导购喜欢数和浏览数
     */
    private void getGuideCounters(final List<Guide> guideList) {
        StringBuffer guideIds = new StringBuffer();
        for (int i = 0; i < guideList.size(); i++) {
            Guide guide = guideList.get(i);
            if (i < guideList.size() - 1) {
                guideIds.append(guide.getGuideId() + "_");
            } else {
                guideIds.append(guide.getGuideId());
            }
        }
        String url = InterfaceConstant.GUIDE + "/counters/" + guideIds;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        for (Guide guide : guideList) {
                            for (Guide resultGuide : resultList) {
                                if (guide.getGuideId() == resultGuide.getGuideId()) {
                                    guide.setPvCount(resultGuide.getPvCount());
                                    guide.setFavoriteCount(resultGuide.getFavoriteCount());
                                }
                            }
                        }
                    }
                    Log.d(TAG, "获取导购计数成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

}
