package com.seastar.wasai.views.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.views.adapters.OrderDescListAdapter;
import com.seastar.wasai.views.base.BaseActivity;

public class OrderDescActivity extends BaseActivity implements View.OnClickListener{
    private TextView mTitleName = null;
    private LinearLayout mBack = null;
    private ExpandableListView expandableListView;
    private OrderDescListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_desc);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        String[] groupArray = new String[]{"淘宝","天猫"};
        String[][] childArray = new String[][]{{"返利规则：\n下单后15分钟（少数情况下2小时）内您可返回哇塞宝贝APP登录账号前往个人中心查看返利订单详细返利信息"},{"返利规则：\n" +
                "下单后15分钟（少数情况下2小时）内您可返回哇塞宝贝APP登录账号前往个人中心查看返利订单详细返利信息"}};
        mAdapter = new OrderDescListAdapter(this,groupArray,childArray);
        expandableListView.setAdapter(mAdapter);
        expandableListView.expandGroup(0);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mTitleName.setText("返利订单");
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mBack.setOnClickListener(this);
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
        }
    }
}
