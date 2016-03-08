package com.seastar.wasai.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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
import com.seastar.wasai.Entity.Tag;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.ui.ColumnHorizontalScrollView;
import com.seastar.wasai.views.adapters.GuideFragmentPagerAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.guide.GuideFragment;
import com.seastar.wasai.views.search.SearchMainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jamie on 2015/6/18.
 */
public class GuideMainActivity extends FragmentActivity implements View.OnClickListener {
    private ColumnHorizontalScrollView mColumnHorizontalScrollView = null;
    private LinearLayout mRadioGroupContent;
    private ViewPager mViewPager;
    private LoadMessageView loadMessageView;
    private SimpleMessageView simpleMessageView;


    private int columnSelectIndex = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private List<Tag> tagList = new ArrayList<Tag>();
    private GuideFragment mGuideFragment = null;


    public ImageView shade_left;
    public ImageView shade_right;
    private int mScreenWidth = 0;
    private RelativeLayout rl_column;
    private PreferencesWrapper preferencesWrapper;

    private long lastClickTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_main_activity);
        preferencesWrapper = new PreferencesWrapper(this);
        WindowManager wm = this.getWindowManager();
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.guide_horizontalScrollView);
        mRadioGroupContent = (LinearLayout) findViewById(R.id.guide_radioGroup_content);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(8);
        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        simpleMessageView = (SimpleMessageView) findViewById(R.id.container_error);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
    }

    private void initData() {
        getTags();
    }

    private void setListener() {
        simpleMessageView.setOnClick(new SimpleMessageView.ICallBack() {
            @Override
            public void onClick() {
                simpleMessageView.setVisibility(View.INVISIBLE);
                loadMessageView.setVisibility(View.VISIBLE);
                initData();
            }
        });
        findViewById(R.id.guide_search).setOnClickListener(this);
    }

    private void initTabColumn() {
        mRadioGroupContent.removeAllViews();
        int count = tagList.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroupContent, shade_left, shade_right, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            params.leftMargin = 5;
//            params.rightMargin = 5;
            TextView columnTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.top_tab_radiobutton, null);
            columnTextView.setText(tagList.get(i).getName());
            if (columnSelectIndex == i) {
//                columnTextView.setChecked(true);
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
        initFragment();

    }

    private void initFragment() {
        fragments.clear();
        int count = tagList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("text", tagList.get(i).getName());
            data.putLong("id", tagList.get(i).getTagId());
            mGuideFragment = new GuideFragment();
            mGuideFragment.setArguments(data);
            fragments.add(mGuideFragment);
        }
        GuideFragmentPagerAdapter mAdapetr = new GuideFragmentPagerAdapter(getSupportFragmentManager(), fragments);
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
            ischeck = j == tabPosition;
            checkView.setSelected(ischeck);
        }
    }

    private void getTags() {
        String url = InterfaceConstant.TAG + "/list/guide";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Tag> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Tag>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        for (Tag tag : resultList) {
                            //					tagList.add(tag);
                            for (Tag subTag : tag.getTags()) {
                                subTag.setSelectable(true);
                                subTag.setName(subTag.getName());
                                tagList.add(subTag);
                                initTabColumn();
                            }
                        }
                    }
                }
                loadMessageView.setVisibility(View.INVISIBLE);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, loadMessageView, simpleMessageView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guide_search:
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Intent sIntent = new Intent(this, SearchMainActivity.class);
                    preferencesWrapper.setIntValueAndCommit("to_search_activity", 2);
                    startActivity(sIntent);
                    lastClickTime = currentTime;
                }
                break;
        }
    }
}
