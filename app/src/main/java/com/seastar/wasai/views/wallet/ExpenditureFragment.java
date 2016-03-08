package com.seastar.wasai.views.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.ExpenditureEntity;
import com.seastar.wasai.Entity.ExpenditureListEntity;
import com.seastar.wasai.Entity.IncomeEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.ExpenditureFragmentAdapter;
import com.seastar.wasai.views.adapters.IncomeFragmentAdapter;
import com.seastar.wasai.views.base.BaseFragment;
import com.seastar.wasai.views.common.WalletExpenditureWebviewActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 */
public class ExpenditureFragment extends BaseFragment {
    private Activity activity;
    private PullToRefreshListView pullToRefreshListView;
    private ExpenditureFragmentAdapter mAdapter;
    private LoadMessageView loadMessageView;
    private SimpleMessageView simpleMessageView;
    private LinearLayout emptyLl;
    private String extraStr = "";
    private List<ExpenditureListEntity> mExpenditureList = new ArrayList<>();

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.bill_payments_fragment, null);
        initView1(view);
        initData1();
        setListener();
        return view;
    }

    @Override
    public void initData() {
        getExpenditureListData("down", "");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    private void initView1(View view) {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.income_pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        simpleMessageView = (SimpleMessageView) view.findViewById(R.id.container_error);
        emptyLl = (LinearLayout) view.findViewById(R.id.empty_data_ll);
    }

    private void initData1() {
        mAdapter = new ExpenditureFragmentAdapter(activity);
        pullToRefreshListView.setAdapter(mAdapter);
    }

    private void setListener() {
        simpleMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleMessageView.setVisibility(View.GONE);
                loadMessageView.setVisibility(View.VISIBLE);
                getExpenditureListData("down", "");
            }
        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getExpenditureListData("down", "");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getExpenditureListData("up", extraStr);
            }

        });
//        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent mIntent = new Intent(activity, WalletExpenditureWebviewActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("web_url",mExpenditureList.get(position-1).confirmURL);
//                mIntent.putExtras(bundle);
//                startActivity(mIntent);
//
//            }
//        });
    }

    private void getExpenditureListData(String gesture, String extra) {
        final boolean isRefresh;
        isRefresh = !gesture.equals("up");
        String url = InterfaceConstant.FANLI_WALLET_EXPENDITURE + "uuid=" + MyApplication.getCurrentUser().getUuid() +
                "&gesture=" + gesture + "&extra=" + extra;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    ExpenditureEntity resultList = gson.fromJson(dataJsonStr, ExpenditureEntity.class);
                    if (resultList != null && (resultList.list.size()) > 0) {
                        if (isRefresh) {
                            mExpenditureList.clear();
                        }
                        mExpenditureList.addAll(resultList.list);
                        mAdapter.setData(mExpenditureList);
                        mAdapter.notifyDataSetChanged();
                        extraStr = resultList.extra;
                    } else {
                        if (!isRefresh && mExpenditureList.size() > 0) {
                            GeneralUtil.showToastShort(activity, ToastMessage.NOT_FOUND_DATA);
                        } else {
                            mExpenditureList.clear();
                            mAdapter.setData(mExpenditureList);
                            mAdapter.notifyDataSetChanged();
                            emptyLl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    emptyLl.setVisibility(View.VISIBLE);
                }
                pullToRefreshListView.onRefreshComplete();
                loadMessageView.setVisibility(View.GONE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, simpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
