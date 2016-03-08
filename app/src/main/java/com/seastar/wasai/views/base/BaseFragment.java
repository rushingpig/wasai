package com.seastar.wasai.views.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by Jamie on 2015/7/21.
 */
public abstract class  BaseFragment extends Fragment {
	private View mContentView;
	protected Context mContext;
	private Boolean hasInitData = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (mContentView == null) {
			mContentView = initView(inflater);
		}
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!hasInitData) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					initData();
					hasInitData = true;
				}
			}, 500);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		((ViewGroup) mContentView.getParent()).removeView(mContentView);
	}

	protected abstract View initView(LayoutInflater inflater);

	public abstract void initData();


}
