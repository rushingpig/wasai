package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagGuideListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Guide> guideList;
	private DisplayImageOptions imageDisplayOptions;
	private Context context;

	public TagGuideListAdapter(Context context) {
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
				guideViewHolder.likeNum.setImageResource(R.drawable.ic_action_compact_favourite_selected);
			} else {
				guideViewHolder.likeNum.setImageResource(R.drawable.ic_action_compact_favourite_normal);
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
								counterView.setImageResource(R.drawable.ic_action_compact_favourite_normal);
								counterView.setCountViewText(guide.getFavoriteCount() > 0?(guide.getFavoriteCount() - 1 + ""):"0");
								postFavorite(guide, 0);
							} else {
								counterView.setImageResource(R.drawable.ic_action_compact_favourite_selected);
								counterView.setCountViewText(guide.getFavoriteCount() + 1 + "");
								postFavorite(guide, 1);
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

	private void postFavorite(final Guide guide, final int actionType) {
		String url = InterfaceConstant.FAVORITE;
		int method = Request.Method.POST;
		if(actionType == 0){
			url = InterfaceConstant.FAVORITE + "/" + guide.getGuideId();
			method = Request.Method.DELETE;
		}
		Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
			@Override
			public void doResponse(String dataJsonStr) {
				Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
				Guide tempGuide = gson.fromJson(dataJsonStr,Guide.class);
				guide.setFavoriteId(actionType == 0?0l:1l);
				guide.setFavoriteCount(tempGuide.getFavoriteCount());
				TagGuideListAdapter.this.notifyDataSetChanged();
				Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
			}
		};
		MyGsonRequest request = new MyGsonRequest(method, url, null, null);
		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("guide_id", guide.getGuideId() + "");
		MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
	}
}
