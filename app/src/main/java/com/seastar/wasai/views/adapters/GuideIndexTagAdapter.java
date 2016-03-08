package com.seastar.wasai.views.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.Entity.Tag;

public class GuideIndexTagAdapter extends BaseExpandableListAdapter {
	private List<Tag> parentTagList;
	private LayoutInflater mLayoutInflater;

	public GuideIndexTagAdapter(Context context, List<Tag> tagList) {
		this.mLayoutInflater = LayoutInflater.from(context);
		this.parentTagList = tagList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return parentTagList.get(groupPosition).getTags().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return parentTagList.get(groupPosition).getTags().get(childPosition).getTagId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ChildViewHolder childViewHolder;
		if (convertView == null) {
			childViewHolder = new ChildViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_category_child, null);
			childViewHolder.childTextView = (TextView) convertView.findViewById(R.id.item_cat_name);
			convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}
		final Tag tag = (Tag) getChild(groupPosition, childPosition);
		childViewHolder.childTextView.setText(tag.getName());
		return convertView;
	}

	class ChildViewHolder {
		TextView childTextView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return parentTagList.get(groupPosition).getTags().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentTagList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return parentTagList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return parentTagList.get(groupPosition).getTagId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder groupViewHolder;
		if (convertView == null) {
			groupViewHolder = new GroupViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_category_group, null);
			groupViewHolder.groupTextView = (TextView) convertView.findViewById(R.id.item_cat_group_name);
			groupViewHolder.groupImageView = (ImageView) convertView.findViewById(R.id.item_cat_group_image);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}
		final Tag tag = (Tag) getGroup(groupPosition);
		groupViewHolder.groupTextView.setText(tag.getName());
		if (isExpanded) {
			groupViewHolder.groupImageView.setImageResource(R.drawable.sx_up);
		} else {
			groupViewHolder.groupImageView.setImageResource(R.drawable.sx_down);
		}
		return convertView;
	}

	class GroupViewHolder {
		TextView groupTextView;
		ImageView groupImageView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
