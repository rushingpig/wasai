package com.seastar.wasai.views.extendedcomponent;

import com.seastar.wasai.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: GuideItemFavouriteCounterView
 * @Description: 自定义喜欢数控件
 * @author 杨腾
 * @date 2015年4月14日 上午9:41:07
 */
public class GuideActionCompactCounterView extends LinearLayout {
	private ImageView imageView;
	private TextView countView;

	public GuideActionCompactCounterView(Context context) {
		super(context);
	}

	public GuideActionCompactCounterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.guide_detail_action_compact_view, this);
		imageView = (ImageView) findViewById(R.id.icon_view);
		countView = (TextView) findViewById(R.id.count_view);
	}

	/**
	 * 设置图片资源
	 */
	public void setImageResource(int resId) {
		imageView.setImageResource(resId);
	}

	/**
	 * 设置显示的文字
	 */
	public void setCountViewText(String text) {
		countView.setText(text);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}
