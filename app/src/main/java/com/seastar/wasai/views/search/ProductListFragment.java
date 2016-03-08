package com.seastar.wasai.views.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.UserWishMap;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.adapters.SearchProductAdapter;
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
public class ProductListFragment extends Fragment {
    private List<Item> mListItems = new ArrayList<Item>();
    private PullToRefreshGridView mPullToRefreshGridView = null;
    private GridView mGridView = null;
    private int offset = 0;
    private String currKeyword;
    private SearchProductAdapter mAdapter;
    private LoadMessageView loadMessageView;
    private SimpleMessageView errorView;
    private TextView mEmptyTextView;
    private PreferencesWrapper mPreferencesWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_product_activity, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        currKeyword = SearchResultActivity.mKeyword;
        mPullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.search_pull_refresh_grid);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        errorView = (SimpleMessageView) view.findViewById(R.id.container_error);
        mEmptyTextView = (TextView) view.findViewById(R.id.empty_textview);
        mEmptyTextView.setText(getResources().getString(R.string.search_products_empty));
        if (!CommonUtil.checkNetWork()) {
            loadMessageView.setVisibility(View.INVISIBLE);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        mPullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getProductList(true, currKeyword);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getProductList(false, currKeyword);
            }
        });
    }

    private long lastClickTime;

    private void initData() {
        mAdapter = new SearchProductAdapter(getActivity(), mListItems);
        mPullToRefreshGridView.setAdapter(mAdapter);
        mGridView = mPullToRefreshGridView.getRefreshableView();
        registerForContextMenu(mGridView);
        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        getProductList(true, currKeyword);

        mPullToRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    final Item product = (Item) mListItems.get(arg2);
                    CommonUtil.forwardToDetailPage(getActivity(), product);
                }
                lastClickTime = currentTime;
            }

        });
    }

    public void refreshProductPager(String mKeyword) {
        this.currKeyword = mKeyword;
        if (StringUtil.isNotEmpty(this.currKeyword)) {
            getProductList(true, mKeyword);
            loadMessageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPreferencesWrapper = new PreferencesWrapper(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            String str = mPreferencesWrapper.getStringValue("save_data", "");
            if (!TextUtils.isEmpty(str)) {
                refreshProductPager(str);
            }
        }
    }

    private void getProductList(final boolean isRefresh, String keyword) {
        if (isRefresh) {
            offset = 0;
        }
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = InterfaceConstant.SEARCH_LIST + "/" + "item" + "/" + keyword + "/" + offset + "/" + 20;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
               boolean isEmpty = true;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        isEmpty = false;
                        if (isRefresh) {
                            mListItems.clear();
                        }
                        mListItems.addAll(resultList);
                        if (isRefresh) {
                            mPullToRefreshGridView.getRefreshableView().setFocusableInTouchMode(true);
                            mPullToRefreshGridView.getRefreshableView().requestFocus();
                            mPullToRefreshGridView.getRefreshableView().setSelection(0);
                        }
                        mAdapter.notifyDataSetChanged();
                        if (MyApplication.isLogin()) {
                            getFavoriteData(resultList);
                        }
                        mEmptyTextView.setVisibility(View.INVISIBLE);
                        offset = mListItems.size();
                    }
                    Log.d(TAG, "获取商品数据成功：" + dataJsonStr);
                }
                if(isEmpty){
                    if (!isRefresh) {
                        Toast.makeText(ProductListFragment.this.getActivity(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        mListItems.clear();
                        mAdapter.notifyDataSetChanged();
                        mEmptyTextView.setVisibility(View.VISIBLE);
                    }
                }
                loadMessageView.setVisibility(View.GONE);
                mPullToRefreshGridView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, isRefresh ? errorView : null, mPullToRefreshGridView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
    }

    private void getFavoriteData(final List<Item> items) {
        StringBuffer ids = new StringBuffer();
        for (int i = 0; i < items.size(); i++) {
            if (i < items.size() - 1) {
                ids.append(items.get(i).getItemId() + "_");
            } else {
                ids.append(items.get(i).getItemId());
            }
        }
        String url = InterfaceConstant.WISHLIST + "/" + ids;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<UserWishMap> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<UserWishMap>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        for (Item item : items) {
                            for (UserWishMap map : resultList) {
                                if (map.getItemId() == item.getItemId()) {
                                    item.setWishId(map.getWishId());
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
