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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.BackOrder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.ListResponse;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.BackOrderListAdapter;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackOrdersFragment extends ListFragment {

    private List<BackOrder> orderList = new ArrayList<>();
    private BackOrderListAdapter mAdapter;
    private PullToRefreshListView listView;
    private View haveNoDataMsgView;
    private View contextView = null;
    private long lastClickTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.fragment_back_order_list, null);
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        haveNoDataMsgView = contextView.findViewById(R.id.no_data_msg);

        listView = (PullToRefreshListView) contextView.findViewById(R.id.pull_refresh_list);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAdapter = new BackOrderListAdapter(contextView.getContext(), orderList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    BackOrder order = orderList.get(position - 1);
                    Intent i = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", order.getOrderId());
                    bundle.putString("itemId", order.getItemId());
                    i.putExtras(bundle);
                    i.setClass(contextView.getContext(), BackOrderDetailActivity.class);
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
        String url = InterfaceConstant.ORDER_BACK + "?uuid=" + MyApplication.getCurrentUser().getUuid();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    ListResponse<BackOrder> orderResp = gson.fromJson(dataJsonStr,
                            new TypeToken<ListResponse<BackOrder>>() {
                            }.getType());
                    if (orderResp != null && orderResp.getList() != null && orderResp.getList().size() > 0) {
                        List<BackOrder> orders = orderResp.getList();
                        orderList.clear();
                        orderList.addAll(orders);
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取订单列表数据成功：" + dataJsonStr);
                } else {
                    haveNoDataMsgView.setVisibility(View.VISIBLE);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
