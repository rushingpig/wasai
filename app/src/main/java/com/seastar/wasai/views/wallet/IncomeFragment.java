package com.seastar.wasai.views.wallet;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.IncomeEntity;
import com.seastar.wasai.Entity.IncomeListEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.WalletMainEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.IncomeFragmentAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 */
public class IncomeFragment extends Fragment{
    private Activity activity;
    private PullToRefreshListView pullToRefreshListView;
    private IncomeFragmentAdapter mAdapter;
    private LoadMessageView loadMessageView;
    private SimpleMessageView simpleMessageView;
    private LinearLayout emptyLl;
    private String extraStr = "";
    private List<IncomeListEntity> mIncomeListEntityList = new ArrayList<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.income_fragment,null);
            initView(view);
            initData();
            setListener();
        }
        return view;
    }

    private void initView(View view) {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.income_pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        simpleMessageView = (SimpleMessageView) view.findViewById(R.id.container_error);
        emptyLl = (LinearLayout) view.findViewById(R.id.empty_data_ll);
    }
    private void initData(){
        mAdapter = new IncomeFragmentAdapter(activity);
        pullToRefreshListView.setAdapter(mAdapter);
    }
    private void setListener(){
        simpleMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleMessageView.setVisibility(View.GONE);
                loadMessageView.setVisibility(View.VISIBLE);
                getIncomeListData("down", "");
            }
        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getIncomeListData("down","");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(activity, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getIncomeListData("up",extraStr);
            }

        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            getIncomeListData("down","");
        }
    }

    private void getIncomeListData(String gesture,String extra) {
        final boolean isRefresh ;
        isRefresh = !gesture.equals("up");
        String url = InterfaceConstant.FANLI_INCOME + "uuid=" + MyApplication.getCurrentUser().getUuid() +
        "&gesture=" + gesture + "&extra=" + extra;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    IncomeEntity resultList =  gson.fromJson(dataJsonStr,IncomeEntity.class);
                    if (resultList != null && (resultList.list.size()) >0) {
                        if(isRefresh){
                            mIncomeListEntityList.clear();
                        }
                        mIncomeListEntityList.addAll(resultList.list);
                        mAdapter.setData(mIncomeListEntityList);
                        mAdapter.notifyDataSetChanged();
                        extraStr = resultList.extra;
                    }else {
                        if (!isRefresh && mIncomeListEntityList.size() > 0) {
                            GeneralUtil.showToastShort(activity, ToastMessage.NOT_FOUND_DATA);
                        } else {
                            mIncomeListEntityList.clear();
                            mAdapter.setData(mIncomeListEntityList);
                            mAdapter.notifyDataSetChanged();
                            emptyLl.setVisibility(View.VISIBLE);
                        }
                    }
                 }else {
                    emptyLl.setVisibility(View.VISIBLE);
                }
                pullToRefreshListView.onRefreshComplete();
                loadMessageView.setVisibility(View.GONE);
                Log.d(TAG,"加载收入数据成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, simpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
