package com.seastar.wasai.utils.networkchange;

import com.seastar.wasai.AppContext;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;


/**
 * @author Jamie
 */
public class NetworkMonitor {

	private final String TAG = NetworkMonitor.class.getSimpleName();

	private static NetworkMonitor mInstance = null;

	//（0、未知；1、2G； 2： 3G； 3、wifi）
	public static final int CONNECT_TYPE_WIFI = 3;
	public static final int CONNECT_TYPE_MOBILE_3G = 2;
	public static final int CONNECT_TYPE_MOBILE_2G = 1;
	public static final int CONNECT_TYPE_NO_NETWORK = 0;
	public static final int CONNECT_TYPE_4G =4;
	
	private int mNetState = CONNECT_TYPE_NO_NETWORK;

	private NetworkMonitor() {
		getNetworkState();
	}

	public static NetworkMonitor getInstance() {
		if (mInstance == null) {
			mInstance = new NetworkMonitor();
		}
		return mInstance;
	}

	/**
	 * 启动时获取网络状态
	 */
	private void getNetworkState() {
		ConnectivityManager cm = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			networkConnectTypeChanged(activeNetInfo);
//			TLog.v(TAG, activeNetInfo.toString());
		}
	}

	/**
	 * 网络状态切换回调
	 * 
	 * @param ctx
	 * @param intent
	 */
	protected void onNetworkChanged(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			NetworkInfo activeNetInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (activeNetInfo != null && activeNetInfo.getState() == State.CONNECTED) {
				networkConnectTypeChanged(activeNetInfo);
//				TLog.v(TAG, "onNetworkChanged:" + activeNetInfo.toString());
			}

			if (activeNetInfo == null) {
				mNetState = CONNECT_TYPE_NO_NETWORK;
			}
		}
	}

	private void networkConnectTypeChanged(NetworkInfo activeNetInfo) {
		if (activeNetInfo != null) {
			ConnectivityManager conMan = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			State mobile = null; // 网络状态要判断一下，防止为空
			State wifi = null;
			if (mobileInfo != null) {
				mobile = mobileInfo.getState();
			}
			if (wifiInfo != null) {
				wifi = wifiInfo.getState();
			}
			if (wifi == State.CONNECTED) {
				mNetState = CONNECT_TYPE_WIFI;
			} else if (mobile == State.CONNECTED) {
				String subTypeName = activeNetInfo.getSubtypeName();
				System.out.println("subTypeName= " + subTypeName);
				//联通3G："HSDPA"，联通2G："EDGE"，电信3G："CDMA - EvDo rev. A"
				if (subTypeName != null) {
					subTypeName = subTypeName.toUpperCase();
					// 3G
					if (subTypeName.indexOf("UMTS") > -1 || subTypeName.indexOf("HSDPA") > -1 || subTypeName.indexOf("EVDO") > -1 || subTypeName.indexOf("SCDMA") > -1 || subTypeName.indexOf("HSPA+") > -1) {
						mNetState = CONNECT_TYPE_MOBILE_3G;
					}
					// 2.5G 2G
					else if (subTypeName.indexOf("EDGE") > -1 || subTypeName.indexOf("GPRS") > -1 || subTypeName.indexOf("CDMA") > -1) {
						mNetState = CONNECT_TYPE_MOBILE_2G;
					} else {
						mNetState = CONNECT_TYPE_4G;
					}
				}
			}
		} else {
			mNetState = CONNECT_TYPE_NO_NETWORK;
		}
	}

	public int getNetworkStateFlag() {
		return mNetState;
	}

	public String getNetworkType() {
		ConnectivityManager cm = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			String netType = activeNetInfo.getTypeName();
			return netType;
		} else {
			mNetState = CONNECT_TYPE_NO_NETWORK;
		}
		return "";
	}
}
