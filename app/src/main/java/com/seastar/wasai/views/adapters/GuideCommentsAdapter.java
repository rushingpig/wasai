package com.seastar.wasai.views.adapters;

import java.util.Date;
import java.util.List;

import com.seastar.wasai.R;

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
import com.seastar.wasai.Entity.Comment;
import com.seastar.wasai.utils.RelativeDateFormat;

/**
 * @ClassName: GuideCommentsAdapter
 * @Description: 导购评论适配器
 * @author 杨腾
 * @date 2015年4月25日 下午5:13:52
 */
public class GuideCommentsAdapter extends BaseAdapter {
	private Context context;
	private List<Comment> commentList;
	private DisplayImageOptions imageDisplayOptions;

	public GuideCommentsAdapter(Context context, List<Comment> commentList) {
		imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).build();
		this.context = context;
		this.commentList = commentList;
	}

	@Override
	public int getCount() {
		return commentList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return commentList.get(position).getCommentId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.guide_comment_list_item, parent, false);
			holder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
			holder.nicknameView = (TextView) convertView.findViewById(R.id.nickname);
			holder.contentView = (TextView) convertView.findViewById(R.id.content);
			holder.createAtView = (TextView) convertView.findViewById(R.id.created_at);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Comment comment = (Comment) getItem(position);
		holder.avatarView.setImageResource(R.drawable.avatar);
		ImageLoader.getInstance().displayImage(comment.getPictureUrl(), holder.avatarView, imageDisplayOptions);
		holder.nicknameView.setText(comment.getNickname());
		holder.contentView.setText(comment.getCommentInfo());
		holder.createAtView.setText(RelativeDateFormat.format(new Date(comment.getCommentTime())));

		return convertView;
	}

	public final class ViewHolder {
		private ImageView avatarView;
		private TextView nicknameView;
		private TextView contentView;
		private TextView createAtView;
	}

}
