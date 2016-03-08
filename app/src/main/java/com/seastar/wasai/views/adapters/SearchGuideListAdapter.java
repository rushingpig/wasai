package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.FavoriteService;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import java.util.List;

public class SearchGuideListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Guide> guideList;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public SearchGuideListAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}

	public void setmData(List<Guide> guideList) {
		this.guideList = guideList;
	}

	@Override
	public int getCount() {
		return guideList != null ? guideList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return guideList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return guideList.get(position).getGuideId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			Guide guide = (Guide) getItem(position);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.tag_guide_item, parent, false);
			holder.titleView = (TextView) convertView.findViewById(R.id.guide_item_title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.guide_item_image);
			holder.imageView.setTag(guide.getPic(Image.SIZE_MIDDLE));
			holder.likeNum = (GuideActionCompactCounterView) convertView.findViewById(R.id.article_action_compact_fav);
			holder.accessNum = (GuideActionCompactCounterView) convertView
					.findViewById(R.id.article_action_compact_access);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		putGuideList(position, convertView, holder);
		return convertView;
	}

	private void putGuideList(int position, View convertView, ViewHolder guideViewHolder) {
		if (guideList != null && guideList.size() > 0) {
			final Guide guide = (Guide) getItem(position);
			if (!guideViewHolder.imageView.getTag().equals(guide.getPic(Image.SIZE_MIDDLE))) {
				guideViewHolder.imageView.setImageBitmap(null);
			}
			ImageLoader.getInstance().displayImage(guide.getPic(Image.SIZE_MIDDLE), guideViewHolder.imageView,
					imageDisplayOptions);
			guideViewHolder.titleView.setText(guide.getTitle());
			if (guide.getFavoriteId() > 0) {
				guideViewHolder.likeNum.setImageResource(R.drawable.ic_small_heart_selected);
			} else {
				guideViewHolder.likeNum.setImageResource(R.drawable.ic_small_heart_normal);
			}
			guideViewHolder.accessNum.setImageResource(R.drawable.access);
			guideViewHolder.accessNum.setCountViewText(guide.getPvCount() + "");
			guideViewHolder.likeNum.setCountViewText(String.valueOf(guide.getFavoriteCount()));
			final GuideActionCompactCounterView counterView = guideViewHolder.likeNum;
			guideViewHolder.likeNum.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MyApplication.isLogin()) {
						if (CommonUtil.checkNetWork()) {
							if (guide.getFavoriteId() > 0) {
								counterView.setImageResource(R.drawable.ic_small_heart_normal);
								counterView.setCountViewText(guide.getFavoriteCount() > 0?(guide.getFavoriteCount() - 1 + ""):"0");
								new SubmitFavoriteTask(guide, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							} else {
								counterView.setImageResource(R.drawable.ic_small_heart_selected);
								counterView.setCountViewText(guide.getFavoriteCount() + 1 + "");
								new SubmitFavoriteTask(guide, 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	}

	public final class ViewHolder {
		public TextView titleView;
		public ImageView imageView;
		public GuideActionCompactCounterView likeNum;
		public GuideActionCompactCounterView accessNum;

	}

	private class SubmitFavoriteTask extends AsyncTask<Object, Object, Long> {
		private Guide guide;
		private int actionType;

		private FavoriteService favoriteService = new FavoriteService();

		public SubmitFavoriteTask(Guide guide, int actionType) {
			this.guide = guide;
			this.actionType = actionType;
		}

		@Override
		protected Long doInBackground(Object... params) {
			Long favoriteCount = guide.getFavoriteCount();
			if (actionType == 0) {
				favoriteCount = favoriteService.deleteFavorite(guide.getGuideId());
			} else {
				favoriteCount = favoriteService.addFavorite(guide.getGuideId());
			}
			return favoriteCount;
		}

		protected void onPostExecute(Long favoriteCount) {
			if (favoriteCount != -1) {
				guide.setFavoriteCount(favoriteCount);
				if (actionType == 0) {
					guide.setFavoriteId(0l);
				} else {
					guide.setFavoriteId(1l);
				}
			} else {
				Toast.makeText(context, ToastMessage.OPERATION_FAILED, Toast.LENGTH_SHORT).show();
			}
			SearchGuideListAdapter.this.notifyDataSetChanged();
		}
	}
}
