package com.seastar.wasai.views.adapters;

import java.util.List;

import com.seastar.wasai.R;
import com.seastar.wasai.Entity.Guide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author Jamie
 *
 */
public class TimeLineChildAdapter extends BaseAdapter{
	private List<Guide> mList;
	private Context mContext;

	public TimeLineChildAdapter( Context mContext, List<Guide> list) {
		super();
		this.mContext = mContext;
		this.mList = list;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		} else {
			return mList.size();
		}
	}
	public void setData(List<Guide> body) {
		this.mList = body;
	}

	@Override
	public Object getItem(int position) {
		if (mList == null || position < 0 || position > mList.size()) {
			return null;
		} else {
			return mList.get(position);
		}
	}
	public void addItem(Guide item){
		this.mList.add(item);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_time_line_activity7, null);
			holder = new ViewHolder();
			holder.mTitleName = (TextView) convertView.findViewById(R.id.item_seven_day_text);
			holder.mLikeText = (TextView) convertView.findViewById(R.id.item_timeline_like0_text7);
			holder.mLikeImg = (ImageView) convertView.findViewById(R.id.item_timeline_like0_img7);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		Guide entity = mList.get(position);
		holder.mTitleName.setText(entity.getTitle());
		if(entity.getFavoriteId() > 0){
			holder.mLikeImg.setImageResource(R.drawable.like_sel);
		}else {
			holder.mLikeImg.setImageResource(R.drawable.like_nor);
		}
		holder.mLikeText.setText(entity.getFavoriteCount() + "");
		
		
		return convertView;
	}
	class ViewHolder{
		private TextView mTitleName,mLikeText;
		private ImageView mLikeImg;
	}

}
