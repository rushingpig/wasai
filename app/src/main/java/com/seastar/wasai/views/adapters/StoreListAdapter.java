package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Store;
import com.seastar.wasai.R;


import java.util.List;

public class StoreListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Store> storeList;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public StoreListAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).showImageForEmptyUri(R.drawable.store_big_holder).showImageOnLoading(R.drawable.store_big_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}

	public void setmData(List<Store> shopsaleList) {
		this.storeList = shopsaleList;
	}

	@Override
	public int getCount() {
		return storeList != null ? storeList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return storeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return storeList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            Store shopsale = (Store) getItem(position);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.shopsale_item, parent, false);
			holder.imageView = (ImageView) convertView.findViewById(R.id.store_item_image);
			holder.imageView.setTag(shopsale.getPic(Image.SIZE_MIDDLE));

            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		putShopsaleList(position,holder);
		return convertView;
	}

	private void putShopsaleList(int position, ViewHolder shopsaleViewHolder) {
		if (storeList != null && storeList.size() > 0) {
			final Store shopsale = (Store) getItem(position);
			if (!shopsaleViewHolder.imageView.getTag().equals(shopsale.getPic(Image.SIZE_MIDDLE))) {
                shopsaleViewHolder.imageView.setImageBitmap(null);
			}
			ImageLoader.getInstance().displayImage(shopsale.getPic(Image.SIZE_MIDDLE), shopsaleViewHolder.imageView,
					imageDisplayOptions);
		}
	}

	public final class ViewHolder {
		public ImageView imageView;

	}

}
