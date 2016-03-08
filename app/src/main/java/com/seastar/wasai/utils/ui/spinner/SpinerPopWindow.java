package com.seastar.wasai.utils.ui.spinner;

import java.util.ArrayList;
import java.util.List;

import com.seastar.wasai.R;
import com.seastar.wasai.Entity.ProductCateEntity;
import com.seastar.wasai.utils.ui.spinner.AbstractSpinerAdapter.IOnItemSelectListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Jamie
 */
@SuppressLint("InflateParams")
public class SpinerPopWindow extends PopupWindow implements OnItemClickListener{

	private Context mContext;
	private ListView mListView;
	private AbstractSpinerAdapter mAdapter;
	private IOnItemSelectListener mItemSelectListener;
	private TextView mTextView = null;
	private ImageView mImg = null;
	private TextPaint tp = null;
	private List<ProductCateEntity> mList = new ArrayList<ProductCateEntity>();
	
	public SpinerPopWindow(Context context)
	{
		super(context);	
		mContext = context;
		init();
	}
	public void setItemListener(IOnItemSelectListener listener){
		mItemSelectListener = listener;
	}
	
	
	public void setAdatper(AbstractSpinerAdapter adapter){
		mAdapter = adapter;
		mListView.setAdapter(mAdapter);	
	}
	public void setSpinnerAdatper(List<ProductCateEntity> list){
//		if (mList != null) {
//			mList.clear();
//		}
		this.mList = list;
		mAdapter = new AbstractSpinerAdapter(mContext);
		mAdapter.setData(mList);
		if(mAdapter!=null){  
			mAdapter.notifyDataSetChanged();  
        }
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();  
	}
	public void notifyDataSetChangedImp(){
		mAdapter.notifyDataSetChanged();  
	}
	View headerView = null;
	private void init(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
		setContentView(view);		
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		
		setFocusable(true);
    	ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
	
		
		mListView = (ListView) view.findViewById(R.id.listview);
		headerView =  LayoutInflater.from(mContext).inflate(R.layout.foo_list_header, null); 
		mTextView = (TextView) headerView.findViewById(R.id.textView);
		mImg = (ImageView) headerView.findViewById(R.id.product_checkbox);
		tp = mTextView.getPaint(); 
		tp.setFakeBoldText(true);
		mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_p));
		mImg.setVisibility(View.VISIBLE);
		mListView.addHeaderView(headerView); 
		mListView.setOnItemClickListener(this);
	}
	public void ListHeaderView(boolean isSelector){
		if(isSelector){
			tp.setFakeBoldText(true);
			mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_p));
			mImg.setVisibility(View.VISIBLE);
		}else {
			tp.setFakeBoldText(false);
			 mTextView.setTextColor(mContext.getResources().getColor(R.color.product_selector_textcolor_n));
			mImg.setVisibility(View.INVISIBLE);
		}
	}
	public void isShowListHeaderView(boolean isShow){
		if(isShow){
			headerView.setVisibility(View.VISIBLE);
		}else {
			mListView.removeHeaderView(headerView);
//			headerView.setVisibility(View.GONE);
		}
	}
	


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null){
			mItemSelectListener.onItemClick(pos);
		}
	}
	
}
