package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Order;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.RelativeDateFormat;

import java.util.Date;
import java.util.List;

public class OrderListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Order> orderList;
    private DisplayImageOptions imageDisplayOptions;

    public OrderListAdapter(Context context, List<Order> orderList) {
        this.mInflater = LayoutInflater.from(context);
        this.orderList = orderList;
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
    }

    @Override
    public int getCount() {
        return orderList != null ? orderList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Order order = (Order) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.order_item, parent, false);
            holder.orderIdView = (TextView) convertView.findViewById(R.id.order_id);
            holder.orderDateView = (TextView) convertView.findViewById(R.id.order_date);
            holder.orderItemImageView = (ImageView) convertView.findViewById(R.id.order_item_image);
            holder.orderRebateView = (TextView) convertView.findViewById(R.id.order_item_rebate_money);
            holder.orderMoneyDateView = (TextView) convertView.findViewById(R.id.order_item_money_date);

            holder.orderItemRebateMoneyLeft = (TextView) convertView.findViewById(R.id.order_item_rebate_money_left);
            holder.orderItemRebateMoneyRight = (TextView) convertView.findViewById(R.id.order_item_rebate_money_right);

            holder.orderNormalDesc = convertView.findViewById(R.id.order_normal_desc);
            holder.orderClosedDesc = convertView.findViewById(R.id.order_closed_desc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.orderIdView.setText("订单号:" + order.getOrderId());
        holder.orderDateView.setText("记录时间：" + RelativeDateFormat.dateToStr(new Date(order.getTime()), "yyyy-MM-dd"));
        ImageLoader.getInstance().displayImage(CommonUtil.getImageURL(order.getPicUrl(), Image.SIZE_MIDDLE), holder.orderItemImageView, imageDisplayOptions);

        if(!order.isClosed()){
            holder.orderNormalDesc.setVisibility(View.VISIBLE);
            holder.orderClosedDesc.setVisibility(View.GONE);

            holder.orderItemRebateMoneyLeft.setText(CommonUtil.wrapOrderStatus(order.getStatus()));
            holder.orderRebateView.setText(order.getFanli());
            holder.orderMoneyDateView.setText(order.getMsg());
        }else{
            holder.orderNormalDesc.setVisibility(View.GONE);
            holder.orderClosedDesc.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView orderIdView;
        public TextView orderDateView;
        public ImageView orderItemImageView;
        public TextView orderRebateView;
        public TextView orderMoneyDateView;

        public TextView orderItemRebateMoneyLeft;
        public TextView orderItemRebateMoneyRight;

        public View orderNormalDesc;
        public View orderClosedDesc;
    }
}
