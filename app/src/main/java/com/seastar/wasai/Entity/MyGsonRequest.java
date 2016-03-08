package com.seastar.wasai.Entity;

import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by yangteng on 2015/6/8.
 */
public class MyGsonRequest {
    public static final int TIME_OUT = 15 * 1000;
    private int method;
    private String url;
    private View errorView;
    private View loadingView;
    private PullToRefreshAdapterViewBase listView;


    public MyGsonRequest(String url, View errorView) {
        this.method = Request.Method.GET;
        this.url = url;
        this.errorView = errorView;
    }

    public MyGsonRequest(String url, View loadingView, View errorView) {
        this.method = Request.Method.GET;
        this.url = url;
        this.loadingView = loadingView;
        this.errorView = errorView;
    }

    public MyGsonRequest(String url, View loadingView, View errorView, PullToRefreshAdapterViewBase listView) {
        this.method = Request.Method.GET;
        this.url = url;
        this.loadingView = loadingView;
        this.errorView = errorView;
        this.listView = listView;
    }

    public MyGsonRequest(int method, String url, View loadingView, View errorView) {
        this.method = method;
        this.url = url;
        this.loadingView = loadingView;
        this.errorView = errorView;
    }

    public MyGsonRequest(int method, String url) {
        this.method = method;
        this.url = url;
    }


    public JsonObjectRequest getRequest(Response.Listener<JSONObject> listener) {
        MyJsonObjectRequest request = new MyJsonObjectRequest(this.url, listener, new MyErrorListener(errorView, loadingView, listView));
        request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 1, 1.0f));
        return request;
    }

    public JsonObjectRequest getRequestWithBody(Response.Listener<JSONObject> listener, final Map<String, String> requestBody) {
        JSONObject jsonObject = new JSONObject(requestBody);
        MyJsonObjectRequest request = new MyJsonObjectRequest(this.method, this.url, jsonObject, listener, new MyErrorListener(errorView, loadingView, listView));
        request.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 1, 1.0f));
        return request;
    }

}
