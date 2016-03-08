package com.seastar.wasai.views.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ProductCateEntity;
import com.seastar.wasai.Entity.WalletMainEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 */
public class WalletMainActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mBack = null;
    private TextView mTitleName;
    private RelativeLayout mChangjianwenti;
    private PreferencesWrapper mPreferencesWrapper;
    private TextView mToBillActivity;
    private TextView mToImmediatelyActivity;
    private TextView mToJiJiangKeYongActivity;
    //    private WalletMainEntity walletData = new WalletMainEntity();
    private LoadMessageView mLoadMessageView;
    private SimpleMessageView mSimpleMessageView;
    private TextView mFanliAmount, mWcoin, mToBeAvailAmount;
    private String fanliText = "";
    private String wcoinText = "";
    private String toBeAvailAmountText = "";
    private String toBeAvailWCoinText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_main_activity);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mChangjianwenti = (RelativeLayout) findViewById(R.id.changjian_wenti);
        mToBillActivity = (TextView) findViewById(R.id.to_fanli_Bill);
        mToImmediatelyActivity = (TextView) findViewById(R.id.to_immediately);
        mToJiJiangKeYongActivity = (TextView) findViewById(R.id.to_jijiangkeyong);
        mLoadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        mSimpleMessageView = (SimpleMessageView) findViewById(R.id.container_error);
        mFanliAmount = (TextView) findViewById(R.id.fanliAmount);
        mWcoin = (TextView) findViewById(R.id.wcoin);
        mToBeAvailAmount = (TextView) findViewById(R.id.toBeAvailAmount);

    }

    private void initData() {
        mTitleName.setText("我的钱包");
        mPreferencesWrapper = new PreferencesWrapper(this);
        if (mPreferencesWrapper.getBooleanValue("isFirstShow", true)) {
            mChangjianwenti.setVisibility(View.VISIBLE);
            mPreferencesWrapper.setBooleanValueAndCommit("isFirstShow", false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChangjianwenti.setVisibility(View.GONE);
                }
            }, 3000);
        } else {
            mChangjianwenti.setVisibility(View.GONE);
        }
        getWalletData();
    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mToBillActivity.setOnClickListener(this);
        mToImmediatelyActivity.setOnClickListener(this);
        mToJiJiangKeYongActivity.setOnClickListener(this);
        mSimpleMessageView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                mSimpleMessageView.setVisibility(View.GONE);
                mLoadMessageView.setVisibility(View.VISIBLE);
                getWalletData();
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
            case R.id.to_fanli_Bill: {
                Intent bIntent = new Intent(this, BillPaymentsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fanliText", fanliText);
                bundle.putString("wcoinText", wcoinText);
                bIntent.putExtras(bundle);
                startActivity(bIntent);
            }
            break;
            case R.id.to_immediately: {
                Intent mIntent = new Intent(this, ImmediateUseActivity.class);
                startActivity(mIntent);
            }
            break;
            case R.id.to_jijiangkeyong: {
                Intent fIntent = new Intent(this, FanLiWalletActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("toBeAvailAmountText", toBeAvailAmountText);
                bundle.putString("toBeAvailWCoinText", toBeAvailWCoinText);
                fIntent.putExtras(bundle);
                startActivity(fIntent);
            }
            break;
        }
    }

    private void getWalletData() {
        String url = InterfaceConstant.FANLI_WALLET_AMOUNT + "uuid=" + MyApplication.getCurrentUser().getUuid();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    WalletMainEntity resultList = gson.fromJson(dataJsonStr, WalletMainEntity.class);
                    if (resultList != null) {
                        setViewData(resultList);
                        fanliText = resultList.fanliAmount;
                        wcoinText = resultList.wcoin;
                        toBeAvailAmountText = resultList.toBeAvailAmount;
                        toBeAvailWCoinText = resultList.toBeAvailWCoin;

                    }
                }
                mLoadMessageView.setVisibility(View.INVISIBLE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, mLoadMessageView, mSimpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void setViewData(WalletMainEntity data) {
        mFanliAmount.setText(data.fanliAmount);
        mWcoin.setText(data.wcoin);
        mPreferencesWrapper.setStringValueAndCommit("data_wcoin",data.wcoin);
        mToBeAvailAmount.setText(data.toBeAvailAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWalletData();
    }
}
