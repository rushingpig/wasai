package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seastar.wasai.Entity.IncomeListEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.views.order.BackOrderDetailActivity;
import com.seastar.wasai.views.order.OrderDetailActivity;

import java.util.List;


/**
 * Created by Jamie on 2015/7/9.
 */
public class IncomeFragmentAdapter extends BaseAdapter{
    private List<IncomeListEntity> mList;
    private Context mContext;

    public IncomeFragmentAdapter(Context context){
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

    public void setData(List<IncomeListEntity> list){
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_income_fragment,null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.item_income_title);
            holder.mDate = (TextView) convertView.findViewById(R.id.item_income_date);
            holder.mFanli = (TextView) convertView.findViewById(R.id.item_income_fanli);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final IncomeListEntity entity = mList.get(position);
        holder.mTitle.setText(entity.title);
        holder.mDate.setText(entity.buydate);
        holder.mFanli.setText(entity.fanli + "元");

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent();
//                if(Double.parseDouble(entity.fanli) > 0){
//                    mIntent.setClass(mContext, OrderDetailActivity.class);
//                }else{
//                    mIntent.setClass(mContext, BackOrderDetailActivity.class);
//                }
//                Bundle bundle = new Bundle();
//                bundle.putString("orderId",entity.orderId);
//                bundle.putString("itemId",entity.itemId);
//                mIntent.putExtras(bundle);
//                mContext.startActivity(mIntent);
//            }
//        });
        return convertView;
    }
    class ViewHolder{
        private TextView mTitle,mDate,mFanli;
    }
}
