package com.seastar.wasai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.PushExraMsg;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.StartActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import java.util.Date;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyJpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private long lastClickTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            PushExraMsg exraMsg;
            if (StringUtil.isNotEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                exraMsg = gson.fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), PushExraMsg.class);
                if (exraMsg.getType() == TypeConstant.USER) {
                    PreferencesWrapper preferencesWrapper = new PreferencesWrapper(MyApplication.getContextObject());
                    preferencesWrapper.setBooleanValueAndCommit("hasNewOrderNotification", true);
                    Intent myIntent = new Intent();
                    myIntent.putExtra("type",TypeConstant.USER);
                    myIntent.setAction("com.seastar.wasai.views.UserActivity");
                    context.sendBroadcast(myIntent);//发送广播
                } else if (exraMsg.getType() == TypeConstant.WALLET_NOTIFICATION) {
                    PreferencesWrapper preferencesWrapper = new PreferencesWrapper(MyApplication.getContextObject());
                    preferencesWrapper.setBooleanValueAndCommit("hasNewMoneyNotification", true);
                    Intent myIntent = new Intent();
                    myIntent.putExtra("type",TypeConstant.WALLET_NOTIFICATION);
                    myIntent.setAction("com.seastar.wasai.views.UserActivity");
                    context.sendBroadcast(myIntent);//发送广播
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
//        	//打开自定义的Activity
            PushExraMsg exraMsg = null;
            boolean needForward = false;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            if (StringUtil.isNotEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                exraMsg = gson.fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), PushExraMsg.class);
                if (exraMsg != null) {
                    needForward = true;
                }
            }

            long currentTime = new Date().getTime();
            if (currentTime - lastClickTime > 1000) {
                if (needForward) {
                    if (exraMsg.getIsNeedLogin() != 0 && !MyApplication.isLogin()) {
                        Intent forwardIntent = new Intent(context, LoginActivity.class);
                        forwardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(forwardIntent);
                    } else {
                        CommonUtil.pushForwardCommon(context, exraMsg.getType(), exraMsg.getLink(), exraMsg.getTitle());
                    }
                } else {
                    Intent i = new Intent(context, StartActivity.class);
                    i.putExtras(bundle);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(i);
                }
                lastClickTime = currentTime;
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
    }
}
