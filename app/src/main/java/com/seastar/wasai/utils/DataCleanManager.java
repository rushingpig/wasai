package com.seastar.wasai.utils;

import java.io.File;
import java.math.BigDecimal;

import android.content.Context;
import android.os.Environment;

import com.seastar.wasai.db.WasaiContentProviderUtils;

/**
* @ClassName: DataCleanManager 
* @Description: 缓存清除
* @author 杨腾
* @date 2015年5月12日 上午9:37:37
 */
public class DataCleanManager {

	/**
	* @Title: getTotalCacheSize 
	* @Description: 获取缓存大小
	* @param @param context
	* @param @return
	* @param @throws Exception
	* @return String
	* @throws
	 */
	public static String getTotalCacheSize(Context context){
		long cacheSize = 0;
		try {
			cacheSize = getFolderSize(context.getCacheDir());
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				cacheSize += getFolderSize(context.getExternalCacheDir());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getFormatSize(cacheSize);
	}

	/**
	* @Title: clearAllCache 
	* @Description: 清空缓存
	* @param @param context
	* @return void
	* @throws
	 */
	public static void clearAllCache(Context context) {
		WasaiContentProviderUtils.getInstance(context).deleteAllSearchHistoryData();
		deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteDir(context.getExternalCacheDir());
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir != null) {
			if(dir.isDirectory()){
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}else{
				return dir.delete();
			}
		}
		return false;
	}

	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return "0K";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

}