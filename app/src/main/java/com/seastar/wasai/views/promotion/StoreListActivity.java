package com.seastar.wasai.views.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.Store;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.StoreListAdapter;
import com.seastar.wasai.views.base.BaseListActivity;
import com.seastar.wasai.views.common.StoreWebActivity;
import com.seastar.wasai.views.extendedcomponent.EmptyMessageView;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jakey on 2015/6/18.
 */
public class StoreListActivity extends BaseListActivity {
    private static final String TAG = "StoreListActivity";
    private PullToRefreshListView mPullRefreshListView;
    private StoreListAdapter storeListAdapter;
    private List<Store> storeList = new ArrayList<>();
    private View actionBack;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private EmptyMessageView emptyView;
    private int offset = 0;
    private static final int LIMIT = 20;
    private String apiUrl;
    private long lastClickTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_store_promotionl);

        //返回
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        apiUrl = bundle.getString("apiUrl");
        TextView pageTitleView = (TextView) findViewById(R.id.page_title);
        pageTitleView.setText(bundle.getString("pageTitle"));

        //刷新
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getStoreList(false);
            }

        });

        //跳转
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if(currentTime - lastClickTime > 1000){
                    Store store = storeList.get(arg2 - 1);
                    Log.v(TAG, "点击到的id：" + store.getId());
                    Intent intent = new Intent(StoreListActivity.this, StoreWebActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", store.getId());
                    bundle.putString("pageTitle", "店铺详情");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    lastClickTime = currentTime;
                }
            }
        });

        //初始化加载数据
        final ListView actualListView = mPullRefreshListView.getRefreshableView();
        registerForContextMenu(actualListView);
        storeListAdapter = new StoreListAdapter(this);
        actualListView.setAdapter(storeListAdapter);
//        actualListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        //标题事件
        findViewById(R.id.titleBar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actualListView.setSelection(0);
            }
        });

    //状态页面
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                initData();
            }
        });
    emptyView = (EmptyMessageView) findViewById(R.id.container_empty);

        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        if (!CommonUtil.checkNetWork()) {
            errorView.setVisibility(View.VISIBLE);
            loadMessageView.setVisibility(View.INVISIBLE);
        }

    }

    private void initData() {
        getStoreList(true);
    }


    /**
     * 获取店铺促销列表
     */
    private void getStoreList(final boolean isRefresh) {

        if (isRefresh) {
            offset = 0;
        }
        Log.v(TAG, "offset:" + offset);

        String url = InterfaceConstant.INTERFACE_HOST + apiUrl + "/" + offset + "/" + LIMIT;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Store> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Store>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        if (isRefresh) {
                            storeList.clear();
                        }
                        storeList.addAll(resultList);
                        offset = storeList.size();
                        storeListAdapter.setmData(storeList);
                        storeListAdapter.notifyDataSetChanged();

                        emptyView.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "获取店铺促销数据成功：" + dataJsonStr);
                } else {
                    if (!isRefresh) {
                        Toast.makeText(StoreListActivity.this, ToastMessage.NOT_FOUND_STORE_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        storeList.clear();
                        storeListAdapter.notifyDataSetChanged();
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
}
