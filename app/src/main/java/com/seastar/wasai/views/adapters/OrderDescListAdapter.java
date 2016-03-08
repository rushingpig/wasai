package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seastar.wasai.R;

public class OrderDescListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mInflater;
    private String[] groupArray;
    private String[][] childArray;
    public OrderDescListAdapter(Context context,String[] groupArray,String[][] childArray){
        this.mInflater = LayoutInflater.from(context);
        this.groupArray = groupArray;
        this.childArray = childArray;
    }
    @Override
    public int getGroupCount() {
        return groupArray.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.order_desc_item_group, null);
        TextView platformView = (TextView) view.findViewById(R.id.order_desc_platform);
        platformView.setText(groupArray[groupPosition]);
        ImageView arrowImagView = (ImageView) view.findViewById(R.id.arrow_img);
        if(isExpanded){
            arrowImagView.setImageResource(R.drawable.sx_up);
        }else{
            arrowImagView.setImageResource(R.drawable.sx_down);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.order_desc_item_child, null);
        TextView platformView = (TextView) view.findViewById(R.id.order_desc);
        platformView.setText(childArray[groupPosition][childPosition]);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}