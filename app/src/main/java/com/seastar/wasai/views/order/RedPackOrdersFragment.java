package com.seastar.wasai.views.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.BackOrder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.ListResponse;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.RedPackOrder;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.BackOrderListAdapter;
import com.seastar.wasai.views.adapters.RedPackOrderListAdapter;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RedPackOrdersFragment extends ListFragment {

    private List<RedPackOrder> orderList = new ArrayList<>();
    private RedPackOrderListAdapter mAdapter;
    private PullToRefreshListView listView;
    private View haveNoDataMsgView;
    private View contextView = null;
    private long lastClickTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.fragment_red_pack_order_list, null);
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        haveNoDataMsgView = contextView.findViewById(R.id.no_data_msg);

        listView = (PullToRefreshListView) contextView.findViewById(R.id.pull_refresh_list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAdapter = new RedPackOrderListAdapter(contextView.getContext(), orderList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    RedPackOrder order = orderList.get(position - 1);
                    Intent i = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", order.oId);
                    i.putExtras(bundle);
                    i.setClass(contextView.getContext(), RedPackOrderDetailActivity.class);
                    startActivity(i);
                    lastClickTime = currentTime;
                }
            }
        });
        getOrderList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getOrderList() {
        String url = InterfaceConstant.INTERFACE_HOST + "/activity/redenvelope/list/" + MyApplication.getCurrentUser().getUuid();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<RedPackOrder> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<RedPackOrder>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        orderList.clear();
                        orderList.addAll(resultList);
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取红包订单列表数据成功：" + dataJsonStr);
                } else {
                    haveNoDataMsgView.setVisibility(View.VISIBLE);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
