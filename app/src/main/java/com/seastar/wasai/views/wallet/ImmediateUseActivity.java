package com.seastar.wasai.views.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.views.base.BaseActivity;

/**
 * Created by Jamie on 2015/8/24.
 */
public class ImmediateUseActivity extends BaseActivity{
    @Override
    public void finishActivity() {

    }
    private TextView mTitleName;
    private LinearLayout mBack;
    private RelativeLayout mToWithdrawalsMainActivity; //集分宝  = 1
    private RelativeLayout mToAlipayRl; //兑换到支付宝 = 2
    public static final String JIFENBAO_OR_ALIPAY = "jifenbao_or_alipay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.immediate_use_activity);
        initView();
        initData();
        setListener();
    }
    private void initView(){
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mToWithdrawalsMainActivity = (RelativeLayout) findViewById(R.id.to_withdrawals_activity);
        mToAlipayRl = (RelativeLayout) findViewById(R.id.to_withdrawals_alipay_activity);
    }
    private void initData(){
        mTitleName.setText("立即使用");
    }
    private void setListener(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToWithdrawalsMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ImmediateUseActivity.this,WithdrawalsMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(JIFENBAO_OR_ALIPAY,1);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });
        mToAlipayRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ImmediateUseActivity.this,WithdrawalsMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(JIFENBAO_OR_ALIPAY,2);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });
    }
}
