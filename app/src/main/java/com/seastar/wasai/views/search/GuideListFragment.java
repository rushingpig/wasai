package com.seastar.wasai.views.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.adapters.SearchGuideListAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jamie on 2015/6/9.
 */
public class GuideListFragment extends ListFragment {
    private PullToRefreshListView mPullToRefreshListView = null;
    private SearchGuideListAdapter mAdapter = null;
    private ListView mListView = null;
    private List<Guide> guideList = new ArrayList<Guide>();
    private int offset = 0;
    private String currKeyword;
    private long lastClickTime;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private TextView emptyView;
    private PreferencesWrapper mPreferencesWrapper;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        mPreferencesWrapper = new PreferencesWrapper(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_list_fragment_activity, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        currKeyword = SearchResultActivity.mKeyword;
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        mPullToRefreshListView.setMode(Mode.BOTH);
        errorView = (SimpleMessageView) view.findViewById(R.id.container_error);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        emptyView = (TextView) view.findViewById(R.id.empty_textview);
        emptyView.setText(getResources().getString(R.string.search_guides_empty));
        if (!CommonUtil.checkNetWork()) {
            errorView.setVisibility(View.VISIBLE);
            loadMessageView.setVisibility(View.INVISIBLE);
            return;
        }
    }

    private void setListener() {
        mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getGuideList(true, SearchResultActivity.keywordRefresh);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getGuideList(false, currKeyword);
            }

        });
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Guide guide = guideList.get(position - 1);
                    CommonUtil.forwardToGuideDetail(activity, guide);
                }
                lastClickTime = currentTime;
            }
        });
    }

    private void initData() {
        mAdapter = new SearchGuideListAdapter(getActivity());
        mPullToRefreshListView.setAdapter(mAdapter);
        mListView = mPullToRefreshListView.getRefreshableView();
        registerForContextMenu(mListView);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        getGuideList(true, currKeyword);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();

    }


    public void refreshGuidePager(String mKeyword) {
        this.currKeyword = mKeyword;
        if (StringUtil.isNotEmpty(currKeyword)) {
            loadMessageView.setVisibility(View.VISIBLE);
            getGuideList(true, mKeyword);
        }
    }

    private void getGuideList(final boolean isRefresh, String keyword) {
        if (isRefresh) {
            offset = 0;
        }
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = InterfaceConstant.SEARCH_LIST + "/" + "guide" + "/" + keyword + "/" + offset + "/" + 20;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                boolean isEmpty = true;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        isEmpty = false;
                        if (isRefresh) {
                            guideList.clear();
                        }
                        guideList.addAll(resultList);
                        mAdapter.setmData(guideList);
                        mAdapter.notifyDataSetChanged();
                        if (MyApplication.isLogin()) {
                            getFavoriteData(resultList);
                        }
                        getGuideCounters(resultList);
                        emptyView.setVisibility(View.INVISIBLE);
                        offset = guideList.size();
                        if (isRefresh) {
                            mPullToRefreshListView.getRefreshableView().setSelection(0);
                        }
                    }
                    Log.d(TAG, "获取导购数据成功：" + dataJsonStr);
                }
                if(isEmpty){
                    if (!isRefresh) {
                        Toast.makeText(activity, ToastMessage.NOT_FOUND_GUIDE_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        guideList.clear();
                        mAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                loadMessageView.setVisibility(View.INVISIBLE);
                mPullToRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, isRefresh ? errorView : null, mPullToRefreshListView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

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
            mAdapter.notifyDataSetChanged();
        }
        if (guideList.size() > 0) {
            getGuideCounters(guideList);
        }
    }

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
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取导购计数成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

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
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取喜欢数据成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

}
