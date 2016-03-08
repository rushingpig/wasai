package com.seastar.wasai.utils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import com.seastar.wasai.AppContext;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 手机相关信息
 * @author Jamie
 */
public class DeviceInfo {

	public static final String TAG = DeviceInfo.class.getName();

	// 网络状态
	public static final String UNKNWON = "unkwon";
	public static final String NOT_AVAILABLE = "not_avaible";
//	public static final String LTE = "";
	public static final String WIFI = "wifi";
	public static final String G3NET = "3gnet";
	public static final String G3WAP = "3gwap";
	public static final String UNINET = "uninet";
	public static final String UNIWAP = "uniwap";
	public static final String CMNET = "cmnet";
	public static final String CMWAP = "cmwap";
	public static final String CTNET = "ctnet";
	public static final String CTWAP = "ctwap";
	public static final String MOBILE = "4G";

	/**
	 * 手机型号
	 * 
	 * @return
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 手机android版本
	 * 
	 * @return 1.5,1.6,...,2.3、3.0 ....
	 */
	public static String getSDKVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * api版本,
	 * 
	 * @return 3,4,...,8,9,11......
	 */
	public static int getAPIVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	private static String imei = "";

	/**
	 * 获取IMEI，如果是cdma，则为MEID<br />
	 * <b>需要权限:READ_PHONE_STATE</b>
	 * 
	 * @return imei(meid),如果取不到就返回"0";
	 */
	public static String getIMEI() {
		if (!TextUtils.isEmpty(imei)) {
			return imei;
		}
		if (AppContext.get().checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
			try {
				TelephonyManager tm = (TelephonyManager) AppContext.get().getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.getDeviceId() != null) {
					imei = tm.getDeviceId();
				} else {
					imei = getMACAddress();
				}
				if (TextUtils.isEmpty(imei)) {
					imei = "0";
				}
				return imei;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// TLog.v(TAG, "需要权限:READ_PHONE_STATE");
		}
		return "0";
	}

	private static String imsi = "";

