package com.seastar.wasai.utils.ui;

import com.seastar.wasai.R;
import com.seastar.wasai.views.extendedcomponent.GifView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * ClassName:CustomProgressDialog <br/> 自定义progressDialog
 * @author   Jamie 
 */
public class CustomProgressDialog extends Dialog{

	 private Context context = null;  
	    private static CustomProgressDialog customProgressDialog = null;  
	      
	    public CustomProgressDialog(Context context){  
	        super(context);  
	        this.context = context;  
	    }  
	      
	    public CustomProgressDialog(Context context, int theme) {  
	        super(context, theme);  
	    }  
	      
	    public static CustomProgressDialog createDialog(Context context){  
	        customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);  
	        customProgressDialog.setContentView(R.layout.custom_progress_dialog);  
	        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
	          
	        return customProgressDialog;  
	    }  
	   
	    public void onWindowFocusChanged(boolean hasFocus){  
	          
	        if (customProgressDialog == null){  
	            return;  
	        }  
	          
	        GifView imageView = (GifView) customProgressDialog.findViewById(R.id.loadingImageView);  
//	        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();  
//	        animationDrawable.start();  
	    }  
	   
	    /** 
	     *  
	     *       setTitile 标题 
	     * @param strTitle 
	     * @return 
	     * 
	     */  
	    public CustomProgressDialog setTitile(String strTitle){  
	        return customProgressDialog;  
	    }  
	      
	    /** 
	     *  
	     *       setMessage 提示内容 
	     * @param strMessage 
	     * @return 
	     * 
	     */  
	    public CustomProgressDialog setMessage(String strMessage){  
	        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);  
	        if (tvMsg != null){  
	            tvMsg.setText(strMessage);  
	        }  
	        return customProgressDialog;  
	    }  
}
