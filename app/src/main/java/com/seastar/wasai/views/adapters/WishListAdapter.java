package com.seastar.wasai.views.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.R;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.Entity.Wish;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

/**
 * @ClassName: ProductListAdapter
 * @Description: 商品喜欢列表适配器
 * @author 杨腾
 * @date 2015年4月17日 下午6:22:13
 */
public class WishListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Wish> wishList;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public WishListAdapter(Context context,List<Wish> itemList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.wishList = itemList;
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(false).cacheOnDisk(true).build();
	}

	@Override
	public int getCount() {
		return wishList != null ? wishList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return wishList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return wishList.get(position).getItemId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.favorite_product_item, parent, false);
			holder.titleView = (TextView) convertView.findViewById(R.id.product_item_title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.product_item_image);
			holder.priceView = (TextView) convertView.findViewById(R.id.product_item_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (wishList != null && wishList.size() > 0) {
			final Wish wish = (Wish)getItem(position);
			ImageLoader.getInstance().displayImage(wish.getPic(Image.SIZE_MIDDLE), holder.imageView,
					imageDisplayOptions);
			holder.titleView.setText(wish.getItemName());
			holder.priceView.setText(wish.getNowPrice());
		}
		return convertView;
	}

	public final class ViewHolder {
		public TextView titleView;
		public TextView priceView;
		public ImageView imageView;
	}
}
