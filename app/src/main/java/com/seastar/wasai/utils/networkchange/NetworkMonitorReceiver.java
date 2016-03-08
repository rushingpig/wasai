package com.seastar.wasai.utils.networkchange;

import com.seastar.wasai.AppContext;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;


/**
 * 网络切换广播，动态注册
 * @author Jamie
 */
public class NetworkMonitorReceiver extends BroadcastReceiver {
//	private static final String TAG = NetworkMonitorReceiver.class.getSimpleName();
	private static NetworkMonitorReceiver mInstance = null;
	private boolean isRegisteReceiver = false;

	private NetworkMonitorReceiver() {

	}

	public static NetworkMonitorReceiver getInstance() {
		if (mInstance == null) {
			mInstance = new NetworkMonitorReceiver();
		}
		return mInstance;
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		NetworkMonitor.getInstance().onNetworkChanged(ctx, intent);
	}

	public void registerReceiver() {
		try {

			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			AppContext.get().registerReceiver(this, filter);
		} catch (Exception e) {

		}
		isRegisteReceiver = true;
	}

	static public void unregisterReceiver() {
		if (mInstance != null) {
			if (mInstance.isRegisteReceiver == true) {
				AppContext.get().unregisterReceiver(mInstance);
				mInstance.isRegisteReceiver = false;
			}
		}
	}

}
