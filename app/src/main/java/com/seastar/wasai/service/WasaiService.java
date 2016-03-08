package com.seastar.wasai.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ShoppingGuideArticlesEntity;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

/**
 * 定时上传用户行为日志 后台服务
 * 
 * @author Jamie
 * 
 */
public class WasaiService extends Service {
	public static final String TAG = "LogService";
	private UserActionTask mUserActionTask;

	private Timer mTimer = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (mTimer == null) {
			mTimer = new Timer();
		}
		mUserActionTask = new UserActionTask();
		mTimer.scheduleAtFixedRate(mUserActionTask, 10000, 50000); // 每5分钟请求一个
	}

	private class UserActionTask extends TimerTask {

		@Override
		public void run() {
			initData();
		}
	}
	private void initData() {
		ArrayList<UserActionOfGuide> guide = WasaiContentProviderUtils.getInstance(getApplicationContext()).queryUserActionMessage();
		ArrayList<ShoppingGuideArticlesEntity> shoppingGuide = WasaiContentProviderUtils.getInstance(getApplicationContext()).queryUserActionOfShoppingGuideArticlesData();
		if(guide.size() > 0 || shoppingGuide.size() >0){
			Gson  gson = new Gson();
			String	stay_time = "\"stay_time\""+":"+ gson.toJson(guide);
			String guide_forward = "\"guide_forward\"" +":"+ gson.toJson(shoppingGuide);
			userActionPostData("{"+ stay_time+","+ guide_forward +"}");
		}



	}
	private void userActionPostData(String data){
		String url = InterfaceConstant.USER_ACTION_LOG;
		Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
			@Override
			public void doResponse(String dataJsonStr) {
				WasaiContentProviderUtils.getInstance(getApplicationContext()).deleteAllUserActionMessage();
				WasaiContentProviderUtils.getInstance(getApplicationContext()).deleteAllUserActionOfShoppingGuideArticlesData();
			}
		};
		MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("info", data);
		MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
	}


	@Override
	public void onDestroy() {
		if(mTimer != null){
			mTimer.cancel();
		}
		super.onDestroy();
	}
	
}
