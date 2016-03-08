package com.seastar.wasai.views.signin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.seastar.wasai.Entity.IntegralRecordEntity;
import com.seastar.wasai.Entity.IntegralRecordExtraEntity;
import com.seastar.wasai.Entity.IntegralRecordListEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.MyIntegralEntity;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.IntegralRecordAdapter;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie on 2015/6/23.
 */
public class IntegralRecordActivity extends BaseActivity implements View.OnClickListener{
    private TextView mTitleName;
    private LinearLayout mBack;
    private PullToRefreshListView mPullToRefreshListView;
    private IntegralRecordAdapter mAdapter;
    private List<IntegralRecordListEntity> mList = new ArrayList<IntegralRecordListEntity>();
    private SimpleMessageView mErrorView;
    private TextView mEmptyTextView;
    private LoadMessageView mLoadMessageView;
    private IntegralRecordExtraEntity mExtraEntity = new IntegralRecordExtraEntity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.integral_record_activity);
        initView();
        initData();
        setListener();

    }
    private void initView(){
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.integral_record_pull_refresh_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mLoadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
        mErrorView = (SimpleMessageView) findViewById(R.id.container_error);
    }
    private void initData(){
        mTitleName.setText("积分记录");
        mAdapter = new IntegralRecordAdapter(this);
        mPullToRefreshListView.setAdapter(mAdapter);
        getIntegralRecordList("down","");
    }
    private void setListener(){
        mBack.setOnClickListener(this);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(IntegralRecordActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getIntegralRecordList("down","");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(IntegralRecordActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                Gson gson = new Gson();
                getIntegralRecordList("up", gson.toJson(mExtraEntity));
            }

        });

    }

    @Override
    public void finishActivity() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.leftButton:
                finish();
                break;

        }
    }

    private void getIntegralRecordList(final String gesture,final String extra) {
        final boolean isRefresh ;
        isRefresh = !gesture.equals("up");
//        String url = InterfaceConstant.SIGNIN_POINTS_LIST +"c23362e0-f8c9-11e4-954c-d77c6d30a7cf" + "?gesture=" + gesture + "&extra=" + extra;
        String url = InterfaceConstant.SIGNIN_POINTS_LIST +MyApplication.getCurrentUser().getUuid() + "?gesture=" + gesture + "&extra=" + extra;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    IntegralRecordEntity resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<IntegralRecordEntity>() {
                            }.getType());
                    if (resultList != null) {
                        if(resultList.list !=null && resultList.list.size() > 0){
                            if (isRefresh) {
                                mList.clear();
                            }
                            mList.addAll(resultList.list);
                            mAdapter.setData(mList);
                            mExtraEntity = resultList.extra;
                            mAdapter.notifyDataSetChanged();
                            mEmptyTextView.setVisibility(View.INVISIBLE);
                        }else {
                            if (!isRefresh && mList.size() > 0) {
                                GeneralUtil.showToastShort(IntegralRecordActivity.this,ToastMessage.NOT_FOUND_DATA);
                            } else {
                                mList.clear();
                                mAdapter.setData(mList);
                                mAdapter.notifyDataSetChanged();
                                mEmptyTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                mLoadMessageView.setVisibility(View.INVISIBLE);
                mPullToRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, mLoadMessageView, isRefresh ? mErrorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
