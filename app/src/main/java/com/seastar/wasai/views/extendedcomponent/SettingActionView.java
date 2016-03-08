package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seastar.wasai.R;

/**
* @ClassName: SettingActionView 
* @Description: 自定义设置项
* @author 杨腾
* @date 2015年5月11日 下午4:07:25
 */
public class SettingActionView  extends RelativeLayout {
	private ImageView imageView;
	private ImageView redPointView;
	private TextView textView;
	private TextView descView;

	public SettingActionView(Context context) {
		super(context);
	}

	public SettingActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.settingActionView);
		LayoutInflater.from(context).inflate(R.layout.setting_action_view, this, true);
		imageView = (ImageView) findViewById(R.id.icon);
		imageView.setImageDrawable(typedArray.getDrawable(R.styleable.settingActionView_settingIcon));
		redPointView = (ImageView) findViewById(R.id.redPoint);
		textView = (TextView) findViewById(R.id.title);
		textView.setText(typedArray.getText(R.styleable.settingActionView_settingTitle));
		descView = (TextView) findViewById(R.id.extra_desc);
		descView.setText(typedArray.getText(R.styleable.settingActionView_settingDesc));
		typedArray.recycle();
	}
	
	public void setText(String text){
		textView.setText(text);
	}
	public void setDescText(String text){
		descView.setText(text);
	}
	public void setRedPointViewVisible(boolean flag){
		if(flag){
			redPointView.setVisibility(View.VISIBLE);
		}else{
			redPointView.setVisibility(View.GONE);
		}
	}
}
