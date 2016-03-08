package com.seastar.wasai.views.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.ImageUtil;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.astuetz.SearchPagerSlidingTabStrip;
import com.seastar.wasai.views.base.BaseActivity;

/**
 * Created by Jamie on 2015/6/9.
 */
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {
    private SearchPagerSlidingTabStrip tabs = null;
    private ViewPager mViewPager = null;
    private AllProductListFragment mAiTaobaoFragment = null;
    private GuideListFragment mGuideListFragment = null;
    private ProductListFragment mProductListFragment = null;
    private PagerAdapterImp mPagerAdapter = null;
    private DisplayMetrics dm = null;
    public static String mKeyword = "";

    private EditText mInputEt = null;
    private ImageView mDeleteImg = null;
    private TextView mSearchOrCancelTv = null;
    private PreferencesWrapper mPreferencesWrapper;
    public static String keywordRefresh = "";

    private int forwardPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        mPreferencesWrapper = new PreferencesWrapper(this);
        Bundle bundle = getIntent().getExtras();
//        bundle = savedInstanceState;
//        if (bundle == null) {
//            bundle = getIntent().getExtras();
//        }
        if (bundle != null) {
            mKeyword = bundle.getString("keyword");
            if (TextUtils.isEmpty(mKeyword)) {
                finish();
                return;
            }
            forwardPosition = bundle.getInt("position");
        }


        initView();
        initData();
        setListener();

    }

    private void initView() {
        dm = getResources().getDisplayMetrics();
        tabs = (SearchPagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new PagerAdapterImp(getSupportFragmentManager());
        mInputEt = (EditText) findViewById(R.id.search_edittext);
        mInputEt.setText(mKeyword);
        mDeleteImg = (ImageView) findViewById(R.id.delete_search);
        mSearchOrCancelTv = (TextView) findViewById(R.id.done_or_cancel_tv);

    }

    private void initData() {
        mInputEt.setSelection(mInputEt.getText().length());
        mInputEt.getSelectionStart();
        keywordRefresh = mInputEt.getText().toString();
        mViewPager.setAdapter(mPagerAdapter);
        tabs.setViewPager(mViewPager);
        setTabsValue();

        if (forwardPosition > 0) {
            mViewPager.setCurrentItem(forwardPosition);
            forwardPosition = 0;
        }
        mViewPager.setCurrentItem(mPreferencesWrapper.getIntValue("to_search_activity", 0));
    }

    private void setListener() {
        mDeleteImg.setOnClickListener(this);
        mSearchOrCancelTv.setOnClickListener(this);
        mInputEt.setOnKeyListener(onKeyListener);
        mInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keywordRefresh = s.toString();
                if (keywordRefresh.length() > 0) {
                    mDeleteImg.setVisibility(View.VISIBLE);
                } else {
                    mDeleteImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                String currentSearchText = mInputEt.getText().toString().trim();
                if (!TextUtils.isEmpty(currentSearchText)) {
                    if (TextUtils.isEmpty(currentSearchText)) {
                        GeneralUtil.showToastShort(SearchResultActivity.this, getResources().getString(R.string.input_search_keyword));
                    } else {
                        refreshSearchData(currentSearchText);
                    }

                } else {
                    GeneralUtil.showToastShort(SearchResultActivity.this, getResources().getString(R.string.input_search_keyword));
                }

                return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_search:
                mInputEt.setText("");
                break;
            case R.id.done_or_cancel_tv:
                String keyword = mInputEt.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    GeneralUtil.showToastShort(SearchResultActivity.this, getResources().getString(R.string.input_search_keyword));
                } else {
                    refreshSearchData(keyword);
                    mInputEt.clearFocus();
                    closeInputMethod();
                }
                break;
        }
    }

    /**
     * 关闭软键盘
     */
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(mInputEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void refreshSearchData(String keyword) {
        this.mKeyword = keyword;
        mGuideListFragment.refreshGuidePager(keyword);
        mProductListFragment.refreshProductPager(keyword);
        mAiTaobaoFragment.refreshProductPager(keyword);
        mPreferencesWrapper.setStringValueAndCommit("save_data", keyword);
        GeneralUtil.sendBroadcst(this, SearchMainActivity.NOTI_OF_DATA);
    }

    private void setTabsValue() {
        tabs.setShouldExpand(true);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, dm));
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        tabs.setIndicatorColor(getResources().getColor(R.color.titlebackground));
        tabs.setSelectedTextColor(getResources().getColor(R.color.white));
        tabs.setTextColor(getResources().getColor(R.color.titlebackground));
        tabs.setDividerColorResource(R.color.titlebackground);
        tabs.setDividerPadding(0);
        tabs.setTabBackground(0);
    }

    @Override
    public void finishActivity() {
        mPreferencesWrapper.setStringValueAndCommit("searchResultActivity_input", mInputEt.getText().toString());
    }

    public class PagerAdapterImp extends FragmentPagerAdapter {
        public PagerAdapterImp(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"全部商品", "精选商品", "攻略文章"};

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
            if (mAiTaobaoFragment == null) {
                mAiTaobaoFragment = new AllProductListFragment();
            }
            if (mGuideListFragment == null) {
                mGuideListFragment = new GuideListFragment();
            }
            if (mProductListFragment == null) {
                mProductListFragment = new ProductListFragment();
            }
            switch (position) {
                case 0:
                    return mAiTaobaoFragment;
                case 1:
                    return mProductListFragment;
                case 2:
                    return mGuideListFragment;
                default:
                    return null;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mPreferencesWrapper.setStringValueAndCommit("searchResultActivity_input", mInputEt.getText().toString());
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
