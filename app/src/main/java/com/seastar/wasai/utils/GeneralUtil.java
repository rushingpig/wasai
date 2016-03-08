package com.seastar.wasai.utils;

import com.seastar.wasai.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.seastar.wasai.utils.ui.CustomProgressDialog;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;
/**
 * 各种工具类
 * @author Jamie
 *
 */
@SuppressLint("SimpleDateFormat")
public class GeneralUtil {
	static PreferencesWrapper mPreferencesWrapper;
	static Context mContext;
	private static CustomProgressDialog mProgressDialog = null;


	// 获取当前版本号
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	// 获取当前版本号
	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
			if (versionCode == 0) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	// 发送广播
	public static void sendBroadcst(Context ctx, String action) {
		Intent intent = new Intent(action);
		ctx.sendBroadcast(intent);
	}

	public static void sendMsgBroadcst(Context ctx, String action, String key,
			Parcelable msg) {
		Intent intent = new Intent(action);
		intent.putExtra(key, msg);
		ctx.sendBroadcast(intent);
	}

	/**
	 * 提示框
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToastLong(Context context, int text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void showToastLong(Context context, String text) {
		// Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToastShort(Context context, int text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToastShort(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	public static String bitmapToString(Bitmap bitmap, int scale) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, scale, bao);
		byte[] ba = bao.toByteArray();
		// TLog.d("Time", "oridatalen"+ba.length);
		String ret = android.util.Base64.encodeToString(ba,
				android.util.Base64.DEFAULT); // 将图片转成base64
		// TLog.d("Time", "base64len"+ret.length() * 2);
		if (ret == null) {
			ret = "";
		}
		return ret;
	}

	/**
	 * 清理缓存
	 */
	public static void clearCache(String pPath) {
		File folder = new File(pPath);
		if (folder.exists() && folder.isDirectory()) {
			File[] folders = folder.listFiles();
			for (File file : folders) {
				if (file == null)
					continue;
				if (!file.exists())
					continue;
				if (file.isDirectory()) {
					clearCache(file.getPath());
				} else if (file.isFile()) {
					file.delete();
				}
			}
		}
	}

	public static Bitmap safeDecodeBitmap(String localPath) {
		Options option = new Options();
		option.inJustDecodeBounds = true;

		Bitmap bitmapOri = BitmapFactory.decodeFile(localPath, option);

		try {
			if (option.outHeight > 1000 || option.outWidth > 1000) {
				option.inJustDecodeBounds = false;
				option.inSampleSize = 5;
				bitmapOri = BitmapFactory.decodeFile(localPath, option);
			} else {
				bitmapOri = BitmapFactory.decodeFile(localPath);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return bitmapOri;
	}

	public static String getStrDate(long cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
		// 例如：cc_time=1291778220
		re_StrTime = sdf.format(new Date(cc_time));

		return re_StrTime;

	}

	public static String getStrDate3(long cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
		// 例如：cc_time=1291778220
		re_StrTime = sdf.format(new Date(cc_time));

		return re_StrTime;

	}

	public static String getStrDate1(long cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		// 例如：cc_time=1291778220
		re_StrTime = sdf.format(new Date(cc_time));

		return re_StrTime;

	}

	public static String getStrTime(long cc_time) {
		String re_StrTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 例如：cc_time=1291778220
		re_StrTime = sdf.format(new Date(cc_time));

		return re_StrTime;

	}

	public static int getSecondsFromDate(String expireDate) {
		if (expireDate == null || expireDate.trim().equals(" "))
			return 0;
		Calendar c = Calendar.getInstance();
		int expireYear = Integer.parseInt(expireDate.substring(0, 4));
		int expireMonth = Integer.parseInt(expireDate.substring(5, 7));
		int expireDay = Integer.parseInt(expireDate.substring(8));
		c.set(expireYear, expireMonth, expireDay);
		long time1 = c.getTime().getTime();
		// c.set(1970,1, 1);
		// long time2 = c.getTime().getTime();
		return (int) (time1);
	}

	public static String getDateFromSeconds(String seconds) {
		if (seconds == null || seconds.trim().equals(" ")
				|| seconds.equals("0 "))
			return " ";
		else {
			Date date = new Date();
			date.setTime(Long.parseLong(seconds) * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
			return sdf.format(date);
		}
	}

	/**
	 * 判断是否是24小时
	 * 
	 * @author:Jamie
	 * @time:2014年10月22日 下午4:34:09
	 */
	public static boolean is24(Context ctx) {
		ContentResolver cv = ctx.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if (strTimeFormat != null && strTimeFormat.equals("24")) {// strTimeFormat某些rom12小时制时会返回null
			return true;
		} else
			return false;
	}



	/**
	 * 设置图片圆角
	 * @param bitmap
	 * @param roundPx 角度
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
    public static String getStorePath(Context context, String path) {
			// 获取SdCard状态
			String state = android.os.Environment.getExternalStorageState();
			// 判断SdCard是否存在并且是可用的
			if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
				if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
					File file = new File(android.os.Environment.getExternalStorageDirectory().getPath() + path);
					if (!file.exists()) {
						file.mkdirs();
					}
					String absolutePath = file.getAbsolutePath();
					if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
						absolutePath += "/";
					}
					return absolutePath;
				}
			}
		String absolutePath = context.getFilesDir().getAbsolutePath();
		// 加path子文件夹，有权限问题，需要提升一下chmod 777
		// File file = new File(filesDir + path);
		// if (!file.exists()) {
		// file.mkdirs();
		// }
		// String absolutePath = file.getAbsolutePath();
		if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
			absolutePath += "/";
		}
		return absolutePath;
	}
    /**
     *  自定义dialog  show
     * @param activity 
     * @param message  
     */
	public static void showProgressDialogMessage(Activity activity,String message){
		 if (mProgressDialog == null){  
			 mProgressDialog = CustomProgressDialog.createDialog(activity);  
			 mProgressDialog.setMessage(message);  
	        }  
		 mProgressDialog.setCanceledOnTouchOutside(false);
		 mProgressDialog.show();
	}
	/**
	 *  自定义dialog  show
	 * @param activity
	 */
	public static  void showProgressDialog(Activity activity){
		 if (mProgressDialog == null){  
			 mProgressDialog = CustomProgressDialog.createDialog(activity);  
			 mProgressDialog.setMessage(activity.getResources().getString(R.string.loading));  
	        }  
		 mProgressDialog.setCanceledOnTouchOutside(false);
		 mProgressDialog.show();
	}
	/**
	 *  dismiss dialog
	 */
	public static void cancelProgressDialog(){
		 if (mProgressDialog != null){  
			 mProgressDialog.dismiss();  
			 mProgressDialog = null;  
	        } 
	}
	

}
