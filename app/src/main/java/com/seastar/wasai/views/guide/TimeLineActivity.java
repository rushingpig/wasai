package com.seastar.wasai.views.guide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.FavoriteService;
import com.seastar.wasai.service.GuideService;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.LvHeightUtil;
import com.seastar.wasai.views.adapters.TimeLineChildAdapter;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 时间轴界面
 *
 * @author Jamie
 */
@SuppressLint({"UseSparseArrays", "SimpleDateFormat"})
public class TimeLineActivity extends BaseActivity implements OnClickListener {
    public static final String ISSHOWCHILDVIEW = "isShowChildView";
    private ListView mPullToRefreshListView = null;
    private LinearLayout mBack = null;
    private TextView mTitleName = null;
    private TextView mToListViewTopClick = null;
    private TimeLineAdapter mAdapter = null;
    private List<Guide> guideList = new ArrayList<Guide>();

    private SimpleMessageView errorView = null;
    private LoadMessageView loadMessageView;
    public static final int CODE_TIMELINE_DETAILS = 0;


    //标识listview是否处在滑动状态
    private boolean isBusy;
    private boolean isShow = true;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.time_line_activity);
        initView();
    }

    private void initView() {
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mPullToRefreshListView = (ListView) findViewById(R.id.listview_time_line);
//		mListView = mPullToRefreshListView.getRefreshableView();
        mToListViewTopClick = (TextView) findViewById(R.id.rightButton);
        mToListViewTopClick.setVisibility(View.VISIBLE);
        mTitleName.setText("往期");
        mToListViewTopClick.setText("今天");
        loadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                if (CommonUtil.checkNetWork()) {
                    errorView.setVisibility(View.GONE);
                    loadMessageView.setVisibility(View.VISIBLE);
                    initData();
                    setListener();
                }
            }
        });
        if (!CommonUtil.checkNetWork()) {
            loadMessageView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            initData();
            setListener();
        }
    }

    private void initData() {
//		GeneralUtil.showProgressDialog(TimeLineActivity.this);
        loadMessageView.setVisibility(View.VISIBLE);
        new GetGuideListDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mToListViewTopClick.setOnClickListener(this);
//		mPullToRefreshListView.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				//标识正在滑动中
//				if(scrollState==OnScrollListener.SCROLL_STATE_FLING){
//					isBusy=true;
//				}else {
//					isBusy=false;
//					isShow = true;
//				}
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				firstItem=firstVisibleItem;
//				bottmItem=firstItem+visibleItemCount;
//			}
//		});

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.leftButton:
                finish();
                break;
            case R.id.rightButton:
                mPullToRefreshListView.setSelection(0);
                break;

            default:
                break;
        }
    }

    private Long guideLastId = 0l;


    private class GetGuideListDataTask extends AsyncTask<Object, Object, List<Guide>> {
        private GuideService guideService = new GuideService();

        @Override
        protected List<Guide> doInBackground(Object... params) {
            List<Guide> guides = guideService.getGuideShortList(guideLastId, 2, 128);
            return guides;
        }

        protected void onPostExecute(List<Guide> result) {
            super.onPostExecute(result);
            if (result != null && result.size() > 0) {
                guideList.addAll(result);
                mAdapter = new TimeLineAdapter(TimeLineActivity.this, groupList(guideList));
                mPullToRefreshListView.setAdapter(mAdapter);
//				mAdapter.setData(groupList(guideList));
//				mAdapter.notifyDataSetChanged();
                guideLastId = result.get(result.size() - 1).getGuideId();
                if (MyApplication.isLogin()) {
                    new GetGuideFavoriteTask(result).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else {
                GeneralUtil.showToastShort(getBaseContext(), ToastMessage.NOT_FOUND_GUIDE_LIST);
            }

//			GeneralUtil.cancelProgressDialog();
            loadMessageView.setVisibility(View.GONE);
        }

    }

    /**
     * @author 杨腾
     * @ClassName: GetDataTask
     * @Description: 异步获取导购是否收藏列表
     * @date 2015年4月13日 上午11:15:49
     */
    private class GetGuideFavoriteTask extends AsyncTask<Object, Object, List<Guide>> {
        private FavoriteService favoriteService = new FavoriteService();
        private List<Guide> guideList;

        public GetGuideFavoriteTask(List<Guide> guideList) {
            this.guideList = guideList;
        }

        @Override
        protected List<Guide> doInBackground(Object... params) {
            StringBuffer guideIds = new StringBuffer();
            for (int i = 0; i < guideList.size(); i++) {
                Guide guide = guideList.get(i);
                if (i < guideList.size() - 1) {
                    guideIds.append(guide.getGuideId() + "_");
                } else {
                    guideIds.append(guide.getGuideId());
                }

            }
            return favoriteService.getFavoriteIds(guideIds.toString());
        }

        protected void onPostExecute(List<Guide> result) {
            super.onPostExecute(result);
            if (result != null && result.size() > 0) {
                for (Guide guide : guideList) {
                    for (Guide resultGuide : result) {
                        if (guide.getGuideId() == resultGuide.getGuideId()) {
                            guide.setFavoriteId(resultGuide.getFavoriteId());
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    public static final int today = 0;
    public static final int yesterday = 1;
    public static final int beforeYesterday = 2;
    public static final int three_days_ago = 3;
    public static final int four_days_ago = 4;
    public static final int five_days_ago = 5;
    public static final int six_days_ago = 6;
    public static final int seven_days_ago = 7;


    private HashMap<Integer, List<Guide>> groupList(List<Guide> data) {
        HashMap<Integer, List<Guide>> result = new HashMap<Integer, List<Guide>>();
        result.put(TimeLineActivity.today, new ArrayList<Guide>());
        result.put(TimeLineActivity.yesterday, new ArrayList<Guide>());
        result.put(TimeLineActivity.beforeYesterday, new ArrayList<Guide>());
        result.put(TimeLineActivity.three_days_ago, new ArrayList<Guide>());
        result.put(TimeLineActivity.four_days_ago, new ArrayList<Guide>());
        result.put(TimeLineActivity.five_days_ago, new ArrayList<Guide>());
        result.put(TimeLineActivity.six_days_ago, new ArrayList<Guide>());
        result.put(TimeLineActivity.seven_days_ago, new ArrayList<Guide>());

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        String yesterday = sdf.format(calendar.getTime()); // 昨天

        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -2);
        String beforeYesterday = sdf.format(calendar.getTime()); // 前天

        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -3);
        String mThree_days_ago = sdf.format(calendar.getTime()); //3天前

        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -4);
        String mFour_days_ago = sdf.format(calendar.getTime()); //4天前

        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -5);
        String mFive_days_ago = sdf.format(calendar.getTime()); //5天前

        calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -6);
        String mSix_days_ago = sdf.format(calendar.getTime()); //6天前

//		calendar = java.util.Calendar.getInstance();
//		calendar.roll(Calendar.DAY_OF_YEAR, -7);
//		String mSeven_days_ago = sdf.format(calendar.getTime()); //7天前

        String today = sdf.format(new Date().getTime()); // 今天
        int dataSize = data.size();
        for (int i = 0; i < dataSize; i++) {
            String sysDate = sdf.format(data.get(i).getLastUpdateTime()); // 系统时间
            if (today.equals(sysDate)) { // 如果是今天
                result.get(TimeLineActivity.today).add(data.get(i));
            } else if (yesterday.equals(sysDate)) { // 如果是昨天
                result.get(TimeLineActivity.yesterday).add(data.get(i));
            } else if (beforeYesterday.equals(sysDate)) {
                result.get(TimeLineActivity.beforeYesterday).add(data.get(i));
            } else if (mThree_days_ago.equals(sysDate)) {
                result.get(TimeLineActivity.three_days_ago).add(data.get(i));
            } else if (mFour_days_ago.equals(sysDate)) {
                result.get(TimeLineActivity.four_days_ago).add(data.get(i));
            } else if (mFive_days_ago.equals(sysDate)) {
                result.get(TimeLineActivity.five_days_ago).add(data.get(i));
            } else if (mSix_days_ago.equals(sysDate)) {
                result.get(TimeLineActivity.six_days_ago).add(data.get(i));
            } else/* if(mSeven_days_ago.equals(sysDate))*/ {
                result.get(TimeLineActivity.seven_days_ago).add(data.get(i));
            }
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (MyApplication.isLogin()) {
                Long backGuideId = data.getLongExtra("guideId", 0);
                new GetGuideFavoriteByIdTask(backGuideId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    ;

    private class GetGuideFavoriteByIdTask extends AsyncTask<Object, Object, Guide> {
        private FavoriteService favoriteService = new FavoriteService();
        private GuideService guideService = new GuideService();
        private Long backGuideId;

        public GetGuideFavoriteByIdTask(Long guideId) {
            this.backGuideId = guideId;
        }

        @Override
        protected Guide doInBackground(Object... params) {
            List<Guide> guides = favoriteService.getFavoriteIds(backGuideId + "");
            Guide tempGuide = guideService.getGuideDetail(backGuideId);
            if (guides != null && guides.size() > 0) {
                tempGuide.setFavoriteId(guides.get(0).getFavoriteId());
            }
            return tempGuide;
        }

        protected void onPostExecute(Guide result) {
            super.onPostExecute(result);
            if (result != null) {
                for (Guide guide : guideList) {
                    if (result.getGuideId() == guide.getGuideId()) {
                        guide.setFavoriteCount(result.getFavoriteCount());
                        guide.setFavoriteId(result.getFavoriteId());
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void finishActivity() {
    }


    class TimeLineAdapter extends BaseAdapter implements OnClickListener {

        private HashMap<Integer, List<Guide>> mList;
        private Context mContext;
        List<Guide> sevenBody = new ArrayList<Guide>();

        public TimeLineAdapter(Context context, HashMap<Integer, List<Guide>> map) {
            super();
            this.mContext = context;
            this.mList = map;
            sevenBody = mList.get(TimeLineActivity.seven_days_ago);
        }

        private TimeLineChildAdapter mChildAdapter;


        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            } else {
                return mList.size();
            }
        }

        public void setData(HashMap<Integer, List<Guide>> body) {
            this.mList = body;
        }

        @Override
        public Object getItem(int position) {
            if (mList == null || position < 0 || position > mList.size()) {
                return null;
            } else {
                return mList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_time_line_activity, null);
                holder.one_status_name0 = (TextView) convertView.findViewById(R.id.one_status_name0);
                holder.one_status_name1 = (TextView) convertView.findViewById(R.id.one_status_name1);
                holder.one_status_name2 = (TextView) convertView.findViewById(R.id.one_status_name2);
                holder.one_status_name3 = (TextView) convertView.findViewById(R.id.one_status_name3);
                holder.one_status_name4 = (TextView) convertView.findViewById(R.id.one_status_name4);
                holder.one_status_name5 = (TextView) convertView.findViewById(R.id.one_status_name5);
                holder.one_status_name6 = (TextView) convertView.findViewById(R.id.one_status_name6);
                holder.one_status_name7 = (TextView) convertView.findViewById(R.id.one_status_name7);
                holder.mDate = (TextView) convertView.findViewById(R.id.item_time_line_date);
                holder.mItemBg = (LinearLayout) convertView.findViewById(R.id.itemline_bg);

                holder.mLinearLayout = (View) convertView.findViewById(R.id.linearLayout1);

                holder.likeText0 = (TextView) convertView.findViewById(R.id.item_timeline_like0_text);
                holder.likeText1 = (TextView) convertView.findViewById(R.id.item_timeline_like1_text);
                holder.likeText2 = (TextView) convertView.findViewById(R.id.item_timeline_like2_text);
                holder.likeText3 = (TextView) convertView.findViewById(R.id.item_timeline_like3_text);
                holder.likeText4 = (TextView) convertView.findViewById(R.id.item_timeline_like4_text);
                holder.likeText5 = (TextView) convertView.findViewById(R.id.item_timeline_like5_text);
                holder.likeText6 = (TextView) convertView.findViewById(R.id.item_timeline_like6_text);
                holder.likeText7 = (TextView) convertView.findViewById(R.id.item_timeline_like7_text);

                holder.mItemRelativeLayout01 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_01_rl);
                holder.mItemRelativeLayout02 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_02_rl);
                holder.mItemRelativeLayout03 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_03_rl);
                holder.mItemRelativeLayout04 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_04_rl);
                holder.mItemRelativeLayout05 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_05_rl);
                holder.mItemRelativeLayout06 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_06_rl);
                holder.mItemRelativeLayout07 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_07_rl);
                holder.mItemRelativeLayout08 = (RelativeLayout) convertView.findViewById(R.id.item_timeline_08_rl);

                holder.mLikeImg0 = (ImageView) convertView.findViewById(R.id.item_timeline_like0_img);
                holder.mLikeImg1 = (ImageView) convertView.findViewById(R.id.item_timeline_like1_img);
                holder.mLikeImg2 = (ImageView) convertView.findViewById(R.id.item_timeline_like2_img);
                holder.mLikeImg3 = (ImageView) convertView.findViewById(R.id.item_timeline_like3_img);
                holder.mLikeImg4 = (ImageView) convertView.findViewById(R.id.item_timeline_like4_img);
                holder.mLikeImg5 = (ImageView) convertView.findViewById(R.id.item_timeline_like5_img);
                holder.mLikeImg6 = (ImageView) convertView.findViewById(R.id.item_timeline_like6_img);
                holder.mLikeImg7 = (ImageView) convertView.findViewById(R.id.item_timeline_like7_img);

                holder.mChildListView = (ListView) convertView.findViewById(R.id.lv_contain);
                holder.mChildLinearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout2);
                holder.mLoadMore = (TextView) convertView.findViewById(R.id.load_more);

                holder.mLoadMoreToast = convertView.findViewById(R.id.load_more_toast);


                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (position) {
                case 0:
//				isShow = true;
                    holder.mDate.setText("今天");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> todayBody = mList.get(TimeLineActivity.today);
                    if (todayBody != null && todayBody.size() > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < todayBody.size(); i++) {
                        if (todayBody.size() >= 8) {
                            size8Body(holder, todayBody);
                        } else if (todayBody.size() == 1) {
                            size1Body(holder, todayBody);
                        } else if (todayBody.size() == 2) {
                            size2Body(holder, todayBody);
                        } else if (todayBody.size() == 3) {
                            size3Body(holder, todayBody);
                        } else if (todayBody.size() == 4) {
                            size4Body(holder, todayBody);
                        } else if (todayBody.size() == 5) {
                            size5Body(holder, todayBody);
                        } else if (todayBody.size() == 6) {
                            size6Body(holder, todayBody);
                        } else if (todayBody.size() == 7) {
                            size7Body(holder, todayBody);
                        }
//					}
                        itemClickImp(holder, todayBody);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                case 1:
//				isShow = true;
                    holder.mDate.setText("昨天");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> yesBody = mList.get(TimeLineActivity.yesterday);
                    if (yesBody != null && yesBody.size() > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < yesBody.size(); i++) {
                        if (yesBody.size() >= 8) {
                            size8Body(holder, yesBody);
                        } else if (yesBody.size() == 1) {
                            size1Body(holder, yesBody);
                        } else if (yesBody.size() == 2) {
                            size2Body(holder, yesBody);
                        } else if (yesBody.size() == 3) {
                            size3Body(holder, yesBody);
                        } else if (yesBody.size() == 4) {
                            size4Body(holder, yesBody);
                        } else if (yesBody.size() == 5) {
                            size5Body(holder, yesBody);
                        } else if (yesBody.size() == 6) {
                            size6Body(holder, yesBody);
                        } else if (yesBody.size() == 7) {
                            size7Body(holder, yesBody);
//							break;
                        }
//					}
                        itemClickImp(holder, yesBody);
//						  AyncImgImp(1, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                case 2:
//				isShow = true;
                    holder.mDate.setText("前天");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> beforeYesterdayBody = mList.get(TimeLineActivity.beforeYesterday);
                    if (beforeYesterdayBody != null && beforeYesterdayBody.size() > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < beforeYesterdayBody.size(); i++) {
                        if (beforeYesterdayBody.size() >= 8) {
                            size8Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 1) {
                            size1Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 2) {
                            size2Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 3) {
                            size3Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 4) {
                            size4Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 5) {
                            size5Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 6) {
                            size6Body(holder, beforeYesterdayBody);
                        } else if (beforeYesterdayBody.size() == 7) {
                            size7Body(holder, beforeYesterdayBody);
                        }
//					}
                        itemClickImp(holder, beforeYesterdayBody);
//						  AyncImgImp(2, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                case 3:
//				isShow = true;
                    holder.mDate.setText("三天前");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> threeDayAgoBody = mList.get(TimeLineActivity.three_days_ago);
                    if (threeDayAgoBody != null && threeDayAgoBody.size() > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < threeDayAgoBody.size(); i++) {
                        if (threeDayAgoBody.size() >= 8) {
                            size8Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 1) {
                            size1Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 2) {
                            size2Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 3) {
                            size3Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 4) {
                            size4Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 5) {
                            size5Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 6) {
                            size6Body(holder, threeDayAgoBody);
                        } else if (threeDayAgoBody.size() == 7) {
                            size7Body(holder, threeDayAgoBody);
                        }
//					}
                        itemClickImp(holder, threeDayAgoBody);
//					  AyncImgImp(3, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                case 4:
//				isShow = true;
                    holder.mDate.setText("四天前");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> fourBody = mList.get(TimeLineActivity.four_days_ago);
                    if (fourBody != null && fourBody.size() > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < fourBody.size(); i++) {
                        if (fourBody.size() >= 8) {
                            size8Body(holder, fourBody);
                        } else if (fourBody.size() == 1) {
                            size1Body(holder, fourBody);
                        } else if (fourBody.size() == 2) {
                            size2Body(holder, fourBody);
                        } else if (fourBody.size() == 3) {
                            size3Body(holder, fourBody);
                        } else if (fourBody.size() == 4) {
                            size4Body(holder, fourBody);
                        } else if (fourBody.size() == 5) {
                            size5Body(holder, fourBody);
                        } else if (fourBody.size() == 6) {
                            size6Body(holder, fourBody);
                        } else if (fourBody.size() == 7) {
                            size7Body(holder, fourBody);
                        }
//					}
                        itemClickImp(holder, fourBody);
//					  AyncImgImp(4, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                case 5:
//				isShow = true;
                    holder.mDate.setText("五天前");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> fiveBody = mList.get(TimeLineActivity.five_days_ago);
                    int fiveSize = fiveBody.size();
                    if (fiveBody != null && fiveSize > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < fiveSize; i++) {
                        if (fiveSize >= 8) {
                            size8Body(holder, fiveBody);
                        } else if (fiveSize == 1) {
                            size1Body(holder, fiveBody);
                        } else if (fiveSize == 2) {
                            size2Body(holder, fiveBody);
                        } else if (fiveSize == 3) {
                            size3Body(holder, fiveBody);
                        } else if (fiveSize == 4) {
                            size4Body(holder, fiveBody);
                        } else if (fiveSize == 5) {
                            size5Body(holder, fiveBody);
                        } else if (fiveSize == 6) {
                            size6Body(holder, fiveBody);
                        } else if (fiveSize == 7) {
                            size7Body(holder, fiveBody);
                        }
//					}
                        itemClickImp(holder, fiveBody);
//					  AyncImgImp(5, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }

                    break;
                case 6:
//				isShow = true;
                    holder.mDate.setText("六天前");
                    holder.mChildLinearLayout.setVisibility(View.GONE);
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.GONE);
                    List<Guide> sixBody = mList.get(TimeLineActivity.six_days_ago);
                    int sixSize = sixBody.size();
                    if (sixBody != null && sixSize > 0) {
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
//					for (int i = 0; i < sixSize; i++) {
                        if (sixSize >= 8) {
                            size8Body(holder, sixBody);
                        } else if (sixSize == 1) {
                            size1Body(holder, sixBody);
                        } else if (sixSize == 2) {
                            size2Body(holder, sixBody);
                        } else if (sixSize == 3) {
                            size3Body(holder, sixBody);
                        } else if (sixSize == 4) {
                            size4Body(holder, sixBody);
                        } else if (sixSize == 5) {
                            size5Body(holder, sixBody);
                        } else if (sixSize == 6) {
                            size6Body(holder, sixBody);
                        } else if (sixSize == 7) {
                            size7Body(holder, sixBody);
                        }
//					}
                        itemClickImp(holder, sixBody);
//					  AyncImgImp(6, bitmap, holder);
                    } else {
                        holder.mLinearLayout.setVisibility(View.GONE);
                    }

                    break;
                case 7:
                    holder.mLinearLayout.setVisibility(View.GONE);
//				if(isShow){
                    if (sevenBody.size() > 0) {
                        LoadMoreDatas(holder, sevenBody);
                    }
//				}
                    break;

                default:
                    break;
            }

            return convertView;
        }

        private void LoadMoreDatas(final ViewHolder holder, List<Guide> sevenBody) {
            holder.mChildLinearLayout.setVisibility(View.VISIBLE);
            if (mChildAdapter == null) {
                mChildAdapter = new TimeLineChildAdapter(mContext, sevenBody);
            }
            holder.mLoadMore.setVisibility(View.VISIBLE);
            holder.mChildListView.setAdapter(mChildAdapter);
            LvHeightUtil.setListViewHeightBasedOnChildren(holder.mChildListView);
            holder.mChildListView.setOnItemClickListener(SevenDayAgoOnItemClick());
            holder.mLoadMore.setOnClickListener(loadMoreData(holder));
//			notifyDataSetChanged();
            isShow = false;
        }

        ;

        private OnClickListener loadMoreData(final ViewHolder holder) {
            return new OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.mLoadMore.setVisibility(View.GONE);
                    holder.mLoadMoreToast.setVisibility(View.VISIBLE);
                    if (CommonUtil.checkNetWork()) {
                        isShow = true;
                        new GetGuideListDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        holder.mLoadMoreToast.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        holder.mLoadMoreToast.setVisibility(View.GONE);
                        GeneralUtil.showToastShort(getApplicationContext(), ToastMessage.NET_WORK_NOT_WORK);
                    }
                }
            };
        }

        private long mLastClickTime;

        private OnItemClickListener SevenDayAgoOnItemClick() {
            return new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0,
                                        View arg1, int arg2, long arg3) {
                    long currentTime = new Date().getTime();
                    if (currentTime - mLastClickTime > 1000) {
                        Guide mdata = (Guide) arg0.getItemAtPosition(arg2);
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, mdata, TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    mLastClickTime = currentTime;
                }
            };
        }

        private void itemClickImp(ViewHolder holder, List<Guide> body) {
            holder.mItemRelativeLayout01.setTag(body);
            holder.mItemRelativeLayout01.setOnClickListener(this);
            holder.mItemRelativeLayout02.setTag(body);
            holder.mItemRelativeLayout02.setOnClickListener(this);
            holder.mItemRelativeLayout03.setTag(body);
            holder.mItemRelativeLayout03.setOnClickListener(this);
            holder.mItemRelativeLayout04.setTag(body);
            holder.mItemRelativeLayout04.setOnClickListener(this);
            holder.mItemRelativeLayout05.setTag(body);
            holder.mItemRelativeLayout05.setOnClickListener(this);
            holder.mItemRelativeLayout06.setTag(body);
            holder.mItemRelativeLayout06.setOnClickListener(this);
            holder.mItemRelativeLayout07.setTag(body);
            holder.mItemRelativeLayout07.setOnClickListener(this);
            holder.mItemRelativeLayout08.setTag(body);
            holder.mItemRelativeLayout08.setOnClickListener(this);
        }

        private void size8Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.one_status_name3.setText(body.get(3).getTitle());
            holder.one_status_name4.setText(body.get(4).getTitle());
            holder.one_status_name5.setText(body.get(5).getTitle());
            holder.one_status_name6.setText(body.get(6).getTitle());
            holder.one_status_name7.setText(body.get(7).getTitle());
            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            if (body.get(3).getFavoriteId() > 0) {
                holder.mLikeImg3.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg3.setImageResource(R.drawable.like_nor);
            }
            if (body.get(4).getFavoriteId() > 0) {
                holder.mLikeImg4.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg4.setImageResource(R.drawable.like_nor);
            }
            if (body.get(5).getFavoriteId() > 0) {
                holder.mLikeImg5.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg5.setImageResource(R.drawable.like_nor);
            }
            if (body.get(6).getFavoriteId() > 0) {
                holder.mLikeImg6.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg6.setImageResource(R.drawable.like_nor);
            }
            if (body.get(7).getFavoriteId() > 0) {
                holder.mLikeImg7.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg7.setImageResource(R.drawable.like_nor);
            }
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            holder.likeText3.setText(body.get(3).getFavoriteCount() + "");
            holder.likeText4.setText(body.get(4).getFavoriteCount() + "");
            holder.likeText5.setText(body.get(5).getFavoriteCount() + "");
            holder.likeText6.setText(body.get(6).getFavoriteCount() + "");
            holder.likeText7.setText(body.get(7).getFavoriteCount() + "");
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.VISIBLE);
        }

        private void size1Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size2Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size3Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size4Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.one_status_name3.setText(body.get(3).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            holder.likeText3.setText(body.get(3).getFavoriteCount() + "");

            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            if (body.get(3).getFavoriteId() > 0) {
                holder.mLikeImg3.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg3.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size5Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.one_status_name3.setText(body.get(3).getTitle());
            holder.one_status_name4.setText(body.get(4).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            holder.likeText3.setText(body.get(3).getFavoriteCount() + "");
            holder.likeText4.setText(body.get(4).getFavoriteCount() + "");

            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            if (body.get(3).getFavoriteId() > 0) {
                holder.mLikeImg3.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg3.setImageResource(R.drawable.like_nor);
            }
            if (body.get(4).getFavoriteId() > 0) {
                holder.mLikeImg4.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg4.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size6Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.one_status_name3.setText(body.get(3).getTitle());
            holder.one_status_name4.setText(body.get(4).getTitle());
            holder.one_status_name5.setText(body.get(5).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            holder.likeText3.setText(body.get(3).getFavoriteCount() + "");
            holder.likeText4.setText(body.get(4).getFavoriteCount() + "");
            holder.likeText5.setText(body.get(5).getFavoriteCount() + "");

            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            if (body.get(3).getFavoriteId() > 0) {
                holder.mLikeImg3.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg3.setImageResource(R.drawable.like_nor);
            }
            if (body.get(4).getFavoriteId() > 0) {
                holder.mLikeImg4.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg4.setImageResource(R.drawable.like_nor);
            }
            if (body.get(5).getFavoriteId() > 0) {
                holder.mLikeImg5.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg5.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.INVISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);
        }

        private void size7Body(ViewHolder holder, List<Guide> body) {
            holder.one_status_name0.setText(body.get(0).getTitle());
            holder.one_status_name1.setText(body.get(1).getTitle());
            holder.one_status_name2.setText(body.get(2).getTitle());
            holder.one_status_name3.setText(body.get(3).getTitle());
            holder.one_status_name4.setText(body.get(4).getTitle());
            holder.one_status_name5.setText(body.get(5).getTitle());
            holder.one_status_name6.setText(body.get(6).getTitle());
            holder.likeText0.setText(body.get(0).getFavoriteCount() + "");
            holder.likeText1.setText(body.get(1).getFavoriteCount() + "");
            holder.likeText2.setText(body.get(2).getFavoriteCount() + "");
            holder.likeText3.setText(body.get(3).getFavoriteCount() + "");
            holder.likeText4.setText(body.get(4).getFavoriteCount() + "");
            holder.likeText5.setText(body.get(5).getFavoriteCount() + "");
            holder.likeText6.setText(body.get(6).getFavoriteCount() + "");
            if (body.get(0).getFavoriteId() > 0) {
                holder.mLikeImg0.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg0.setImageResource(R.drawable.like_nor);
            }
            if (body.get(1).getFavoriteId() > 0) {
                holder.mLikeImg1.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg1.setImageResource(R.drawable.like_nor);
            }
            if (body.get(2).getFavoriteId() > 0) {
                holder.mLikeImg2.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg2.setImageResource(R.drawable.like_nor);
            }
            if (body.get(3).getFavoriteId() > 0) {
                holder.mLikeImg3.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg3.setImageResource(R.drawable.like_nor);
            }
            if (body.get(4).getFavoriteId() > 0) {
                holder.mLikeImg4.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg4.setImageResource(R.drawable.like_nor);
            }
            if (body.get(5).getFavoriteId() > 0) {
                holder.mLikeImg5.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg5.setImageResource(R.drawable.like_nor);
            }
            if (body.get(6).getFavoriteId() > 0) {
                holder.mLikeImg6.setImageResource(R.drawable.like_sel);
            } else {
                holder.mLikeImg6.setImageResource(R.drawable.like_nor);
            }
            holder.mItemRelativeLayout01.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout02.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout03.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout04.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout05.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout06.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout07.setVisibility(View.VISIBLE);
            holder.mItemRelativeLayout08.setVisibility(View.INVISIBLE);

        }


        public void setGonLoadMore(ViewHolder holder) {
            holder.mLoadMore.setVisibility(View.GONE);
        }

        private long lastClickTime0, lastClickTime1, lastClickTime2, lastClickTime3, lastClickTime4, lastClickTime5, lastClickTime6, lastClickTime7;

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View arg0) {
            List<Guide> item = (List<Guide>) arg0.getTag();
            Intent dIntent = new Intent();
            Bundle mBundle = new Bundle();
            long currentTime;
            switch (arg0.getId()) {
                case R.id.item_timeline_01_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime0 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(0), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime0 = currentTime;
                    break;
                case R.id.item_timeline_02_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime1 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(1), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime1 = currentTime;
                    break;
                case R.id.item_timeline_03_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime2 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(2), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime2 = currentTime;
                    break;
                case R.id.item_timeline_04_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime3 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(3), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime3 = currentTime;
                    break;
                case R.id.item_timeline_05_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime4 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(4), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime4 = currentTime;
                    break;
                case R.id.item_timeline_06_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime5 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(5), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime5 = currentTime;
                    break;
                case R.id.item_timeline_07_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime6 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(6), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime6 = currentTime;
                    break;
                case R.id.item_timeline_08_rl:
                    currentTime = new Date().getTime();
                    if (currentTime - lastClickTime7 > 1000) {
                        CommonUtil.forwardToGuideDetailForResult((TimeLineActivity) mContext, item.get(7), TimeLineActivity.CODE_TIMELINE_DETAILS);
                    }
                    lastClickTime7 = currentTime;
                    break;
                default:
                    break;
            }
        }
    }

    class ViewHolder {
        TextView one_status_name0, one_status_name1, one_status_name2, one_status_name3, one_status_name4,
                one_status_name5, one_status_name6, one_status_name7;
        TextView mDate;
        TextView likeText0, likeText1, likeText2, likeText3, likeText4,
                likeText5, likeText6, likeText7;
        LinearLayout mItemBg;
        RelativeLayout mItemRelativeLayout01, mItemRelativeLayout02, mItemRelativeLayout03, mItemRelativeLayout04, mItemRelativeLayout05, mItemRelativeLayout06, mItemRelativeLayout07, mItemRelativeLayout08;
        View mLinearLayout;
        ImageView mTimelineBg;
        ImageView mLikeImg0, mLikeImg1, mLikeImg2, mLikeImg3, mLikeImg4, mLikeImg5, mLikeImg6, mLikeImg7;
        ListView mChildListView;
        private LinearLayout mChildLinearLayout;
        private TextView mLoadMore;
        private View mLoadMoreToast;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
