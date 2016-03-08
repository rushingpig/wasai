package com.seastar.wasai.views.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.RedPackOrder;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Jamie on 2015/7/7.
 */
public class RedPackOrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleName;
    private LinearLayout mBack;
    private long lastClickTime;
    private String orderId;
    private View loadingView;

    private TextView redPackIconTextView;
    private TextView orderDateView;
    private TextView orderAmountView;
    private TextView orderWayView;
    private TextView orderStatusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_pack_order_details_activity);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        orderId = bundle.getString("orderId");

        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName.setText("红包详情");
        setListener();
        initOrderView();
        getOrderDetail();
    }

    private void initOrderView() {
        loadingView = findViewById(R.id.container_load);
        redPackIconTextView = (TextView) findViewById(R.id.red_pack_icon_text);
        orderDateView = (TextView) findViewById(R.id.order_date);
        orderAmountView = (TextView) findViewById(R.id.order_amount);
        orderStatusView = (TextView) findViewById(R.id.order_status);
        orderWayView = (TextView) findViewById(R.id.order_way);
    }


    private void setListener() {
        mBack.setOnClickListener(this);
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public void onClick(View v) {
        long currentTime = new Date().getTime();
        if (currentTime - lastClickTime > 1000) {
            switch (v.getId()) {
                case R.id.leftButton:
                    finish();
                    break;
            }
        }
        lastClickTime = currentTime;
    }

    private void getOrderDetail() {
        String url = InterfaceConstant.INTERFACE_HOST + "/activity/redenvelope/" + orderId;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    RedPackOrder order = gson.fromJson(dataJsonStr, RedPackOrder.class);
                    if (order != null) {
                        redPackIconTextView.setText(order.amount + "红包");
                        orderDateView.setText(order.createAt);
                        orderAmountView.setText(order.amount);
                        orderStatusView.setText(order.statusMsg);
                        orderWayView.setText(order.typeMsg);
                    }
                    Log.d(TAG, "获取订单列表数据成功：" + dataJsonStr);
                }
                loadingView.setVisibility(View.GONE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadingView, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}

