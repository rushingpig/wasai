package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.seastar.wasai.utils.DisplayUtil;

/**
 * @projectName: Wasai
 * @packageName: com.seastar.wasai.views.extendedcomponent
 * @ClassName: WrapHeightViewPager
 * @author: 黄锦鹏
 * @data: 2015/6/27	17:33
 * @description: 自动适应高度的viewpager
 * @svnVersion: $$Rev$$
 * @svnAuthor: $$Author$$
 * @svnData: $$Date$$
 * @svnDescription: TODO
 */

public class WrapHeightViewPager extends ViewPager {

    private  Context context;
    public WrapHeightViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public WrapHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = DisplayUtil.dip2px(context,200);
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            //采用最大的view的高度。
            if (h > height)
                height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
