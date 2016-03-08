package com.seastar.wasai.views.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.seastar.wasai.R;
import com.seastar.wasai.views.base.BaseActivity;

/**
* @ClassName: FeedbackActivity 
* @Description: 意见反馈
* @author 杨腾
* @date 2015年5月12日 下午1:03:16
 */
public class FeedbackActivity extends BaseActivity {
	private View actionBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		actionBack = findViewById(R.id.action_back);
		actionBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void finishActivity() {

	}
}
