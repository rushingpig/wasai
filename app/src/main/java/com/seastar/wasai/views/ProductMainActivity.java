package com.seastar.wasai.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.utils.ui.ColumnHorizontalScrollView;
import com.seastar.wasai.views.adapters.ProductFragmentPagerAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.product.ProductFragment;
import com.seastar.wasai.views.search.SearchMainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jamie on 2015/6/16.
 */
public class ProductMainActivity extends FragmentActivity implements View.OnClickListener {

    private List<ProductCateEntity> mCategoryList = new ArrayList<ProductCateEntity>();

    private ColumnHorizontalScrollView mColumnHorizontalScrollView = null;
    private LinearLayout mRadioGroupContent;
    private ViewPager mViewPager;

    private int columnSelectIndex = 0;

    public ImageView shade_left;
    public ImageView shade_right;
    private int mScreenWidth = 0;
    private RelativeLayout rl_column;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    //    private TextView mTitleName = null;
    private TextView mSearchLl = null;

    private TextView mEmptyTextView = null;
    private LoadMessageView mLoadMessageView = null;
    private SimpleMessageView mSimpleMessageView = null;
    private PreferencesWrapper mPreferencesWrapper;

    private long lastClickTime;

    @Override
    protected void onResume() {
        super.onResume();

        //从首页点超级返过来的，需要根据category ID跳转到相应的TAB
        if (MainActivity.forwardParam != 0) {
            if (mCategoryList != null && mCategoryList.size() > 0) {
                forwardToSuperRebateTab();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCategoryList != null && mCategoryList.size() > 0) {
                            forwardToSuperRebateTab();
                        }
                    }
                }, 500);
            }
        }
    }

    private void forwardToSuperRebateTab() {
        int position = 0;
        for (int i = 0; i < mCategoryList.size(); i++) {
            int catId = mCategoryList.get(i).id;
            if (catId == MainActivity.forwardParam) {
                position = i;
                break;
            }
        }
        mViewPager.setCurrentItem(position);
        selectTab(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_index);
        mPreferencesWrapper = new PreferencesWrapper(this);
        WindowManager wm = this.getWindowManager();
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        initView();
        getCategoryList();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.columnHorizontalScrollView);
        mRadioGroupContent = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOffscreenPageLimit(0);
        mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
        mLoadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        mSimpleMessageView = (SimpleMessageView) findViewById(R.id.container_error);
//        mTitleName = (TextView) findViewById(R.id.category_page_title);
        mSearchLl = (TextView) findViewById(R.id.product_search);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
    }

    private void setListener() {
//        mTitleName.setOnClickListener(this);
        mSearchLl.setOnClickListener(this);

        mSimpleMessageView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                mSimpleMessageView.setVisibility(View.INVISIBLE);
                mLoadMessageView.setVisibility(View.VISIBLE);
                getCategoryList();
            }
        });
    }

    private void initTabColumn() {
        mRadioGroupContent.removeAllViews();
        int count = mCategoryList.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroupContent, shade_left, shade_right, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = 15;
            params.rightMargin = 15;
            TextView columnTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.top_tab_radiobutton, null);
            columnTextView.setText(mCategoryList.get(i).name);
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroupContent.getChildCount(); i++) {
                        View localView = mRadioGroupContent.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }

            });
            mRadioGroupContent.addView(columnTextView, i, params);
        }

    }

    private void selectTab(int tabPosition) {
        columnSelectIndex = tabPosition;
        for (int i = 0; i < mRadioGroupContent.getChildCount(); i++) {
            View checkView = mRadioGroupContent.getChildAt(tabPosition);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
        }
        for (int j = 0; j < mRadioGroupContent.getChildCount(); j++) {
            View checkView = mRadioGroupContent.getChildAt(j);
            boolean ischeck;
            if (j == tabPosition) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }

    private void initFragment() {
        fragments.clear();
        int count = mCategoryList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("text", mCategoryList.get(i).name);
            data.putInt("id", mCategoryList.get(i).id);
            ProductFragment pFragment = null;
            if (pFragment == null) {
                pFragment = new ProductFragment();
            }
            Log.e("info", "打开一个fragment");
//            ProductFragment pFragment = new ProductFragment();
            pFragment.setArguments(data);
            fragments.add(pFragment);
        }

        ProductFragmentPagerAdapter mAdapetr = new ProductFragmentPagerAdapter(getSupportFragmentManager());
        mAdapetr.setFragments(fragments);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }


    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

    private void getCategoryList() {
        String url = InterfaceConstant.PRODUCT_CATEGORY_LIST;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<ProductCateEntity> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<ProductCateEntity>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {

                        mCategoryList.clear();
                        mCategoryList.addAll(resultList);
                        initTabColumn();
                        initFragment();
                    }
                }
                mLoadMessageView.setVisibility(View.INVISIBLE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, mLoadMessageView, mSimpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_search:
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Intent sIntent = new Intent(this, SearchMainActivity.class);
                    mPreferencesWrapper.setIntValueAndCommit("to_search_activity",1);
                    startActivity(sIntent);
                    lastClickTime = currentTime;
                }
                break;
            case R.id.category_page_title:
//                if(pFragment.getUserVisibleHint()){
//                    pFragment.gridToTop();
//                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {//防止重叠
        //super.onSaveInstanceState(outState);
    }
}
