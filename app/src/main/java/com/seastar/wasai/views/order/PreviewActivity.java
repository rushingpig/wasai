package com.seastar.wasai.views.order;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seastar.wasai.R;
import com.seastar.wasai.views.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by Jamie on 2015/8/18.
 */
public class PreviewActivity extends BaseActivity implements View.OnClickListener{
    private ImageView backImg;
    private ImageView deleteImg;
    private ViewPager viewPager;
    private PreviewViewPagerAdapter pageAdapter;
    private ArrayList<View> views;
    private ImageView mBottomImg;

    private ArrayList<String> viewList = new ArrayList<>();

    @Override
    public void finishActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_of_find_order_activity);
        initView();
        Bundle bundle = getIntent().getExtras();
        viewList = (ArrayList<String>) bundle.get("preview_url");
        inidData();

    }
    private void initView(){
        backImg = (ImageView) findViewById(R.id.preview_back_img);
        backImg.setOnClickListener(this);
        deleteImg = (ImageView) findViewById(R.id.preview_delete_img);
        viewPager = (ViewPager) findViewById(R.id.preview_viewpager);
        mBottomImg = (ImageView) findViewById(R.id.preview_bottom_img);
    }
    private void inidData(){
        views = new ArrayList<>();
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        int positionSize = viewList.size();
        for(int i=0; i < positionSize; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageURI(Uri.parse(viewList.get(i)));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(iv);
        }
        viewPager.setOnPageChangeListener(new OnPageChangeListenerImp());
        pageAdapter = new PreviewViewPagerAdapter(views);
        viewPager.setAdapter(pageAdapter);

        if(positionSize <= 1){
            mBottomImg.setImageResource(R.drawable.preview_of_1);
        }else if(positionSize <= 2){
            mBottomImg.setImageResource(R.drawable.preview_of_2_left);
        }else {
            mBottomImg.setImageResource(R.drawable.preview_img1);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.preview_back_img:
                finish();

                break;
            case R.id.preview_delete_img:


                break;
//            case R.id.preview_back_img:
//                break;
        }
    }

    public class OnPageChangeListenerImp implements ViewPager.OnPageChangeListener {

        public void onPageSelected(int page) {
            switch (page) {
                case 0:
                    if (viewList.size() <= 1) {
                        mBottomImg.setImageResource(R.drawable.preview_of_1);
                    } else if (viewList.size() <= 2) {
                        mBottomImg.setImageResource(R.drawable.preview_of_2_left);
                    } else {
                        mBottomImg.setImageResource(R.drawable.preview_img1);
                    }
                    break;
                case 1:
                    if (viewList.size() <= 2) {
                        mBottomImg.setImageResource(R.drawable.preview_of_2_right);
                    } else {
                        mBottomImg.setImageResource(R.drawable.preview_img2);
                    }
                    break;
                case 2:
                    mBottomImg.setImageResource(R.drawable.preview_img3);
                    break;
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    }


    class PreviewViewPagerAdapter extends PagerAdapter {

        private ArrayList<View> views;

        public PreviewViewPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }

            return 0;
        }


        @Override
        public Object instantiateItem(View arg0, int arg1) {

            ((ViewPager) arg0).addView(views.get(arg1), 0);

            return views.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }
    }
}
