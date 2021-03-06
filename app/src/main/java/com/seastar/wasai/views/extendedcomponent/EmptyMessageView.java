package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.R;

/**
 * @ClassName: SimpleMessageView
 * @Description: 自定义消息View
 * @author 杨腾
 * @date 2015年4月27日 上午9:22:46
 */
public class EmptyMessageView extends LinearLayout {
	private TextView messageView;

	public EmptyMessageView(Context context) {
		super(context);
	}

	public EmptyMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.simpleMessageView);
		LayoutInflater.from(context).inflate(R.layout.empty_message_view, this, true);
		messageView = (TextView) findViewById(R.id.message);
		messageView.setText(typedArray.getText(R.styleable.simpleMessageView_message));
		typedArray.recycle();
	}

}
