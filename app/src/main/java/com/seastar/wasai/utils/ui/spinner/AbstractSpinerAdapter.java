package com.seastar.wasai.utils.ui.spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seastar.wasai.R;
import com.seastar.wasai.Entity.ProductCateEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Jamie
 */
public  class AbstractSpinerAdapter extends BaseAdapter {
	Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();

	public static interface IOnItemSelectListener{
		public void onItemClick(int pos);
	};
	
	 private Context mContext;   
	 private List<ProductCateEntity> mList = new ArrayList<ProductCateEntity>();
	    
	 private LayoutInflater mInflater;
	
	 public  AbstractSpinerAdapter(Context context){
		 mContext = context;
	      mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 public void setData(List<ProductCateEntity> list){
		 this.mList = list;
	 }
	    
	    
	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mList.get(pos).toString();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		 ViewHolder viewHolder;
    	 
	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
	         viewHolder = new ViewHolder();
	         viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
	         viewHolder.mCheckBox = (ImageView) convertView.findViewById(R.id.product_checkbox);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     
	     ProductCateEntity item =  mList.get(pos);
		 viewHolder.mTextView.setText(item.name);
		 TextPaint tp = viewHolder.mTextView.getPaint(); 
		 tp.setFakeBoldText(true); 
		 if(item.isSelector == true){
			 tp.setFakeBoldText(true); 
			 viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_p));
			  viewHolder.mCheckBox.setVisibility(View.VISIBLE);
		 }else {
			 tp.setFakeBoldText(false); 
			 viewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_n));
			  viewHolder.mCheckBox.setVisibility(View.INVISIBLE);
		 }
		 


	     return convertView;
	}

	public static class ViewHolder
	{
	    public TextView mTextView;
	    public ImageView mCheckBox;
    }



}
