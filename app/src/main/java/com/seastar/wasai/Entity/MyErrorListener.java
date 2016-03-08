package com.seastar.wasai.Entity;

import android.view.View;

import com.android.volley.*;
import com.android.volley.Response;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.utils.VolleyExceptionHelper;

public class MyErrorListener implements Response.ErrorListener {
    private View errorView;
    private View loadingView;
    private PullToRefreshAdapterViewBase listView;

    public MyErrorListener(View errorView,View loadingView,PullToRefreshAdapterViewBase listView){
        this.errorView = errorView;
        this.loadingView = loadingView;
        this.listView = listView;
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        VolleyExceptionHelper.helper(error, errorView, loadingView, listView);
    }
}