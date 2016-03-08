package com.seastar.wasai.views.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.R;
import com.seastar.wasai.Entity.Favorite;
import com.seastar.wasai.Entity.Image;

/**
 * @ClassName: FavoriteGuideListAdapter
 * @Description: 收藏列表适配器
 * @author 杨腾
 * @date 2015年4月17日 下午6:21:40
 */
public class FavoriteGuideListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Favorite> mData;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public FavoriteGuideListAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
				.cacheInMemory(false).cacheOnDisk(true).build();
	}

	public void setmData(List<Favorite> mData) {
		this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData != null ? mData.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.favorite_guide_item, parent, false);
			holder.titleView = (TextView) convertView.findViewById(R.id.guide_item_title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.guide_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mData != null && mData.size() > 0) {
			final Favorite guide = mData.get(position);
			ImageLoader.getInstance().displayImage(guide.getPic(Image.SIZE_MIDDLE).trim(), holder.imageView,
					imageDisplayOptions);
			holder.titleView.setText(guide.getTitle());
		}

		return convertView;
	}

	public final class ViewHolder {
		public TextView titleView;
		public ImageView imageView;
	}
}
