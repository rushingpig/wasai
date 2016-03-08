package com.seastar.wasai.views.waitingdlg;

import android.app.ProgressDialog;
import android.content.Context;
public class WaitingDlg{
	Context context = null;
	public WaitingDlg(Context con){
		context = con;
	}
	
	ProgressDialog myProgressDialog = null;
	
	public void showWaitingDlg(boolean flag){
		if(flag){
			if(myProgressDialog == null){
				myProgressDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);
				myProgressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);//设置进度条风格，风格为圆形，旋转的
				myProgressDialog.setMessage("请稍等...");	//设置ProgressDialog 提示信息
				myProgressDialog.setIndeterminate(true);	//设置ProgressDialog 的进度条是否不明确
				myProgressDialog.setCancelable(true);	//设置ProgressDialog 是否可以按退回按键取消
			}
			myProgressDialog.show();
		}
		else{
			if(myProgressDialog != null){
				myProgressDialog.dismiss();
			}
		}			
	}
}