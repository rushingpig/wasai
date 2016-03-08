package com.seastar.wasai.views.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.views.astuetz.PagerSlidingTabStrip;
import com.seastar.wasai.views.base.BaseActivity;

/**
 * Created by Jamie on 2015/7/9.
 * 收支账单
 */
public class BillPaymentsActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mBack = null;
    private TextView mTitleName;
    private PagerSlidingTabStrip tabs = null;
    private ViewPager mViewPager = null;
    private DisplayMetrics dm = null;
    private PagerAdapterImp mPagerAdapter = null;
    private IncomeFragment mIncomeFragment;
    private ExpenditureFragment mExpenditureFragment;
    private String fanliText = "";
    private String wcoinText = "";
    private TextView billPriceTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_payments_activity);
        Bundle bundle;
        bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle != null) {
            fanliText = bundle.getString("fanliText");
            wcoinText = bundle.getString("wcoinText");

        }
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        dm = getResources().getDisplayMetrics();
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        billPriceTop = (TextView) findViewById(R.id.bill_price_top);
    }

    private void initData() {
        mTitleName.setText("收支账单");
        mPagerAdapter = new PagerAdapterImp(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        tabs.setViewPager(mViewPager);
        setTabsValue();
        billPriceTop.setText(" " + fanliText);
    }

    private void setListener() {
        mBack.setOnClickListener(this);
    }


    private void setTabsValue() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.titlebackground));
        tabs.setSelectedTextColor(getResources().getColor(R.color.titlebackground));
        tabs.setTabBackground(0);
    }

    @Override
    public void finishActivity() {

    }


    public class PagerAdapterImp extends FragmentPagerAdapter {
        public PagerAdapterImp(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"收入记录", "支出记录"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mIncomeFragment == null) {
                        mIncomeFragment = new IncomeFragment();
                    }
                    return mIncomeFragment;
                case 1:
                    if (mExpenditureFragment == null) {
                        mExpenditureFragment = new ExpenditureFragment();
                    }
                    return mExpenditureFragment;
                default:
                    return null;
            }
        }
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
