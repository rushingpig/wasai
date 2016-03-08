package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;

/**
 * @ClassName: SimpleMessageView
 * @Description: 自定义消息View
 * @author 杨腾
 * @date 2015年4月27日 上午9:22:46
 */
public class SimpleMessageView extends LinearLayout {
	private ImageView imageView;
	private TextView messageView;
	private Button refreshBtn;
	private ICallBack icallBack = null;

	public interface ICallBack {
		public void onClick();
	}

	public void setOnClick(ICallBack iBack) {
		icallBack = iBack;
	}

	public SimpleMessageView(Context context) {
		super(context);
	}

	public SimpleMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.simpleMessageView);
		LayoutInflater.from(context).inflate(R.layout.simple_message_view, this, true);
		imageView = (ImageView) findViewById(R.id.image);
		messageView = (TextView) findViewById(R.id.message);
		refreshBtn = (Button) findViewById(R.id.refresh_btn);
		imageView.setImageDrawable(typedArray.getDrawable(R.styleable.simpleMessageView_image));
		messageView.setText(typedArray.getText(R.styleable.simpleMessageView_message));
		refreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				icallBack.onClick();
			}
		});

		typedArray.recycle();
	}

}
