package com.seastar.wasai.views.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.custom.vg.list.CustomListView;
import com.custom.vg.list.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.KeywordEntity;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.SearchHistoryDataEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.adapters.HistorySearchAdapter;
import com.seastar.wasai.views.base.NotifyOfDataBaseActivity;
import com.seastar.wasai.views.common.HelpCourseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import android.widget.LinearLayout.LayoutParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 搜索界面
 * @author Jamie
 */
public class SearchMainActivity extends NotifyOfDataBaseActivity implements OnClickListener{
	private ViewGroup mHotSearchLl = null;
	private TextView mHotSearchTitle = null;
	private EditText mSearchEdittext = null;
	private TextView mClearHistory = null;
	private TextView mSearchTv; //点击搜索按钮
	private ImageView mDeleteInputText = null;
	//	private ViewGroup mHistoryVp = null;
	private ArrayList<SearchHistoryDataEntity> mList = new ArrayList<SearchHistoryDataEntity>();
	private ArrayList<KeywordEntity> mKeyowrdList = new ArrayList<KeywordEntity>();
	public static final String NOTI_OF_DATA = "NOTI_OF_DATA";
	private PreferencesWrapper mPreferencesWrapper;
	private CustomListView historyListView;
	private HistorySearchAdapter historyAdapter;
	private ScrollView mScrollView;
	private float y1 = 0;
	private float y2 = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		mPreferencesWrapper = new PreferencesWrapper(this);
		initView();
		initData();
		setListener();
	}
	private void initView(){
		mHotSearchLl = (ViewGroup) findViewById(R.id.hot_search_ll);
//		hotListView = (CustomListView) findViewById(R.id.hot_search_listview);
		historyListView = (CustomListView) findViewById(R.id.history_search_listview);
		mHotSearchTitle = (TextView) findViewById(R.id.hot_search_title);
		mSearchEdittext = (EditText) findViewById(R.id.search_edittext);
//		mHistoryVp = (ViewGroup) findViewById(R.id.history_search_ll);
		mClearHistory = (TextView) findViewById(R.id.clear_history_search);
		mSearchTv = (TextView) findViewById(R.id.done_or_cancel_tv);
		mDeleteInputText = (ImageView) findViewById(R.id.delete_search);

		mSearchEdittext.setFocusable(true);
		mSearchEdittext.setFocusableInTouchMode(true);
		mSearchEdittext.requestFocus();

		mScrollView = (ScrollView) findViewById(R.id.scrollview_layout);


		mScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						y1 = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						y2 = event.getY();
						if(y1 - y2 > 50){
							InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							if (inputMethodManager.isActive()) {
								inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
							}
						}
						break;
				}

				return false;
			}
		});
	}



	private void initData(){
		historyAdapter = new HistorySearchAdapter(this);
		historyListView.setAdapter(historyAdapter);
		editTextSetSelectionImp();
		GetKeywordDataTask();
		addHistoryView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		String strText = mPreferencesWrapper.getStringValue("searchResultActivity_input","");
		if(!TextUtils.isEmpty(strText)){
			mSearchEdittext.setText(strText);
			editTextSetSelectionImp();
		}
	}

	private void editTextSetSelectionImp() {
		mSearchEdittext.setSelection(mSearchEdittext.getText().length());
		mSearchEdittext.getSelectionStart();
	}

	private void setListener(){
		mClearHistory.setOnClickListener(this);
		mSearchTv.setOnClickListener(this);
		mDeleteInputText.setOnClickListener(this);
		mSearchEdittext.setOnKeyListener(onKeyListener);
		findViewById(R.id.course_detail).setOnClickListener(this);

		mSearchEdittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					mDeleteInputText.setVisibility(View.VISIBLE);
				} else {
					mDeleteInputText.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN ) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager.isActive()) {
					inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}
				String currentSearchText = mSearchEdittext.getText().toString().trim();
				if(!TextUtils.isEmpty(currentSearchText)){
					editTextSetSelectionImp();
					insertOrUpdateData(currentSearchText);
					ToSearchResultActivity(currentSearchText, SearchMainActivity.this);
				}else{
					GeneralUtil.showToastShort(SearchMainActivity.this, getResources().getString(R.string.input_search_keyword));
				}
				return true;
			}
			return false;
		}
	};

	private void addHttpView(List<KeywordEntity> list){
		mHotSearchTitle.setVisibility(View.VISIBLE);
		mHotSearchLl.setVisibility(View.VISIBLE);
		for (int i = 0 ; i < list.size();i++) {
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(10,10,10,10);
			final TextView searchTextView = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_textview_include, null);
			searchTextView.setLayoutParams(lp);
			searchTextView.setText(list.get(i).keyword);
			mHotSearchLl.addView(searchTextView);
			searchTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String currentText = searchTextView.getText().toString();
					insertOrUpdateData(currentText);
					ToSearchResultActivity(currentText, SearchMainActivity.this);
				}
			});
		}
	}

	private void insertOrUpdateData(String currentText) {
		SearchHistoryDataEntity entity = new SearchHistoryDataEntity();
		entity.search_name = currentText;
		entity.search_time = String.valueOf(new Date().getTime());
		boolean flag = false;
		for (SearchHistoryDataEntity tempEntity : mList) {
			if (tempEntity.search_name.equals(currentText)) {
				WasaiContentProviderUtils.getInstance(getApplicationContext()).updateSearchHistoryData(entity);
				flag = true;
				break;
			}
		}
		if (!flag) {
			WasaiContentProviderUtils.getInstance(getApplicationContext()).addSearchHistoryData(entity);
		}
		addHistoryView();
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(null, false, null, false);
		return true;
	}

	public void addHistoryView() {
		historyListView.removeAllViews();
		mList = WasaiContentProviderUtils.getInstance(getApplicationContext()).querySearchHistoryData();
		if(mList.size() >0){
			goneViewOfHistory(View.VISIBLE);
			int size;
			if(mList.size() > 10){
				size = 10;
			}else {
				size = mList.size();
			}
			for(int i = 0 ; i<size;i++){
				historyAdapter.setData(mList);
				historyAdapter.notifyDataSetChanged();
				historyListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						String currentText = mList.get(i).search_name;
						insertOrUpdateData(currentText);
						ToSearchResultActivity(currentText, SearchMainActivity.this);
					}
				});
			}
		} else {
			goneViewOfHistory(View.GONE);
		}
	}

	private void goneViewOfHistory(int gone) {
		findViewById(R.id.history_search_title).setVisibility(gone);
		historyListView.setVisibility(gone);
		findViewById(R.id.clear_history_search).setVisibility(gone);
	}

	private void ToSearchResultActivity(String currentText, SearchMainActivity packageContext) {
		mSearchEdittext.setText(currentText);
		editTextSetSelectionImp();
		Intent reIntent = new Intent(packageContext, SearchResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("keyword", currentText);
		reIntent.putExtras(bundle);
		startActivity(reIntent);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.clear_history_search:
				WasaiContentProviderUtils.getInstance(getApplicationContext()).deleteAllSearchHistoryData();
				historyListView.removeAllViews();
				mList.clear();
				goneViewOfHistory(View.GONE);
				break;
			case R.id.delete_search:
				mSearchEdittext.setText("");
				break;
			case R.id.done_or_cancel_tv:
				String currentSearchText = mSearchEdittext.getText().toString().trim();
				if(!TextUtils.isEmpty(currentSearchText)){
					editTextSetSelectionImp();
					insertOrUpdateData(currentSearchText);
					ToSearchResultActivity(currentSearchText, SearchMainActivity.this);
				}else{
					GeneralUtil.showToastShort(SearchMainActivity.this,getResources().getString(R.string.input_search_keyword));
				}
				break;
			case R.id.course_detail:
				Intent intent = new Intent();
				intent.setClass(this, HelpCourseActivity.class);
				startActivity(intent);
				break;

		}
	}



	@Override
	public void finishActivity() {
		editTextSetSelectionImp();
		saveDataNull();
	}

	private void saveDataNull() {
		mPreferencesWrapper.setStringValueAndCommit("searchResultActivity_input", "");
		mPreferencesWrapper.setStringValueAndCommit("save_data", "");
	}


	private void GetKeywordDataTask() {
		String url = InterfaceConstant.KEYWORD_LIST + "/" + "hot" + "/" + 10;
		Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
			@Override
			public void doResponse(String dataJsonStr) {
				if (dataJsonStr != null) {
					Gson gson = new Gson();
					List<KeywordEntity> resultList = gson.fromJson(dataJsonStr,new TypeToken<List<KeywordEntity>>() {
					}.getType());
					if (resultList != null && resultList.size() > 0) {
						addHttpView(resultList);

					}
				}
			}
		};
		MyGsonRequest request = new MyGsonRequest(url, null, null);
		MyApplication.addToRequestQueue(request.getRequest(sucessListener));
	}




	@Override
	public void nofityOfData(Intent intent) {
		super.nofityOfData(intent);
		String strText = mPreferencesWrapper.getStringValue("save_data","");
		if(!TextUtils.isEmpty(strText)){
			insertOrUpdateData(strText);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			editTextSetSelectionImp();
			saveDataNull();
			finish();
		}
		return super.dispatchKeyEvent(event);
	}
}
