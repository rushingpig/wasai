package com.seastar.wasai.views.extendedcomponent;

import java.text.DecimalFormat;

import com.seastar.wasai.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: GuideItemFavouriteCounterView
 * @Description: 自定义喜欢数控件
 * @author 杨腾
 * @date 2015年4月14日 上午9:41:07
 */
public class ProductItemFavouriteCounterView extends LinearLayout {
	private ImageView imageView;
	private TextView textView;

	public ProductItemFavouriteCounterView(Context context) {
		super(context);
	}

	public ProductItemFavouriteCounterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.product_item_favorite_counter_view, this);
		imageView = (ImageView) findViewById(R.id.favorite_icon);
		textView = (TextView) findViewById(R.id.favorite_counter);
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
	public void setTextViewText(String text) {
		int textInt = Integer.parseInt(text);
		if(textInt >= 1000){
			if(textInt % 1000 > 0){
				DecimalFormat df = new DecimalFormat("0.0");
				float textF = Float.parseFloat(text) / 1000;
				String textFS = df.format(textF);
				String[] textFSs = textFS.split("\\.");
				if("0".equals(textFSs[1])){
					text = textFSs[0] + "k";
				}else{
					text = df.format(textF) + "k";
				}
			}else{
				int textI = textInt / 1000;
				text = textI + "k";
			}
			
		}
		textView.setText(text);
	}
}
