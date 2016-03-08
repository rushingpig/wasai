package com.seastar.wasai.views.adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.Entity.Tag;

/**
 * @author Jamie
 */
public class GuideSpinerAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Tag> mList = new ArrayList<Tag>();
	private LayoutInflater mInflater;
	
	public static interface IOnItemSelectListener {
		public void onItemClick(int pos);
	}

	public GuideSpinerAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setData(List<Tag> list) {
		this.mList = list;
	}

	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mList.get(pos).toString();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.spiner_guide_tag_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
			viewHolder.mCheckBox = (ImageView) convertView.findViewById(R.id.guide_tag_checkbox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Tag item = mList.get(pos);
		viewHolder.mTextView.setText(item.getName());
		TextPaint tp = viewHolder.mTextView.getPaint();
		tp.setFakeBoldText(true);
		if (item.isSelected()) {
			tp.setFakeBoldText(true);
			viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_p));
			viewHolder.mCheckBox.setVisibility(View.VISIBLE);
		} else {
			tp.setFakeBoldText(false);
			viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_n));
			viewHolder.mCheckBox.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	public static class ViewHolder {
		public TextView mTextView;
		public ImageView mCheckBox;
	}

}
