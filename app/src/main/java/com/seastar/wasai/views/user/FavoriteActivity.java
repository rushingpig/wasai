package com.seastar.wasai.views.user;

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

import com.seastar.wasai.R;
import com.seastar.wasai.views.astuetz.PagerSlidingTabStrip;

public class FavoriteActivity extends FragmentActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;
    private DisplayMetrics dm = null;
    private UserGuideListFragment mGuideListFragment = null;
    private UserItemListFragment mProductListFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
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
    }

    private void setupTabs() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.titlebackground));
        tabs.setSelectedTextColor(getResources().getColor(R.color.titlebackground));
        tabs.setTabBackground(0);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"喜欢的攻略", "喜欢的商品"};

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
                    if (mGuideListFragment == null) {
                        mGuideListFragment = new UserGuideListFragment();
                    }
                    return mGuideListFragment;
                case 1:
                    if (mProductListFragment == null) {
                        mProductListFragment = new UserItemListFragment();
                    }

                    return mProductListFragment;
                default:
                    return null;
            }
        }
    }
}
