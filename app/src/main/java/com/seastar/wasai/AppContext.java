package com.seastar.wasai;

import com.seastar.wasai.views.extendedcomponent.MyApplication;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取Application对象
 * @author Jamie
 */
public class AppContext {
	
	public static DisplayMetrics md = null;
	
	public static Application get() {
		return MyApplication.getInstance();
	}
	
	public static DisplayMetrics getDM() {
		if (md == null) {
			md = new DisplayMetrics();
			WindowManager wm = (WindowManager) AppContext.get().getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(md);
		}
		return md;
	}
}


