package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seastar.wasai.Entity.ExpenditureListEntity;
import com.seastar.wasai.Entity.IncomeListEntity;
import com.seastar.wasai.R;

import java.util.List;

/**
 * Created by Jamie on 2015/7/9.
 */
public class ExpenditureFragmentAdapter extends BaseAdapter {
    private List<ExpenditureListEntity> mList;
    private Context mContext;

    public ExpenditureFragmentAdapter(Context context) {
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
        return position;
    }

    public void setData(List<ExpenditureListEntity> list) {
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ex_fragment, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.item_ex_title);
            holder.mDate = (TextView) convertView.findViewById(R.id.item_ex_date);
            holder.mNum = (TextView) convertView.findViewById(R.id.item_ex_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ExpenditureListEntity entity = mList.get(position);
        holder.mTitle.setText(entity.title);
        holder.mNum.setText(Double.parseDouble(entity.fanli)/100 + "元");
        holder.mDate.setText(entity.time);

        return convertView;
    }

    class ViewHolder {
        private TextView mTitle, mNum, mDate;
    }
}
