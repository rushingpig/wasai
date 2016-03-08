package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;

/**
 * @ClassName: GuideItemFavouriteCounterView
 * @Description: 自定义分享控件
 * @author 杨腾
 * @date 2015年4月14日 上午9:41:07
 */
public class ShareButtonView extends LinearLayout {
	public ImageView imageView;
	public TextView labelView;
	private ICallBack icallBack = null;

	public interface ICallBack {
		public void onClick();
	}

	public void setOnClick(ICallBack iBack) {
		icallBack = iBack;
	}

	public ShareButtonView(Context context) {
		super(context);
	}

	public ShareButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.shareButtonView);
		LayoutInflater.from(context).inflate(R.layout.share_btn_view, this, true);
		imageView = (ImageView) findViewById(R.id.share_icon_view);
		labelView = (TextView) findViewById(R.id.share_label_view);
		imageView.setImageDrawable(typedArray.getDrawable(R.styleable.shareButtonView_shareIcon));
		labelView.setText(typedArray.getText(R.styleable.shareButtonView_shareTitle));
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				icallBack.onClick();
			}
		});
		typedArray.recycle();
	}
}
