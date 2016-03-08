package com.seastar.wasai.views.order;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.Order;
import com.seastar.wasai.Entity.OrderItem;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.RelativeDateFormat;
import com.seastar.wasai.views.adapters.BackOrderItemListAdapter;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.common.MyTaobaoOrdersWebActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.seastar.wasai.R.id.order_date;

/**
 * Created by Jamie on 2015/7/7.
 */
public class BackOrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleName;
    private LinearLayout mBack;
    private DisplayImageOptions imageDisplayOptions;

    private TextView orderStatusCreate;
    private TextView orderStatusRecord;
    private TextView orderStatusConfirm;
    private TextView orderStatusFinish;
    private TextView orderStatusCreateTime;
    private TextView orderStatusRecordTime;
    private TextView orderStatusConfirmTime;
    private TextView orderStatusFinishTime;

    private TextView orderDetailPrice;
    private TextView confirmGoods;
    private TextView orderStatusDesc;
    private TextView orderMsg;
    private TextView orderPlatform;
    private TextView orderIdView;
    private TextView orderDate;
    private TextView orderAmountView;


    private long lastClickTime;

    private String orderId;
    private String itemId;

    private Order order;

    private ListView orderListView;
    private BackOrderItemListAdapter orderItemListAdapter;
    private List<OrderItem> orderItemList = new ArrayList<>();
    private View loadingView;

    private int ORDER_TYPE = 1; //返利退单


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details_activity);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        orderId = bundle.getString("orderId");
        itemId = bundle.getString("itemId");

        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName.setText("部分退单详情");
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
        setListener();
        initOrderView();
        getOrderDetail();
    }

    private void initOrderView() {

        orderItemListAdapter = new BackOrderItemListAdapter(this, orderItemList);
        orderListView = (ListView) findViewById(R.id.order_list_view);
        View headerView = LayoutInflater.from(this).inflate(R.layout.header_order_detail_back, null, false);
        View footerView = LayoutInflater.from(this).inflate(R.layout.footer_order_detail, null, false);

        orderStatusCreate = (TextView) headerView.findViewById(R.id.order_status_create);
        orderStatusRecord = (TextView) headerView.findViewById(R.id.order_status_record);
        orderStatusConfirm = (TextView) headerView.findViewById(R.id.order_status_confirm);
        orderStatusFinish = (TextView) headerView.findViewById(R.id.order_status_finish);

        orderStatusCreateTime = (TextView) headerView.findViewById(R.id.order_status_create_time);
        orderStatusRecordTime = (TextView) headerView.findViewById(R.id.order_status_record_time);
        orderStatusConfirmTime = (TextView) headerView.findViewById(R.id.order_status_confirm_time);
        orderStatusFinishTime = (TextView) headerView.findViewById(R.id.order_status_finish_time);

        orderDetailPrice = (TextView) headerView.findViewById(R.id.order_details_price);
        confirmGoods = (TextView) headerView.findViewById(R.id.confirm_goods);
        confirmGoods.setOnClickListener(this);
        orderStatusDesc = (TextView) headerView.findViewById(R.id.order_status_desc);
        orderMsg = (TextView) headerView.findViewById(R.id.order_msg);

        orderPlatform = (TextView) footerView.findViewById(R.id.order_platform);
        orderIdView = (TextView) footerView.findViewById(R.id.order_id);
        orderDate = (TextView) footerView.findViewById(order_date);
        orderAmountView = (TextView) footerView.findViewById(R.id.order_amount);

        orderListView.addHeaderView(headerView);
        orderListView.addFooterView(footerView);

        orderListView.setAdapter(orderItemListAdapter);

        loadingView = findViewById(R.id.container_load);

        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= orderItemList.size()) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("confirmURL", order.getConfirmURL());
                    intent.putExtras(bundle);
                    intent.setClass(BackOrderDetailActivity.this, MyTaobaoOrdersWebActivity.class);
                    startActivity(intent);
                }
            }
        });

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
                case R.id.confirm_goods: {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("confirmURL", order.getConfirmURL());
                    intent.putExtras(bundle);
                    intent.setClass(BackOrderDetailActivity.this, MyTaobaoOrdersWebActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            lastClickTime = currentTime;
        }
    }

    private void getOrderDetail() {
        String url = InterfaceConstant.ORDER_DETAIL + "?itemId=" + itemId + "&orderId=" + orderId + "&type=1";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    Order order = gson.fromJson(dataJsonStr, Order.class);
                    if (order != null) {
                        order.setConfirmURL(wrapConfirmURL(order.getStatus()));
                        BackOrderDetailActivity.this.order = order;
                        initOrder();
                        Set<String> titleSet = new HashSet<>();
                        for (OrderItem orderItem : order.getItems()) {
                            titleSet.add(orderItem.getItem_title());
                        }
                        for (String title : titleSet) {
                            OrderItem tempItem = new OrderItem();
                            for (OrderItem orderItem : order.getItems()) {
                                if (title.equals(orderItem.getItem_title())) {
                                    tempItem.setItem_image_url(orderItem.getItem_image_url());
                                    tempItem.setItem_title(orderItem.getItem_title());
                                    tempItem.setItem_num(tempItem.getItem_num() + orderItem.getItem_num());
                                }
                            }
                            orderItemList.add(tempItem);
                        }
                        orderItemListAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取订单列表数据成功：" + dataJsonStr);
                }
                loadingView.setVisibility(View.GONE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadingView, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void initOrder() {
        switch (order.getStatus()) {
            case Constant.STATUS_ORDER_RECORD:
                orderStatusCreate.setSelected(true);
                orderStatusCreate.setTextColor(getResources().getColor(R.color.white));
                orderStatusCreateTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                orderStatusRecord.setSelected(true);
                orderStatusRecord.setTextColor(getResources().getColor(R.color.white));
                orderStatusRecordTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                confirmGoods.setVisibility(View.VISIBLE);
                break;
            case Constant.STATUS_ORDER_CONFIRM:
                orderStatusCreate.setSelected(true);
                orderStatusCreate.setTextColor(getResources().getColor(R.color.white));
                orderStatusCreateTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                orderStatusRecord.setSelected(true);
                orderStatusRecord.setTextColor(getResources().getColor(R.color.white));
                orderStatusRecordTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                orderStatusConfirm.setSelected(true);
                orderStatusConfirm.setTextColor(getResources().getColor(R.color.white));
                orderStatusConfirmTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime2()), "MM月dd号"));
                break;
            case Constant.STATUS_ORDER_FINISH:
                orderStatusCreate.setSelected(true);
                orderStatusCreate.setTextColor(getResources().getColor(R.color.white));
                orderStatusCreateTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                orderStatusRecord.setSelected(true);
                orderStatusRecord.setTextColor(getResources().getColor(R.color.white));
                orderStatusRecordTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                orderStatusConfirm.setSelected(true);
                orderStatusConfirm.setTextColor(getResources().getColor(R.color.white));
                orderStatusConfirmTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime2()), "MM月dd号"));
                orderStatusFinish.setSelected(true);
                orderStatusFinish.setTextColor(getResources().getColor(R.color.white));
                orderStatusFinishTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime3()), "MM月dd号"));
                break;
            case Constant.STATUS_ORDER_CLOSED:
                orderStatusCreate.setEnabled(false);
                orderStatusCreate.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime1() != 0) {
                    orderStatusCreateTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                }

                orderStatusRecord.setEnabled(false);
                orderStatusRecord.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime1() != 0) {
                    orderStatusRecordTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                }

                orderStatusConfirm.setEnabled(false);
                orderStatusConfirm.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime2() != 0) {
                    orderStatusConfirmTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime2()), "MM月dd号"));
                }

                orderStatusFinish.setEnabled(false);
                orderStatusFinish.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime3() != 0) {
                    orderStatusFinishTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime3()), "MM月dd号"));
                }
                findViewById(R.id.line1).setBackgroundResource(R.color.gray);
                findViewById(R.id.line2).setBackgroundResource(R.color.gray);
                findViewById(R.id.line3).setBackgroundResource(R.color.gray);
                break;
            case Constant.STATUS_ORDER_BACK:
                orderStatusCreate.setEnabled(false);
                orderStatusCreate.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime1() != 0) {
                    orderStatusCreateTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                }

                orderStatusRecord.setEnabled(false);
                orderStatusRecord.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime1() != 0) {
                    orderStatusRecordTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime1()), "MM月dd号"));
                }

                orderStatusConfirm.setEnabled(false);
                orderStatusConfirm.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime2() != 0) {
                    orderStatusConfirmTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime2()), "MM月dd号"));
                }

                orderStatusFinish.setEnabled(false);
                orderStatusFinish.setTextColor(getResources().getColor(R.color.white));
                if (order.getStatusTime3() != 0) {
                    orderStatusFinishTime.setText(RelativeDateFormat.dateToStr(new Date(order.getStatusTime3()), "MM月dd号"));
                }
                findViewById(R.id.line1).setBackgroundResource(R.color.gray);
                findViewById(R.id.line2).setBackgroundResource(R.color.gray);
                findViewById(R.id.line3).setBackgroundResource(R.color.gray);
                break;
        }

        orderDetailPrice.setText((Double.parseDouble(order.getFanli()) / 100) + "");
        orderStatusDesc.setText(CommonUtil.wrapOrderStatus(order.getStatus(), ORDER_TYPE));
        orderMsg.setText(order.getMsg());
        orderPlatform.setText(order.getPlatform());
        orderIdView.setText(orderId);
        orderDate.setText(RelativeDateFormat.dateToStr(new Date(order.getTime()), "yyyy-MM-dd"));
        orderAmountView.setText((Double.parseDouble(order.getOrderAmount()) / 100) + "");
    }

    private String wrapConfirmURL(int status) {
        String statusStr = "http://h5.m.taobao.com/awp/mtb/mtb.htm?#!/awp/mtb/olist.htm?sta=";
        switch (status) {
            case 0:
                statusStr += "1";
                break;
            case 1:
                statusStr += "2";
                break;
            case 2:
                statusStr += "2";
                break;
            case 3:
                statusStr += "4";
                break;
        }
        return statusStr;
    }
}
