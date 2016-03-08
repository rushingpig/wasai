package com.seastar.wasai.views.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.FanliToBeAvailableEntity;
import com.seastar.wasai.Entity.FanliToBeAvailableListEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.FanliWalletAdapter;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.order.BackOrderDetailActivity;
import com.seastar.wasai.views.order.OrderDetailActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 * 返利钱包
 */
public class FanLiWalletActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mBack;
    private TextView mTitleName;
    private RelativeLayout mWenti;
    private View mWentiLine;
    private TextView mWentiText;
    private ImageView mWentiArrow;
    private boolean imageViewBgIsDown = true;
    private FanliWalletAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private String toBeAvailAmountText = "";
    private String toBeAvailWCoinText = "";
    private TextView billPriceTop;
    private LoadMessageView loadMessageView;
    private SimpleMessageView simpleMessageView;
    private LinearLayout emptyLl;

    private String extraStr = "";
    private List<FanliToBeAvailableListEntity> toBeAvailableList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fanli_wallet_activity);
        Bundle bundle;
        bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle != null) {
            toBeAvailAmountText = bundle.getString("toBeAvailAmountText");
            toBeAvailWCoinText = bundle.getString("toBeAvailWCoinText");

        }
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mWenti = (RelativeLayout) findViewById(R.id.wenti_relativelayout);
        mWentiLine = findViewById(R.id.wenti_line);
        mWentiText = (TextView) findViewById(R.id.wenti_text);
        mWentiArrow = (ImageView) findViewById(R.id.wenti_arrow);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        billPriceTop = (TextView) findViewById(R.id.bill_price_top);
        emptyLl = (LinearLayout) findViewById(R.id.empty_ll);
        simpleMessageView = (SimpleMessageView) findViewById(R.id.container_error);
        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FanliToBeAvailableListEntity entity = toBeAvailableList.get(position - 1);
                Intent mIntent = new Intent();
                if(Double.parseDouble(entity.fanli) > 0){
                    mIntent.setClass(FanLiWalletActivity.this, OrderDetailActivity.class);
                }else{
                    mIntent.setClass(FanLiWalletActivity.this, BackOrderDetailActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putString("itemId",entity.itemId);
                bundle.putString("orderId",entity.orderId);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });
    }

    private void initData() {
        mTitleName.setText("近期将存入");
        billPriceTop.setText(" " + toBeAvailAmountText);
        mAdapter = new FanliWalletAdapter(this);
        mPullToRefreshListView.setAdapter(mAdapter);
        getTobeavailListData("down", "");

    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mWenti.setOnClickListener(this);
        simpleMessageView.setOnClickListener(this);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(FanLiWalletActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getTobeavailListData("down", "");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(FanLiWalletActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getTobeavailListData("up", extraStr);
            }

        });
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton: {
                finish();
            }
            break;
            case R.id.wenti_relativelayout: {
                isShowWentiView();
            }
            break;
            case R.id.container_error: {
                simpleMessageView.setVisibility(View.GONE);
                loadMessageView.setVisibility(View.VISIBLE);
                getTobeavailListData("down", "");
            }
            break;
        }
    }

    private void isShowWentiView() {
        if (imageViewBgIsDown) {
            mWentiArrow.setBackgroundResource(R.drawable.sx_up);
            mWentiLine.setVisibility(View.VISIBLE);
            mWentiText.setVisibility(View.VISIBLE);
            imageViewBgIsDown = false;
        } else {
            mWentiArrow.setBackgroundResource(R.drawable.sx_down);
            mWentiLine.setVisibility(View.GONE);
            mWentiText.setVisibility(View.GONE);
            imageViewBgIsDown = true;
        }
    }


    private void getTobeavailListData(String gesture, String extra) {
        final boolean isRefresh;
        isRefresh = !gesture.equals("up");
        String url = InterfaceConstant.FANLI_TOBEAVAIL + "uuid=" + MyApplication.getCurrentUser().getUuid() +
                "&gesture=" + gesture + "&extra=" + extra;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    FanliToBeAvailableEntity resultList = gson.fromJson(dataJsonStr, FanliToBeAvailableEntity.class);
                    if (resultList != null && (resultList.list.size()) > 0) {
                        if (isRefresh) {
                            toBeAvailableList.clear();
                        }
                        toBeAvailableList.addAll(resultList.list);
                        mAdapter.setData(toBeAvailableList);
                        mAdapter.notifyDataSetChanged();
                        extraStr = resultList.extra;
                    } else {
                        if (!isRefresh && toBeAvailableList.size() > 0) {
                            GeneralUtil.showToastShort(FanLiWalletActivity.this, ToastMessage.NOT_FOUND_DATA);
                        } else {
                            toBeAvailableList.clear();
                            mAdapter.setData(toBeAvailableList);
                            mAdapter.notifyDataSetChanged();
                            emptyLl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    emptyLl.setVisibility(View.VISIBLE);
                }
                mPullToRefreshListView.onRefreshComplete();
                loadMessageView.setVisibility(View.GONE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, simpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
