package com.seastar.wasai.views.product;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
import com.seastar.wasai.views.MainActivity;
import com.seastar.wasai.views.adapters.ProductListAdapter;
import com.seastar.wasai.views.base.BaseFragment;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.PullToRefreshStaggeredGridView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jamie on 2015/6/16.
 */
public class ProductFragment extends BaseFragment {
    public static final String TAG = "ProductFragment";
    private List<Item> mListItems = new ArrayList<Item>();
    private ProductListAdapter mAdapter;
    private PullToRefreshStaggeredGridView mPullRefreshGridView;
    private StaggeredGridView mGridView;
    private long lastId = 0l;
    private int categoryListId;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private TextView mEmptyTextView = null;
    private long lastClickTime;

    private Context mContext;
    private Handler timeHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryListId = bundle.getInt("id");
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View contentView = inflater.inflate(R.layout.product_fragment, null);
        initView1(contentView);
        initData1();
        setListener();
        return contentView;
    }

    @Override
    public void initData() {
        getItemSuper(categoryListId);
        timeHandler.postDelayed(superRebateRunnable, 1000);
    }

    private void initView1(View view) {
        mEmptyTextView = (TextView) view.findViewById(R.id.empty_textview);
        errorView = (SimpleMessageView) view.findViewById(R.id.container_error);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        mPullRefreshGridView = (PullToRefreshStaggeredGridView) view.findViewById(R.id.pull_refresh_grid);
        mGridView = mPullRefreshGridView.getRefreshableView();
    }

    private void setListener() {
        onRefreshListenerImp();
        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    final Item product = mListItems.get(arg2);
                    if(!product.isSuperItem() || (product.getRemainTime() > 0  && product.isSuperItem())){
                        CommonUtil.forwardToDetailPage(ProductFragment.this.getActivity(), product);
                    }
                }
                lastClickTime = currentTime;
            }

        });
        errorView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                loadMessageView.setVisibility(View.VISIBLE);
                getItemSuper(categoryListId);
            }
        });
    }

    Runnable superRebateRunnable = new Runnable() {
        @Override
        public void run() {
            for (Item item : mListItems) {
                if (item.isSuperItem()) {
                    if (item.getRemainTime() > 0) {
                        item.setRemainTime(item.getRemainTime() - 1000);
                    }
//                    else {
//                        refreshSuper(categoryListId);
//                        break;
//                    }
                }
            }
            mAdapter.notifyDataSetChanged();
            timeHandler.postDelayed(this, 1000);
        }
    };

    private void onRefreshListenerImp() {
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getItemSuper(categoryListId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getItemList(false, categoryListId);
            }
        });
    }

    private void initData1() {
        if (mAdapter == null) {
            mAdapter = new ProductListAdapter(mContext, mListItems);
        }
        mGridView.setAdapter(mAdapter);
    }

    private void getItemSuper(final int categoryListId) {
        String url = InterfaceConstant.PRODUCT_ITEMSUPER + "/" + categoryListId;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                mListItems.clear();
                mAdapter.notifyDataSetChanged();
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        mListItems.addAll(resultList);
                        gridviewNotifyData();
                    }
                }
                Log.d(TAG, "超级返商品列表加载成功：" + dataJsonStr);
                getBigDataItemList(categoryListId);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void refreshSuper(final int categoryListId) {
        String url = InterfaceConstant.PRODUCT_ITEMSUPER + "/" + categoryListId;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        Iterator<Item> it = mListItems.iterator();
                        while (it.hasNext()) {
                            Item tempItem = it.next();
                            if (tempItem.isSuperItem()) {
                                it.remove();
                            }
                        }
                        mListItems.addAll(0, resultList);
                        gridviewNotifyData();
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void getBigDataItemList(final int categoryListId) {
        String url = InterfaceConstant.ITEM_LIST_BIG_DATA + "/" + categoryListId + "/2/0/20";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        mListItems.addAll(resultList);
                        gridviewNotifyData();
                    }
                }
                Log.d(TAG, "商品大数据列表加载成功：" + dataJsonStr);
                getItemList(true, categoryListId);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null,null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void getItemList(final boolean isRefresh, int categoryListId) {
        if (isRefresh) {
            lastId = 0l;
        }
        String url = InterfaceConstant.ITEM_LIST + "/" + categoryListId + "/" + lastId + "/2/20";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                boolean hasData = false;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        hasData = true;
                        lastId = resultList.get(resultList.size() - 1).getItemId();
                        mListItems.addAll(resultList);
                        gridviewNotifyData();
                        if (MyApplication.isLogin()) {
                            getItemsWishId(resultList);
                        }
                    }
                }
                if (isRefresh) {
                    if(mListItems.size() == 0){
                        mEmptyTextView.setVisibility(View.VISIBLE);
                    }
                }
                if (!hasData && !isRefresh) {
                    Toast.makeText(ProductFragment.this.getActivity(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                }
                mEmptyTextView.setVisibility(View.INVISIBLE);
                loadMessageView.setVisibility(View.INVISIBLE);
                mPullRefreshGridView.onRefreshComplete();
                Log.d(TAG, "商品列表加载成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, isRefresh ? errorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


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
                        gridviewNotifyData();
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.forwardParam == categoryListId) {
            MainActivity.forwardParam = 0;
            loadMessageView.setVisibility(View.VISIBLE);
            getItemSuper(categoryListId);
        } else {
            if (MyApplication.isLogin()) {
                getItemsWishId(mListItems);
            } else {
                for (Item item : mListItems) {
                    item.setWishId(0);
                }
                gridviewNotifyData();
            }
        }
    }

    private void gridviewNotifyData() {
        if (mAdapter != null) {
            mPullRefreshGridView.requestLayout();
            mAdapter.notifyDataSetChanged();
        }
    }
}
