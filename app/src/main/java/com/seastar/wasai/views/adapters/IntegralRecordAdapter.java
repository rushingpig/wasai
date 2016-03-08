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
import com.seastar.wasai.Entity.IntegralListEntity;
import com.seastar.wasai.Entity.IntegralRecordListEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.views.signin.MyIntegral;

import java.util.List;

/**
 * Created by Jamie on 2015/6/19.
 */
public class IntegralRecordAdapter extends BaseAdapter{
    private List<IntegralRecordListEntity> mList;
    private Context mContext;

    public IntegralRecordAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setData(List<IntegralRecordListEntity> list){
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_integral_record,null);
            holder = new ViewHolder();
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            holder.points = (TextView) convertView.findViewById(R.id.points);
            holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        IntegralRecordListEntity body = mList.get(position);
        holder.desc.setText(body.desc);
        holder.points.setText(body.points);
        holder.create_time.setText(body.create_time);
        return convertView;
    }
    private class ViewHolder{
        private TextView desc;
        private TextView create_time;
        private TextView points;

    }
}
