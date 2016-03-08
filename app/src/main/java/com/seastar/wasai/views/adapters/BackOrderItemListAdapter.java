package com.seastar.wasai.views.adapters;

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
import com.seastar.wasai.Entity.OrderItem;
import com.seastar.wasai.R;

import java.util.List;

public class BackOrderItemListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<OrderItem> orderItemList;
    private DisplayImageOptions imageDisplayOptions;

    public BackOrderItemListAdapter(Context context, List<OrderItem> orderItemList) {
        this.mInflater = LayoutInflater.from(context);
        this.orderItemList = orderItemList;
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).showImageOnFail(R.drawable.order_default).showImageForEmptyUri(R.drawable.order_default).build();
    }

    @Override
    public int getCount() {
        return orderItemList != null ? orderItemList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if(orderItemList != null && orderItemList.size() > 0){
            return orderItemList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        OrderItem orderItem = (OrderItem) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.order_detail_item, parent, false);
            holder.orderItemImageView = (ImageView) convertView.findViewById(R.id.order_item_image);
            holder.orderItemTitleView = (TextView) convertView.findViewById(R.id.order_item_title);
            holder.orderItemNumView = (TextView) convertView.findViewById(R.id.order_item_num);
            holder.orderItemNumView.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(orderItem != null){
            ImageLoader.getInstance().displayImage(orderItem.getItem_image_url(),holder.orderItemImageView,imageDisplayOptions);
            holder.orderItemTitleView.setText(orderItem.getItem_title());
            holder.orderItemNumView.setText("x" + orderItem.getItem_num());
        }
        return convertView;
    }

    public final class ViewHolder {
        public ImageView orderItemImageView;
        public TextView orderItemTitleView;
        public TextView orderItemNumView;
    }
}
