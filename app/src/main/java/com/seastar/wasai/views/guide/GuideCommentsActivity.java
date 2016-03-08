package com.seastar.wasai.views.guide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.seastar.wasai.Entity.Comment;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.service.CommentService;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.views.adapters.GuideCommentsAdapter;
import com.seastar.wasai.views.base.BaseListActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;
import com.seastar.wasai.views.login.LoginActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: TopicListActivity
 * @Description: 导购评论
 * @author 杨腾
 * @date 2015年4月15日 下午6:59:52
 */
public final class GuideCommentsActivity extends BaseListActivity {

	private PullToRefreshListView mPullRefreshListView;
	private GuideCommentsAdapter guideCommentListAdapter;
	private List<Comment> commentList = new ArrayList<Comment>();
	private ProgressDialog progressDialog;
	private TextView sendBtn;
	private EditText commentEdit;
	private Long guideId;
	private Long lastId = 0l;
	private View actionBack;
	private SimpleMessageView errorView;
	private TextView nobodyCommentTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide_comment_list);

		Bundle bundle = this.getIntent().getExtras();
		guideId = bundle.getLong("guideId");
		progressDialog = ProgressDialog.show(this, null, "玩命加载中...", true, false);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		nobodyCommentTv = (TextView) findViewById(R.id.nobody_comment_tv);

		guideCommentListAdapter = new GuideCommentsAdapter(this, commentList);

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new GetDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		});

		ListView actualListView = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(actualListView);
		actualListView.setAdapter(guideCommentListAdapter);
		actualListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		sendBtn = (TextView) findViewById(R.id.send_comment);
		commentEdit = (EditText) findViewById(R.id.edit_comment_content);

		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String commentContent = commentEdit.getText().toString();
				if (!"".equals(commentContent.trim())) {
					if(MyApplication.isLogin()){
						User currentUser = MyApplication.getCurrentUser();
						commentEdit.setText("");
						Comment tempComment = new Comment();
						tempComment.setPictureUrl(currentUser.getPictureUrl());
						tempComment.setNickname(currentUser.getNickname());
						tempComment.setCommentInfo(commentContent);
						tempComment.setCommentTime(new Date().getTime());
						commentList.add(0, tempComment);
						guideCommentListAdapter.notifyDataSetChanged();
						new SubmitCommentTask(commentContent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0) ;

					}else{
						Context context = v.getContext();
						Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}
			}
		});

		actionBack = findViewById(R.id.action_back);
		actionBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		errorView = (SimpleMessageView) findViewById(R.id.container_error);
		errorView.setOnClick(new ICallBack(){
			@Override
			public void onClick() {
				if(CommonUtil.checkNetWork()){
					errorView.setVisibility(View.INVISIBLE);
					new GetDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});
		if(!CommonUtil.checkNetWork()){
			errorView.setVisibility(View.VISIBLE);
			progressDialog.dismiss();
			return;
		}
		new GetDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	* @ClassName: SubmitCommentTask 
	* @Description: 发表评论
	* @author 杨腾
	* @date 2015年4月27日 下午1:27:02
	 */
	private class SubmitCommentTask extends AsyncTask<Object, Object, Long> {
		private String commentContent;

		public SubmitCommentTask(String commentContent) {
			this.commentContent = commentContent;
		}

		private CommentService commentService = new CommentService();

		@Override
		protected Long doInBackground(Object... params) {
			return commentService.addComment("guide", guideId, this.commentContent);
		}

		@Override
		protected void onPostExecute(Long result) {
			if(result != -1){
				Toast.makeText(getApplicationContext(), ToastMessage.SUCCESS_TO_COMMENT, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), ToastMessage.FAILED_TO_COMMENT, Toast.LENGTH_SHORT).show();
			}
			if(nobodyCommentTv.getVisibility() == View.VISIBLE){
				nobodyCommentTv.setVisibility(View.GONE);
			}

		}
	}

	/**
	* @ClassName: GetDataTask 
	* @Description: 获取评论列表
	* @author 杨腾
	* @date 2015年4月27日 下午1:27:12
	 */
	private class GetDataTask extends AsyncTask<Object, Object, List<Comment>> {
		private CommentService commentService = new CommentService();

		@Override
		protected List<Comment> doInBackground(Object... params) {
			List<Comment> comments = commentService.getCommentList("guide", guideId, lastId, 20);
			return comments;
		}

		protected void onPostExecute(List<Comment> result) {
			if (result != null && result.size() > 0) {
				lastId = result.get(result.size() - 1).getCommentId();
				commentList.addAll(result);
				guideCommentListAdapter.notifyDataSetChanged();
			} else {
				if(lastId > 0){
					Toast.makeText(GuideCommentsActivity.this, ToastMessage.NOT_FOUND_COMMENT_LIST, Toast.LENGTH_SHORT).show();
				}else {
					nobodyCommentTv.setVisibility(View.VISIBLE);
				}
			}
			mPullRefreshListView.onRefreshComplete();
			progressDialog.dismiss();
		}
	}
}
