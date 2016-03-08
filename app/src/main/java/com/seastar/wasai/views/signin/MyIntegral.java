package com.seastar.wasai.views.signin;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.Entity.IntegrailExtraEntity;
import com.seastar.wasai.Entity.IntegralListEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.MyIntegralEntity;
import com.seastar.wasai.Entity.SigninTotalPointsEntity;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.adapters.MyIntegralAdapter;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jamie on 2015/6/19.
 */
public class MyIntegral extends BaseActivity implements View.OnClickListener{
    private MyIntegralAdapter mAdapter;
    private TextView mTitleName;
    private LinearLayout mBack;
    private TextView mRightBtn;
    private PullToRefreshListView mPullToRefreshListView;
    private SimpleMessageView mErrorView;
    private TextView mEmptyTextView;
    private LoadMessageView mLoadMessageView;
    private ListView mListView;

    private ImageView mUserPic;
    private TextView mUserName;
    private TextView mIntegralNum;
    private TextView mIntegralRule;
    private ImageView mTopBackground;

    private DisplayImageOptions imageDisplayOptions;

    private List<IntegralListEntity> listEntity = new ArrayList<IntegralListEntity>();
    private IntegrailExtraEntity mExtraEntity = new IntegrailExtraEntity();

    private PopupWindow mPopupWindow;
    private String gesture = "";
    public boolean isBottom = false;
    private PreferencesWrapper mPreferencesWrapper;
    public static final String TOTALPOINTS = "totalPoints";  //总积分
    public static final String RULE_URL = "rule_url";  //积分规则
    private String uuid = "";
    private long activityId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_integral_activity);
        imageDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        mPreferencesWrapper = new PreferencesWrapper(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            activityId = bundle.getLong("activityId");
        }
        initView();
        initData();
        setListener();
    }
    private void initView(){
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mRightBtn = (TextView) findViewById(R.id.rightButton);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.integral_pull_refresh_list);
        mLoadMessageView = (LoadMessageView) findViewById(R.id.container_load);
        mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
        mErrorView = (SimpleMessageView) findViewById(R.id.container_error);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        View headerView =  LayoutInflater.from(this).inflate(R.layout.integral_header, null);
        setHeaderView(headerView);
        mListView.addHeaderView(headerView);
        View footerView =  LayoutInflater.from(this).inflate(R.layout.integral_footer_view, null);
        mListView.addFooterView(footerView);

    }
    private void setHeaderView(View view){
        mUserPic = (ImageView) view.findViewById(R.id.avatar_img);
        mUserName = (TextView) view.findViewById(R.id.avatar_name);
        mIntegralNum = (TextView) view.findViewById(R.id.integral_num);
        mIntegralRule = (TextView) view.findViewById(R.id.integral_rule);
        mTopBackground = (ImageView) view.findViewById(R.id.integral_background);
        initHeaderViewData();
    }
    private void initHeaderViewData(){
        if (MyApplication.isLogin()) {
            User user = MyApplication.getCurrentUser();
            mUserName.setText(user.getNickname());
            ImageLoader.getInstance().displayImage(user.getPictureUrl(), mUserPic, imageDisplayOptions);
//            ImageLoader.getInstance().displayImage(user.getPictureUrl(), mTopBackground, imageDisplayOptions);
        }
        mIntegralRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wIntent = new Intent(MyIntegral.this, IntegralWebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MyIntegral.RULE_URL, mPreferencesWrapper.getStringValue(MyIntegral.RULE_URL, ""));
                wIntent.putExtras(bundle);
                startActivity(wIntent);
            }
        });
    }

    private void initData(){
        uuid = MyApplication.getCurrentUser().getUuid();
        mTitleName.setText("我的积分");
        mRightBtn.setText("积分记录");
        mAdapter = new MyIntegralAdapter(this);
        mPullToRefreshListView.setAdapter(mAdapter);
        mRightBtn.setVisibility(View.VISIBLE);
//        getSigninTotalPoints("c23362e0-f8c9-11e4-954c-d77c6d30a7cf");
        getIntegralList("down", "");
        getSigninTotalPoints(uuid);
    }
    private void setListener(){
        mBack.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MyIntegral.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getIntegralList("down", "");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MyIntegral.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                Gson gson = new Gson();
                getIntegralList("up", gson.toJson(mExtraEntity));
            }

        });
    }
    @Override
    public void finishActivity() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.leftButton:
                finish();
                break;
            case R.id.rightButton:
                Intent mIntent = new Intent(this,IntegralRecordActivity.class);
                startActivity(mIntent);
                break;
        }
    }

    private void getSigninTotalPoints(String uuid) {
        String url = InterfaceConstant.SIGIN_TOTALPOINTS + uuid;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    SigninTotalPointsEntity resultList = gson.fromJson(dataJsonStr, new TypeToken<SigninTotalPointsEntity>() {
                    }.getType());
                    mIntegralNum.setText(resultList.totalPoints + " 积分");
                    mPreferencesWrapper.setIntValueAndCommit(MyIntegral.TOTALPOINTS, resultList.totalPoints);
                    mPreferencesWrapper.setStringValueAndCommit(MyIntegral.RULE_URL,resultList.rule_url);
                } else {

                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null,null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    private void getIntegralList(final String gesture,final String extra) {
        final boolean isRefresh ;
        isRefresh = !gesture.equals("up");
        String url = InterfaceConstant.SIGIN_PRIZE_LIST + "gesture=" + gesture + "&extra=" + extra;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    MyIntegralEntity resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<MyIntegralEntity>() {
                            }.getType());
                    if(resultList != null){
                        if (resultList.list !=null && resultList.list.size() > 0) {
                            if (isRefresh) {
                                listEntity.clear();
                            }
                            listEntity.addAll(resultList.list);
                            mAdapter.setData(listEntity);
                            mExtraEntity = resultList.extra;
                            mAdapter.notifyDataSetChanged();
                            mEmptyTextView.setVisibility(View.INVISIBLE);
                        }else {
                            if (!isRefresh && listEntity.size() > 0) {
                                Toast.makeText(MyIntegral.this, ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                            } else {
                                listEntity.clear();
                                mAdapter.setData(listEntity);
                                mAdapter.notifyDataSetChanged();
                                mEmptyTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                mLoadMessageView.setVisibility(View.INVISIBLE);
                mPullToRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, mLoadMessageView, isRefresh ? mErrorView : null,mPullToRefreshListView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
    public void showPopupwindow(String text){
        initPopuptWindow(text);
        ColorDrawable dw = new ColorDrawable(0x7F000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(findViewById(R.id.header), Gravity.CENTER, 0, 0);
    }
    private void initPopuptWindow(String text) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.integral_popupwindow, null);
        TextView textView = (TextView) popupWindow.findViewById(R.id.text_view);
        textView.setText("您的积分还差 " + text +" 积分，请赚取到足够积分再来吧~");
        TextView leftBtn = (TextView) popupWindow.findViewById(R.id.left_button);
        TextView rightBtn = (TextView) popupWindow.findViewById(R.id.right_button);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wIntent = new Intent(MyIntegral.this,IntegralWebViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MyIntegral.RULE_URL, mPreferencesWrapper.getStringValue(MyIntegral.RULE_URL,""));
                wIntent.putExtras(bundle);
                startActivity(wIntent);
                mPopupWindow.dismiss();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopupWindow.setFocusable(true);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }

    public void showSuccessPopupwindow(String text){
        initSuccessPopuptWindow(text);
        ColorDrawable dw = new ColorDrawable(0x7F000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(findViewById(R.id.header), Gravity.CENTER, 0, 0);
    }
    private void initSuccessPopuptWindow(String text) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.integral_success_popupwindow, null);
        TextView textView = (TextView) popupWindow.findViewById(R.id.points_textview);
        textView.setText(text);
        TextView okBtn = (TextView) popupWindow.findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                getSigninTotalPoints(uuid);
            }
        });

        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopupWindow.setFocusable(true);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }



    /**
     * 临时的方法，积分兑换推出后，这个方法就没用了
     * @param text
     */
    public void showPopupwindowTemp(int itemId,int text){
        initPopuptWindowTemp(itemId, text);
        ColorDrawable dw = new ColorDrawable(0x7F000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(findViewById(R.id.header), Gravity.CENTER, 0, 0);
    }

    /**
     * 临时的方法，积分兑换推出后，这个方法就没用了
     */
    private void initPopuptWindowTemp(final int itemIdText,final int pointsText) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.integral_address_popupwindow, null);
        final EditText recipientsEt = (EditText) popupWindow.findViewById(R.id.recipients_et);
        final EditText phoneNumberEt = (EditText) popupWindow.findViewById(R.id.phone_num_et);
        final EditText addressEt = (EditText) popupWindow.findViewById(R.id.address_et);

        TextView leftBtn = (TextView) popupWindow.findViewById(R.id.left_button);
        TextView rightBtn = (TextView) popupWindow.findViewById(R.id.right_button);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneNumberEt.getText().toString()) || TextUtils.isEmpty(recipientsEt.getText().toString())
                        || TextUtils.isEmpty(addressEt.getText().toString())){
                    GeneralUtil.showToastShort(MyIntegral.this, "输入的信息不能为空");
                    return;
                }
                mPopupWindow.dismiss();
                exchangePostData(itemIdText,recipientsEt.getText().toString(),
                        phoneNumberEt.getText().toString(),
                        addressEt.getText().toString()
                ,pointsText);
            }
        });

        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }


    private void exchangePostData(int itemIdText,String recipientsText,String phoneNumberText,String addressText, final int pointsText){
        String url = InterfaceConstant.ACTIVITY_POINTS_EXCHANGE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                showSuccessPopupwindow(String.valueOf(pointsText));
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("uuid", uuid);
        requestBody.put("activityId", String.valueOf(activityId));
        requestBody.put("itemId", String.valueOf(itemIdText));
        requestBody.put("name", recipientsText);
        requestBody.put("mobile", phoneNumberText);
        requestBody.put("address", addressText);
        requestBody.put("points", String.valueOf(pointsText));
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.e("info", "onKeyDownonKeyDownonKeyDownonKeyDownonKeyDownonKeyDownonKeyDown");
            getSigninTotalPoints(uuid);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
