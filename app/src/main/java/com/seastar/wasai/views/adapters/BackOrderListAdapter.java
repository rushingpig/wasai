package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.seastar.wasai.Entity.BackOrder;
import com.seastar.wasai.Entity.Order;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.RelativeDateFormat;

import java.util.Date;
import java.util.List;

public class BackOrderListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BackOrder> orderList;
    private DisplayImageOptions imageDisplayOptions;

    public BackOrderListAdapter(Context context, List<BackOrder> orderList) {
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
        BackOrder order = (BackOrder) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.back_order_item, parent, false);
            holder.orderPlatformView = (TextView) convertView.findViewById(R.id.order_platform);
            holder.orderDateView = (TextView) convertView.findViewById(R.id.order_date);
            holder.orderNoView = (TextView) convertView.findViewById(R.id.order_no);
            holder.moneyTakingOutMsg = (TextView) convertView.findViewById(R.id.money_taking_out_msg);
            holder.moneyTakingOutView = (TextView) convertView.findViewById(R.id.money_taking_out);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.orderPlatformView.setText(order.getPlatform());
        holder.orderDateView.setText("记录时间：" + order.getTime());
        holder.orderNoView.setText(order.getOrderId());
        holder.moneyTakingOutMsg.setText(order.getMsg() + "：");
        holder.moneyTakingOutView.setText(order.getFanli() + " 元");

        return convertView;
    }

    public final class ViewHolder {
        public TextView orderPlatformView;
        public TextView orderDateView;
        public TextView orderNoView;
        public TextView moneyTakingOutMsg;
        public TextView moneyTakingOutView;
    }
}
