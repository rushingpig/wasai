package com.seastar.wasai.utils;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangteng on 2015/6/8.
 */
public abstract class MyReqSucessListener implements Response.Listener<JSONObject> {
    public static final String TAG = "MyReqSucessListener";
    public static final int SUCCESS = 0;
    public static final int DATA_NOT_FOUND = 10003;

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.getInt("code") == SUCCESS) {
                if(response.has("data")){
                    doResponse(response.getString("data"));
                }else{
                    doResponse(null);
                }
            }else if(response.getInt("code") == DATA_NOT_FOUND){
                doResponse(null);
                Log.w(TAG, "无数据返回：" + response.toString());
            } else {
                doErrorResponse(response);
            }
        } catch (JSONException e) {
            Toast.makeText(MyApplication.getContextObject(), ToastMessage.ERROR, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Json解析错误：" + response.toString());
        }
    }

    public abstract void doResponse(String dataJsonStr);
    public void doErrorResponse(JSONObject response) throws JSONException {
        Toast.makeText(MyApplication.getContextObject(), ToastMessage.ERROR, Toast.LENGTH_SHORT).show();
        Log.w(TAG, "接口返回错误：" + response.toString());
    }
}
