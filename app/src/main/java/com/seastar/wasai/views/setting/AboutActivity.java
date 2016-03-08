package com.seastar.wasai.views.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.views.base.BaseActivity;

/**
* @ClassName: AboutActivity 
* @Description: 关于
* @author 杨腾
* @date 2015年5月11日 下午4:15:40
 */
public class AboutActivity extends BaseActivity {
	private TextView aboutAppNameView;	
	private View actionBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		aboutAppNameView = (TextView) findViewById(R.id.activity_about_app_name);
		String versionName = GeneralUtil.getAppVersionName(this);
		aboutAppNameView.setText("哇塞宝贝v" + versionName);
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
