package com.seastar.wasai.Entity;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangteng on 2015/7/14.
 */
public class MyJsonObjectRequest extends JsonObjectRequest {
    public MyJsonObjectRequest(String url, com.android.volley.Response.Listener<JSONObject> listener, com.android.volley.Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public MyJsonObjectRequest(int method, String url, JSONObject jsonRequest, com.android.volley.Response.Listener<JSONObject> listener, com.android.volley.Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("sessionid", MyApplication.getSessionId());
        return headers;
    }
}
