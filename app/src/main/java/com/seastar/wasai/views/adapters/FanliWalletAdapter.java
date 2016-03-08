package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.Entity.FanliToBeAvailableListEntity;
import com.seastar.wasai.Entity.IncomeListEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.views.order.OrderDetailActivity;

import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 */
public class FanliWalletAdapter extends BaseAdapter{
    private List<FanliToBeAvailableListEntity> mList;
    private Context mContext;
    private boolean isShowDetails = true;

    public FanliWalletAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if(mList == null){
            return 0;
        }else {
            return mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<FanliToBeAvailableListEntity> list){
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fanli_wallet,null);
            holder = new ViewHolder();
            holder.mDate = (TextView) convertView.findViewById(R.id.item_fanli_date);
            holder.mItemName = (TextView) convertView.findViewById(R.id.item_fanli_itemname);
            holder.mFanli = (TextView) convertView.findViewById(R.id.item_fanli_fanli);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FanliToBeAvailableListEntity body = mList.get(position);
        holder.mItemName.setText(body.title);
        holder.mDate.setText(body.buydate);
        holder.mFanli.setText(body.fanli);
        return convertView;
    }
    class ViewHolder{
        private TextView mItemName,mDate,mFanli;
    }
}
