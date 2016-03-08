package com.seastar.wasai.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class CheckWireLess {
	private static WifiManager wifiManager;
	private static DhcpInfo dhcpInfo;
	private static WifiInfo wifiInfo;

	// ip获取
	public static String getIp(Context context) {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// dhcpInfo = wifiManager.getDhcpInfo();
		wifiInfo = wifiManager.getConnectionInfo();
		// wifiInfo返回当前的Wi-Fi连接的动态信息
		if (wifiInfo != null) {
			int ip = wifiInfo.getIpAddress();
			return FormatIP(ip);
		} else {
			return "0.0.0.0";
		}

	}

	// 网关获取
	public static String getGateWay(Context context) {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		dhcpInfo = wifiManager.getDhcpInfo();

		// dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等
		return "dh_ip:" + FormatIP(dhcpInfo.ipAddress) + "\n" + "dh_gateway" + FormatIP(dhcpInfo.gateway);
	}

	// IP地址转化为字符串格式
	public static String FormatIP(int IpAddress) {
		return Formatter.formatIpAddress(IpAddress);
	}

}