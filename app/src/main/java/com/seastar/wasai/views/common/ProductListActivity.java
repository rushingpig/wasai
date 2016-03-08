package com.seastar.wasai.views.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.UserWishMap;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.ProductListAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProductListActivity extends Activity {
    private List<Item> mListItems = new ArrayList<Item>();
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private ProductListAdapter mAdapter;
    private long lastId = 0l;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private String apiUrl;
    private long lastClickTime;

    @Override
    public void onResume() {
        super.onResume();
        if (!MyApplication.isLogin()) {
            for (Item item : mListItems) {
                item.setWishId(0);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            getItemsWishId(mListItems);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Bundle bundle = this.getIntent().getExtras();
        apiUrl = bundle.getString("apiUrl");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(bundle.getString("pageTitle"));

        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                loadMessageView.setVisibility(View.VISIBLE);
                getItemList(true);
            }
        });

        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        mGridView = mPullRefreshGridView.getRefreshableView();
        mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                getItemList(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                getItemList(false);
            }
        });
        mPullRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    final Item product = (Item) mListItems.get(arg2);
                    CommonUtil.forwardToDetailPage(ProductListActivity.this, product);
                }
                lastClickTime = currentTime;
            }

        });
        mAdapter = new ProductListAdapter(this);
        mAdapter.setData(mListItems);
        mGridView.setAdapter(mAdapter);
        getItemList(true);
    }

    /**
     * 获取商品列表
     *
     * @param isRefresh
     */
    private void getItemList(final boolean isRefresh) {
        if (isRefresh) {
            lastId = 0l;
        }
        String url = apiUrl + "/" + lastId + "/2/20";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        if (isRefresh) {
                            mListItems.clear();
                        }
                        lastId = resultList.get(resultList.size() - 1).getItemId();
                        mListItems.addAll(resultList);
                        mAdapter.notifyDataSetChanged();
                        if (MyApplication.isLogin()) {
                            getItemsWishId(resultList);
                        }
                    }
                } else {
                    if (!isRefresh && mListItems.size() > 0) {
                        Toast.makeText(ProductListActivity.this, ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        mListItems.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                }
                loadMessageView.setVisibility(View.INVISIBLE);
                mPullRefreshGridView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, isRefresh ? errorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取商品是否被喜欢
     *
     * @param items
     */
    private void getItemsWishId(final List<Item> items) {
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
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
