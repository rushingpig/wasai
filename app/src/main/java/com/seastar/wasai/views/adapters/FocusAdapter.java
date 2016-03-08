package com.seastar.wasai.views.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class FocusAdapter extends PagerAdapter {

    private List<View> viewlist;

    public FocusAdapter(List<View> viewlist) {
        this.viewlist = viewlist;
    }

    @Override
    public int getCount() {
        return 1000 * viewlist.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
//        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(viewlist.size() > 0){
            position %= viewlist.size();
            if (position < 0) {
                position = viewlist.size() + position;
            }
            View view = viewlist.get(position);
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            //add listeners here if necessary
            return view;
        }
        return null;
    }
}