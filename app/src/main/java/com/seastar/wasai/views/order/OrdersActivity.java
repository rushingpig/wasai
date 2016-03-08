package com.seastar.wasai.views.order;

import android.content.Intent;
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
import com.seastar.wasai.views.common.RedPackActWebActivity;
import com.seastar.wasai.views.extendedcomponent.FindOrderDialog;
import com.seastar.wasai.views.extendedcomponent.UpgradeDialog;

public class OrdersActivity extends BaseActivity {
    private TextView mTitleName;
    private LinearLayout mBack;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    private DisplayMetrics dm = null;
    private NormalOrdersFragment normalOrdersFragment = null;
    private BackOrdersFragment backOrdersFragment = null;
    private RedPackOrdersFragment redPackOrdersFragment = null;

    private int displayPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        findViewById(R.id.where_is_my_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(OrdersActivity.this, OrderDescActivity.class);
                startActivity(intent);
            }
        });
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName.setText("返利订单");

        TextView rightBtn = (TextView) findViewById(R.id.rightButton);
        rightBtn.setText("申诉");
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindOrderDialog dialog = new FindOrderDialog(OrdersActivity.this);
                dialog.show();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null && bundle.getInt("displayPosition") > 0){
            displayPosition = bundle.getInt("displayPosition");
        }

        setupPager();
        setupTabs();
        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPager() {
        dm = getResources().getDisplayMetrics();
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        tabs.setViewPager(viewPager);
        viewPager.setCurrentItem(displayPosition);
    }

    private void setupTabs() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.titlebackground));
        tabs.setSelectedTextColor(getResources().getColor(R.color.titlebackground));
        tabs.setTabBackground(0);
    }

    @Override
    public void finishActivity() {

    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"返利订单", "返利退单","红包领取"};

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
                    if (normalOrdersFragment == null) {
                        normalOrdersFragment = new NormalOrdersFragment();
                    }
                    return normalOrdersFragment;
                case 1:
                    if (backOrdersFragment == null) {
                        backOrdersFragment = new BackOrdersFragment();
                    }
                    return backOrdersFragment;
                case 2:
                    if (redPackOrdersFragment == null) {
                        redPackOrdersFragment = new RedPackOrdersFragment();
                    }
                    return redPackOrdersFragment;
                default:
                    return null;
            }
        }
    }
}
