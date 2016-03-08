package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.ImageUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.ui.Anticlockwise;
import com.seastar.wasai.utils.ui.CustomDigitalClockImp;
import com.seastar.wasai.utils.ui.RotateTextView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.ProductItemFavouriteCounterView;
import com.seastar.wasai.views.login.LoginActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProductListAdapter
 * @Description: 商品列表适配器
 * @author 杨腾
 * @date 2015年4月17日 下午6:22:13
 */
public class ProductListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Item> itemList;
	private List<Item> superList;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public ProductListAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(R.drawable.item_middle_holder).showImageOnLoading(R.drawable.item_middle_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}

	public ProductListAdapter(Context context,List<Item> itemList) {
		this.context = context;
		this.itemList = itemList;
		this.mInflater = LayoutInflater.from(context);
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(R.drawable.item_middle_holder).showImageOnLoading(R.drawable.item_middle_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}


	@Override
	public int getCount() {
		return itemList != null ? itemList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return itemList.get(position).getItemId();
	}

	public void setData(List<Item> itemList)
	{
		this.itemList = itemList;
	}

	public void setGuideList(List<Item> superList) {
		this.superList = superList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			Item product = (Item) getItem(position);
			convertView = mInflater.inflate(R.layout.home_item_item,null);
			holder.beforePrice = (TextView) convertView.findViewById(R.id.before_price);
			holder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
			holder.imageView.setTag(product.getPic(Image.SIZE_MIDDLE));
			holder.priceView = (TextView) convertView.findViewById(R.id.item_price);
			holder.saleMoneyText = (TextView) convertView.findViewById(R.id.sale_money);
			holder.counterView = (ImageView) convertView.findViewById(R.id.item_favourite);
			holder.saleTopText = (RotateTextView) convertView.findViewById(R.id.sale_top_text);
			holder.saleFrameLayout = (FrameLayout) convertView.findViewById(R.id.sale_frame_layout);
			holder.superRelativelayout = (RelativeLayout) convertView.findViewById(R.id.super_reabate_time_layout);

			holder.hour1 = (TextView) convertView.findViewById(R.id.hour_1);
			holder.hour2 = (TextView) convertView.findViewById(R.id.hour_2);
			holder.minute1 = (TextView) convertView.findViewById(R.id.minute_1);
			holder.minute2 = (TextView) convertView.findViewById(R.id.minute_2);
			holder.second1 = (TextView) convertView.findViewById(R.id.second_1);
			holder.second2 = (TextView) convertView.findViewById(R.id.second_2);
			holder.endView = convertView.findViewById(R.id.end_view);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (itemList != null && itemList.size() > 0) {
			final Item product = (Item) getItem(position);
			if(!holder.imageView.getTag().equals(product.getPic(Image.SIZE_MIDDLE))){
				holder.imageView.setImageBitmap(null);
			}

//			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//			int width = wm.getDefaultDisplay().getWidth();
//
//			int imageWidth = (width - ImageUtil.dip2px(context, (8 * 3) + 2)) / 2;
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageWidth);
//			holder.imageView.setLayoutParams(lp);

			ImageLoader.getInstance().displayImage(product.getPic(Image.SIZE_MIDDLE), holder.imageView,
					imageDisplayOptions);
			holder.priceView.setText(product.getNowPrice());
			holder.saleMoneyText.setText(product.getFanli() + "%");
			holder.beforePrice.setText(product.getBeforePrice());
			holder.beforePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			if(product.getSale().equals("-100%") || product.getSale().equals("-0%")){
				holder.saleFrameLayout.setVisibility(View.GONE);
			}else {
				holder.saleFrameLayout.setVisibility(View.VISIBLE);
				holder.saleTopText.setText(product.getSale());
			}
			long mRemainTime = product.getRemainTime();
			if (mRemainTime >= 0) {
				holder.counterView.setVisibility(View.GONE);
                holder.superRelativelayout.setVisibility(View.VISIBLE);
				long hour = product.getRemainTime() / (1000 * 60 * 60);
				long minute = (product.getRemainTime() % (1000 * 60 * 60)) / (1000 * 60);
				long second = (product.getRemainTime() % (1000 * 60)) / 1000;
				if (hour < 10) {
					holder.hour1.setText("0");
					holder.hour2.setText(String.valueOf(hour));
				} else {
					holder.hour1.setText(String.valueOf(hour / 10));
					holder.hour2.setText(String.valueOf(hour % 10));
				}

				if (minute < 10) {
					holder.minute1.setText("0");
					holder.minute2.setText(String.valueOf(minute));
				} else {
					holder.minute1.setText(String.valueOf(minute / 10));
					holder.minute2.setText(String.valueOf(minute % 10));
				}

				if (second < 10) {
					holder.second1.setText("0");
					holder.second2.setText(String.valueOf(second));
				} else {
					holder.second1.setText(String.valueOf(second / 10));
					holder.second2.setText(String.valueOf(second % 10));
				}
			}else if(mRemainTime == -100){
				holder.counterView.setVisibility(View.VISIBLE);
                holder.superRelativelayout.setVisibility(View.GONE);
            }

            if (mRemainTime <= 0 && mRemainTime > -100) {
                holder.endView.setVisibility(View.VISIBLE);
            } else {
                holder.endView.setVisibility(View.GONE);
            }

			if (product.getWishId() > 0) {
				holder.counterView.setImageResource(R.drawable.like_h);
			} else {
				holder.counterView.setImageResource(R.drawable.like_n);
			}
			final ImageView counterTextView = holder.counterView;
			holder.counterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MyApplication.isLogin()) {
						if (CommonUtil.checkNetWork()) {
							if (product.getWishId() <= 0) {
								counterTextView.setImageResource(R.drawable.like_h);
								postFavorite(product, 1);
							} else {
								if (product.getFavoriteCount() > 0) {
									counterTextView.setImageResource(R.drawable.like_n);
									postFavorite(product, 0);
								}
							}
						} else {
							GeneralUtil.showToastShort(context, ToastMessage.NET_WORK_NOT_WORK);
						}
					} else {
						Context context = v.getContext();
						Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}
			});
		}
		return convertView;
	}

	public final class ViewHolder {
		private TextView beforePrice;
		public TextView priceView;
		public ImageView imageView;
		private TextView saleMoneyText;
		public ImageView counterView;
		private RotateTextView saleTopText;
		private FrameLayout saleFrameLayout;

		private RelativeLayout superRelativelayout;
		private CustomDigitalClockImp superRemainTime;

		public TextView hour1;
		public TextView hour2;
		public TextView minute1;
		public TextView minute2;
		public TextView second1;
		public TextView second2;
		private View endView;


	}

	private void postFavorite(final Item product, final int actionType) {
		String url = InterfaceConstant.WISH;
		int method =  Request.Method.POST;
		if(actionType == 0){
			url = InterfaceConstant.WISH + "/default/" + product.getItemId();
			method = Request.Method.DELETE;
		}
		Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
			@Override
			public void doResponse(String dataJsonStr) {
				Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
				Item tempItem = gson.fromJson(dataJsonStr,Item.class);
				product.setWishId(actionType == 0 ? 0l : 1l);
				product.setFavoriteCount(tempItem.getFavoriteCount());
				ProductListAdapter.this.notifyDataSetChanged();
				Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
			}
		};
		MyGsonRequest request = new MyGsonRequest(method, url, null, null);
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("name", "default");
		requestBody.put("item_id", product.getItemId() + "");
		MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
	}
}
