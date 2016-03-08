package com.seastar.wasai.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: StringUtil
 * @Description: 字符串处理工具类
 * @author 杨腾
 * @date 2015年4月8日 上午10:02:03
 */
public class StringUtil {
	public static final char UNDERLINE = '_';

	/**
	 * @Title: underlineToCamel
	 * @Description: 下划线转驼峰命名
	 * @param @param param
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String underlineToCamel(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == UNDERLINE) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	* @Title: MD5 
	* @Description: MD5加密
	* @param @param val
	* @param @return
	* @return String
	* @throws
	 */
	public static String MD5(String val){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			byte[] m = md5.digest();// 加密
			return getString(m);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
	
	public static boolean isNotEmpty(String str){
		return str != null && !"".equals(str.trim());
	}

	public static boolean isContainChineseCharacters(String str){
		Pattern p= Pattern.compile("[\u0391-\uFFE5]+");
		Matcher m=p.matcher(str);
		return m.matches();
	}
}
