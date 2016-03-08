package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.custom.vg.list.CustomAdapter;
import com.seastar.wasai.Entity.KeywordEntity;
import com.seastar.wasai.Entity.SearchHistoryDataEntity;
import com.seastar.wasai.R;

import java.util.List;

/**
 * Created by Jamie on 2015/8/7.
 */
public class HistorySearchAdapter extends CustomAdapter {
    private Context mContext;
    private List<SearchHistoryDataEntity> mList;

    public HistorySearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if(mList == null){
            return 0;
        }else{
            return mList.size();
        }
    }
    public void setData(List<SearchHistoryDataEntity> list){
        this.mList = list;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_textview_include_listview,null);
            holder = new ViewHolder();
            holder.mKeywordText = (TextView) convertView.findViewById(R.id.hot_search_textview);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchHistoryDataEntity body = mList.get(position);
        String str = body.search_name;
        if(str.length() > 10){
            holder.mKeywordText.setText(str.substring(0,10) + "...");
        }else {
            holder.mKeywordText.setText(str);
        }
        return convertView;
    }
    class ViewHolder{
        TextView mKeywordText;
    }

}
