package com.seastar.wasai.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 * Created by jamie on 2015/6/23.
 */
public class ColumnHorizontalScrollView extends HorizontalScrollView {
    private View ll_content;
    private View rl_column;
    private ImageView leftImage;
    private ImageView rightImage;
    private int mScreenWitdh = 0;
    private Activity activity;

    public ColumnHorizontalScrollView(Context context) {
        super(context);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnHorizontalScrollView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
            super.onScrollChanged(l, t, oldl, oldt);
        shade_ShowOrHide();
        if (!activity.isFinishing() && ll_content != null && leftImage != null && rightImage != null && rl_column != null) {
            if (ll_content.getWidth() <= mScreenWitdh) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        if (l == 0) {
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        if (ll_content.getWidth() - l  + rl_column.getLeft() == mScreenWitdh + ((rightImage.getRight()-rightImage.getLeft())*2)) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        if(ll_content.getWidth() - l + rl_column.getLeft() <= mScreenWitdh){
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }


        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }

    public void setParam(Activity activity, int mScreenWitdh, View paramView1, ImageView paramView2, ImageView paramView3,View paramView4) {
        this.activity = activity;
        this.mScreenWitdh = mScreenWitdh;
        ll_content = paramView1;
        leftImage = paramView2;
        rightImage = paramView3;
        rl_column = paramView4;
    }

    public void shade_ShowOrHide() {
        if (!activity.isFinishing() && ll_content != null) {
            measure(0, 0);
            //如果整体宽度小于屏幕宽度的话，那左右阴影都隐藏
            if (mScreenWitdh >= getMeasuredWidth()) {
                leftImage.setVisibility(View.GONE);
                rightImage.setVisibility(View.GONE);
            }
        } else {
            return;
        }
        //如果滑动在最左边时候，左边阴影隐藏，右边显示
        if (getLeft() == 0) {
            Log.e("info","滑动到左边");
            leftImage.setVisibility(View.GONE);
            rightImage.setVisibility(View.VISIBLE);
            return;
        }
        //如果滑动在最右边时候，左边阴影显示，右边隐藏
        if (getRight() == getMeasuredWidth() - mScreenWitdh) {
            leftImage.setVisibility(View.VISIBLE);
            rightImage.setVisibility(View.GONE);
            return;
        }
        //否则，说明在中间位置，左、右阴影都显示
        leftImage.setVisibility(View.VISIBLE);
        rightImage.setVisibility(View.VISIBLE);
    }
}
