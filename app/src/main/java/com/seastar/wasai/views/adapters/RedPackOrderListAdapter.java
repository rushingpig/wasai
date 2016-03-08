package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.seastar.wasai.Entity.RedPackOrder;
import com.seastar.wasai.R;

import java.util.List;

public class RedPackOrderListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<RedPackOrder> orderList;
    private DisplayImageOptions imageDisplayOptions;

    public RedPackOrderListAdapter(Context context, List<RedPackOrder> orderList) {
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
        RedPackOrder order = (RedPackOrder) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.red_pack_order_item, parent, false);
            holder.orderDateView = (TextView) convertView.findViewById(R.id.order_date);
            holder.orderMoneyView = (TextView) convertView.findViewById(R.id.red_pack_type_and_money);
            holder.orderStatusView = (TextView) convertView.findViewById(R.id.red_pack_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.orderDateView.setText("记录：" + order.createAt);
        holder.orderMoneyView.setText(order.typeMsg);
        holder.orderStatusView.setText(order.statusMsg);

        return convertView;
    }

    public final class ViewHolder {
        public TextView orderDateView;
        public TextView orderMoneyView;
        public TextView orderStatusView;
    }
}