	/**
	 * 获取IMSI
	 */
	public static String getIMSI() {
		if (!TextUtils.isEmpty(imsi)) {
			return imsi;
		}
		if (AppContext.get().checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
			try {
				TelephonyManager tm = (TelephonyManager) AppContext.get().getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.getDeviceId() != null) {
					imsi = tm.getSubscriberId();
					if (TextUtils.isEmpty(imsi)) {
						imsi = "0";
					}
				}
				return imsi;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "0";
	}

	private static String CPUSerial = "";

	/**
	 * 获取CPU序列号
	 * 
	 * @return
	 */
	public static String getCPUSerial() {
		if (!TextUtils.isEmpty(CPUSerial)) {
			return CPUSerial;
		}
		String str = null;
		InputStreamReader ir = null;
		LineNumberReader input = null;
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			ir = new InputStreamReader(pp.getInputStream(), "UTF-8");
			input = new LineNumberReader(ir);
			while ((str = input.readLine()) != null) {
				if (str.startsWith("Serial")) {
					int index = str.indexOf(":");
					if (index > -1) {
						CPUSerial = str.substring(index + 1, str.length()).trim();
					}
					break;
				}
			}
			if (TextUtils.isEmpty(CPUSerial)) {
				CPUSerial = "0";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
				if (ir != null) {
					ir.close();
					ir = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CPUSerial;
	}

	private static String getMAChex(byte b) {
		String s = "00" + Integer.toHexString(b).toUpperCase();
		int len = s.length();
		return s.substring(len - 2, len);
	}

	private static String MACAddress = "";

	/**
	 * 获取网卡的mac地址<br />
	 * <b>需要权限:ACCESS_WIFI_STATE</b>
	 * 
	 * @return MAC地址，如取不到，则返回"00-00-00-00-00-00"
	 */
	public static String getMACAddress() {
		if (!TextUtils.isEmpty(MACAddress)) {
			return MACAddress;
		}
		// 先用这种方式尝试获取，如果wifi模块从未使用过，用getHardwareAddress(2.3以上固件)方法尝试获取
		if (AppContext.get().checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == PackageManager.PERMISSION_GRANTED) {
			try {
				WifiManager wifi = (WifiManager) AppContext.get().getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				if (info != null) {

					String mac = info.getMacAddress();
					if (mac != null && mac.length() > 0) {
						MACAddress = mac;
						return mac;
					}

				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		String s = getHardwareAddress();
		if (s != null && s.length() > 0) {
			MACAddress = s;
			return s;
		}

		return "00-00-00-00-00-00";
	}

	/**
	 * 获取设备的MAC地址，适用于2.3以上固件 插了卡的设备会有ifb0,ifb1,p2p0等地址(通讯模块提供)
	 * 没有卡的wifi设备只有wlan0,etho0等地址(wifi模块提供) 这里获取第一个
	 * 
	 * @return
	 */
	public static String getHardwareAddress() {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			if (e != null) {
				Class<?> cls = Class.forName("java.net.NetworkInterface");
				// 2.3以上固件支持该方法
				Method getHardwareAddress = cls.getMethod("getHardwareAddress", new Class[] {});
				while (e.hasMoreElements()) {
					NetworkInterface net = e.nextElement();
					// byte[] mac = net.getHardwareAddress();
					Object o = getHardwareAddress.invoke(net);
					if (o instanceof byte[]) {
						byte[] mac = (byte[]) o;
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < mac.length - 1; i++) {
							sb.append(getMAChex(mac[i]));
							sb.append("-");
						}
						sb.append(getMAChex(mac[mac.length - 1]));
						// TLog.v(TAG,
						// "getHardwareAddress name:"+net.getDisplayName()+" "+sb.toString());
						return sb.toString();
					} else {
						continue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/** 获取网络信息 */
	public static String getNetStatus() {
		if (AppContext.get().checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_DENIED) {
			return UNKNWON;
		}
		ConnectivityManager connectivitymanager = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return NOT_AVAILABLE;
		}

		if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return WIFI;
		}
		String netInfo = networkinfo.getExtraInfo();
		if (netInfo == null) {
			return UNKNWON;
		}
		netInfo = netInfo.toLowerCase();
		if (netInfo.equals("cmnet")) {
			return CMNET;
		} else if (netInfo.equals("cmwap")) {
			return CMWAP;
		} else if (netInfo.equals("3gnet")) {
			return G3NET;
		} else if (netInfo.equals("3gwap")) {
			return G3WAP;
		} else if (netInfo.equals("uninet")) {
			return UNINET;
		} else if (netInfo.equals("uniwap")) {
			return UNIWAP;
		} else if (netInfo.equals("ctnet")) {
			return CTNET;
		} else if (netInfo.equals("ctwap")) {
			return CTWAP;
		} else {
			return MOBILE;
		}
	}

	/**
	 * 当前是否使用wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUsingWifi() {
		String network = getNetStatus();
		if (WIFI.equals(network)) {
			return true;
		} else {
			return false;
		}

	}

	/** 获取网络信息 */
	public static String getNetApn() {
		if (AppContext.get().checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_DENIED) {
			return UNKNWON;
		}
		ConnectivityManager connectivitymanager = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return NOT_AVAILABLE;
		}

		if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return WIFI;
		}
		String netInfo = networkinfo.getExtraInfo();
		if (netInfo == null) {
			return UNKNWON;
		}
		netInfo = netInfo.toLowerCase();
		if (netInfo.equals("cmnet")) {
			return CMNET;
		} else if (netInfo.equals("cmwap")) {
			return CMWAP;
		} else if (netInfo.equals("3gnet")) {
			return G3NET;
		} else if (netInfo.equals("3gwap")) {
			return G3WAP;
		} else if (netInfo.equals("uninet")) {
			return UNINET;
		} else if (netInfo.equals("uniwap")) {
			return UNIWAP;
		} else if (netInfo.equals("ctnet")) {
			return CTNET;
		} else if (netInfo.equals("ctwap")) {
			return CTWAP;
		} else {
			return MOBILE;
		}
	}

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	public static String getIpAddress() {
		try {
			if (WIFI.equals(getNetApn())) {
				// 如果是wifi
				return CheckWireLess.getIp(AppContext.get());
			}
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			if (en != null) {
				for (; en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "0.0.0.0";
	}

	public static String getMemory() {
		Runtime rt = Runtime.getRuntime();
		StringBuffer sb = new StringBuffer();
		sb.append("XM: ").append(rt.maxMemory());
		sb.append("TM: ").append(rt.totalMemory());
		sb.append("FM: ").append(rt.freeMemory());
		return sb.toString();
	}

	/**
	 * 获取系统信息
	 * 
	 * @return
	 */
	public static String getEnviroment() {
		StringBuilder sb = new StringBuilder();
		sb.append("sdk=").append(getSDKVersion()).append("\n");
		sb.append("api=").append(getAPIVersion()).append("\n");
		sb.append("imei=").append(getIMEI()).append("\n");
		sb.append("nw=").append(getNetApn()).append("\n");
		sb.append("ph=").append(getModel()).append("\n");
		sb.append("mac=").append(getMACAddress()).append("\n");
		sb.append("ip=").append(getIpAddress()).append("\n");
		sb.append("loc=").append(Locale.getDefault()).append("\n");
		// sb.append("stg=").append(TContext.getAvailableStorageSize() >>
		// 20).append("\n");
		// sb.append("qua=").append(DLApp.QUA_HEADER).append("\n");
		// sb.append("path=").append(DownloadPath.getFileSaveSetting(true)).append("\n");
		return sb.toString();
	}

	private static int width = 0;
	private static int height = 0;
	private static int density = 0;

	static {
		DisplayMetrics md = new DisplayMetrics();
		WindowManager wm = (WindowManager) AppContext.get().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(md);
		density = md.densityDpi;
		width = md.widthPixels;
		height = md.heightPixels;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	private static String guid = "";

	public static String getGuid() {
		if (!TextUtils.isEmpty(guid)) {
			return guid;
		}
		SharedPreferences sp = AppContext.get().getSharedPreferences("device-info", 0);
		guid = sp.getString("guid", "");
		if (TextUtils.isEmpty(guid)) {
			guid = MD5.StrMD5(System.currentTimeMillis() + "" + Runtime.getRuntime().freeMemory() + "" + getIMEI());
			sp.edit().putString("guid", guid).commit();
		}
		return guid;
	}
}
