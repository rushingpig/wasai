package com.seastar.wasai.views.wallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Line;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.WalletMainEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jamie on 2015/8/22.
 */
public class WithdrawalsMainActivity extends BaseActivity implements View.OnClickListener {
    private boolean imageViewBgIsDown = true;
    private RelativeLayout mDuihuan;
    private View mDuihuanLine;
    private ImageView mDuihuanArrow;
//    private TextView mDuihuanText1, mDuihuanText2;
    private TextView mTitleName;
    private LinearLayout mBack;

    private TextView mBindAccount;
    private TextView mGetSmscodeTv;
    private TextView mShowAccountTv;
    private TextView mShowPhoneNum;

    private EditText mGetSmscodeEt;
    private RelativeLayout mGetSmsCodeRl;
    private Button mExchangeBtn;

    private Timer timer;
    private  String phoneNum = ""; //手机号码
    private String alipayAccount = "";//支付宝账号
    private EditText mExchangeAmount;
    private TextView mWcoinTv;
    private PreferencesWrapper preferencesWrapper;
    public static final int BIND_ALIPAY_CODE = 00;
    private int recLen = 60;
    private String wcionaAmount;

    private PopupWindow mPopupWindow;

    private int mCashType;
    private LinearLayout initLinearLyaout;
    private ScrollView exchangeSuccessSV;
    private LinearLayout jfbSuccessLl;

    private TextView exchangeSuccessBack;

    private TextView duihuanHint;

    @Override
    public void finishActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdrawals_main_activity);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mCashType = bundle.getInt(ImmediateUseActivity.JIFENBAO_OR_ALIPAY);
        }
        initView();
        initData();
        setListener();
    }

    private void initView() {
        preferencesWrapper = new PreferencesWrapper(this);
        mDuihuan = (RelativeLayout) findViewById(R.id.duihuan_relativelayout);
        mDuihuanLine = findViewById(R.id.duihuan_line);
        mDuihuanArrow = (ImageView) findViewById(R.id.duihuan_arrow);
        duihuanHint = (TextView) findViewById(R.id.duihuan_hint);
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mBindAccount = (TextView) findViewById(R.id.binding_username);
        mGetSmscodeTv = (TextView) findViewById(R.id.get_sms_code_tv);
        mShowAccountTv = (TextView) findViewById(R.id.show_zhifubao_account_textview);
        mShowPhoneNum = (TextView) findViewById(R.id.show_phone_number);
        mGetSmsCodeRl = (RelativeLayout) findViewById(R.id.get_smscode_rl);
        mGetSmscodeEt = (EditText) findViewById(R.id.get_sms_code_et);
        mExchangeBtn = (Button) findViewById(R.id.exchange_btn);
        mExchangeAmount = (EditText) findViewById(R.id.exchange_amount);
        mWcoinTv = (TextView) findViewById(R.id.wcoin_tv);
        initLinearLyaout = (LinearLayout) findViewById(R.id.init_linearLayout);
        exchangeSuccessSV = (ScrollView) findViewById(R.id.success_scrollview);
        jfbSuccessLl = (LinearLayout) findViewById(R.id.jfb_success_ll);
        exchangeSuccessBack = (TextView) findViewById(R.id.exchange_success_back_tv);
        duihuanHint = (TextView) findViewById(R.id.duihuan_hint);
    }

    private void initData() {
        mTitleName.setText("立即兑换");
        mExchangeBtn.setEnabled(false);
        if(mCashType == 1){
            duihuanHint.setText(getResources().getString(R.string.duihuan_hint_jfb));
        }else {
            duihuanHint.setText(getResources().getString(R.string.duihuan_hint_alipay));
        }
        wcionaAmount = preferencesWrapper.getStringValue("data_wcoin","0");
        mWcoinTv.setText(wcionaAmount);
        mExchangeAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(wcionaAmount.length())});
        alipayAccount = MyApplication.getCurrentUser().getAlipay();
        phoneNum = MyApplication.getCurrentUser().getMobile();
        if(StringUtil.isNotEmpty(phoneNum)){
            if(alipayAccount != null && !"0".equals(alipayAccount)){
                isAlreadyboundAlipay(alipayAccount);
            }
        }

    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mDuihuan.setOnClickListener(this);
        mBindAccount.setOnClickListener(this);
        mGetSmscodeTv.setOnClickListener(this);
        mExchangeBtn.setOnClickListener(this);
        mGetSmscodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    mExchangeBtn.setBackgroundResource(R.drawable.rounded_confirm_bg);
                    mExchangeBtn.setEnabled(true);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(mGetSmscodeEt.getApplicationWindowToken(), 0);
                    }
                } else {
                    mExchangeBtn.setBackgroundResource(R.drawable.rounded_confirm_bg_gray);
                    mExchangeBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mExchangeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    if(Integer.parseInt(s.toString()) > Integer.parseInt(wcionaAmount)){
                        GeneralUtil.showToastShort(WithdrawalsMainActivity.this,"可用余额不足");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        exchangeSuccessBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                finish();
                break;

            case R.id.duihuan_relativelayout:
                isShowWentiView();
                break;
            case R.id.get_sms_code_tv:
                mGetSmscodeEt.setFocusable(true);
                mGetSmscodeEt.setFocusableInTouchMode(true);
                mGetSmscodeEt.requestFocus();
                mGetSmscodeEt.requestFocusFromTouch();
                if(mExchangeAmount.getText().length() <= 0){
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this, "兑换数量不能为空");
                    return;
                }
                int currentAmount = Integer.parseInt(mExchangeAmount.getText().toString());
                if (currentAmount >= 1000 && currentAmount%100 == 0) {
                    mGetSmscodeTv.setEnabled(false);
                    GetSmsCode();
                    mGetSmscodeTv.setText("请在 " + recLen + " 秒内输入");
                    timer = new Timer();
                    timer.schedule(new RemindTask(), 1000, 1000);
                }else {
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this,"兑换数量必须满1000以上且是100的倍数");
                }

                break;
            case R.id.binding_username:
                Intent intent = new Intent(this, BindAlipayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_phone_number",phoneNum);
                intent.putExtras(bundle);
                startActivityForResult(intent, BIND_ALIPAY_CODE);
                break;
            case R.id.exchange_btn:
                if(Integer.parseInt(mExchangeAmount.getText().toString()) >= 1000){
                    showPopupwindow();
                }else {
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this,"兑换金额必须为1000及以上");
                }

                break;
            case R.id.exchange_success_back_tv:
                Intent bIntent = new Intent(WithdrawalsMainActivity.this,WalletMainActivity.class);
                bIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bIntent);
                finish();
                break;
        }
    }


    private void isShowWentiView() {
        if (imageViewBgIsDown) {
            mDuihuanArrow.setBackgroundResource(R.drawable.sx_up);
            mDuihuanLine.setVisibility(View.VISIBLE);
            duihuanHint.setVisibility(View.VISIBLE);
//            mDuihuanText2.setVisibility(View.VISIBLE);
            imageViewBgIsDown = false;
        } else {
            mDuihuanArrow.setBackgroundResource(R.drawable.sx_down);
            mDuihuanLine.setVisibility(View.GONE);
            duihuanHint.setVisibility(View.GONE);
//            mDuihuanText2.setVisibility(View.GONE);
            imageViewBgIsDown = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode  == BIND_ALIPAY_CODE) {
            if(data != null){
                Bundle bundle = data.getExtras();
                alipayAccount = bundle.getString("zhifubao_account");
                phoneNum = bundle.getString("phone_number");
                isAlreadyboundAlipay(alipayAccount);
            }
        }

    }

    private void isAlreadyboundAlipay(String account) {
        mBindAccount.setVisibility(View.GONE);
        mGetSmsCodeRl.setVisibility(View.VISIBLE);
        mShowPhoneNum.setVisibility(View.VISIBLE);
        String maskAccount = "";
        if (account.contains("@")) {
            maskAccount = account.substring(0, 3) + "***" + account.substring(account.indexOf("@"), account.length());
        } else if(isMobileNum(account)){
            maskAccount = account.substring(0, 3) + "****" + account.substring(7, account.length());
        }else {
            maskAccount = account;
        }
        mShowAccountTv.setText(maskAccount);
        String maskNumber = phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length());
        mShowPhoneNum.setText("绑定手机： " + maskNumber);
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            if (recLen >= 0) {
                recLen--;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            } else {
                timer.cancel();
                recLen = 60;
            }
        }
    }

    final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (recLen <= 0) {
                        mGetSmscodeTv.setText("重新获取验证码");
                        mGetSmscodeTv.setEnabled(true);
                        mGetSmscodeTv.setClickable(true);
                    } else {
                        mGetSmscodeTv.setText("请在 " + recLen + " 秒内输入");
                        mGetSmscodeTv.setClickable(false);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    private void GetSmsCode() {
        String url = InterfaceConstant.SENDSMS;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                boolean isSuccess = false;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    isSuccess = gson.fromJson(dataJsonStr, Boolean.class);
                }
                if (isSuccess) {

                } else {

                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("mob", phoneNum);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    private void getExchangeData(int cashType) {
        String url = InterfaceConstant.EXCHANGE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                boolean isSuccess = false;
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    isSuccess = gson.fromJson(dataJsonStr, Boolean.class);
                }
                if (isSuccess) {
                    mGetSmscodeEt.setText("");
                    mExchangeAmount.setText("");
                    mPopupWindow.dismiss();
                    initLinearLyaout.setVisibility(View.GONE);
                    exchangeSuccessSV.setVisibility(View.VISIBLE);
                    if(mCashType == 1){
                        jfbSuccessLl.setVisibility(View.VISIBLE);
                    }else {
                        jfbSuccessLl.setVisibility(View.GONE);
                    }
                    getWalletData();
                } else {
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this,"兑换失败");
                }
                timer.cancel();
                recLen = 60;
                mGetSmscodeTv.setText("重新获取验证码");
                mGetSmscodeTv.setEnabled(true);
                mGetSmscodeTv.setClickable(true);
            }

            @Override
            public void doErrorResponse(JSONObject response) throws JSONException {
//                super.doErrorResponse(response);
                int code = response.getInt("code");
                if(code == 10007){
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this, ToastMessage.VERIFY_CODE_EXPIRED);
                }else if(code == 10005){
                    GeneralUtil.showToastShort(WithdrawalsMainActivity.this, ToastMessage.VERIFY_CODE_WRONG);
                }
                mPopupWindow.dismiss();
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("mobile", phoneNum);
        requestBody.put("alipay", alipayAccount);
        requestBody.put("number", mExchangeAmount.getText().toString().trim());
        requestBody.put("smscode", mGetSmscodeEt.getText().toString().trim());
        requestBody.put("cashtype", cashType+"");
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }


    private  boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }


    private void getWalletData() {
        String url = InterfaceConstant.FANLI_WALLET_AMOUNT + "uuid=" + MyApplication.getCurrentUser().getUuid();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    WalletMainEntity wallet = gson.fromJson(dataJsonStr, WalletMainEntity.class);
                    if (wallet != null) {
                        wcionaAmount = wallet.wcoin;
                        mWcoinTv.setText(wcionaAmount);
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void showPopupwindow(){
        initPopuptWindow();
        ColorDrawable dw = new ColorDrawable(0x7F000000);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.showAtLocation(findViewById(R.id.header), Gravity.CENTER, 0, 0);
    }

    private void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.confirm_exchange_popupwindow, null);
        TextView leftBtn = (TextView) popupWindow.findViewById(R.id.left_button);
        TextView rightBtn = (TextView) popupWindow.findViewById(R.id.right_button);
        TextView alipayCount = (TextView) popupWindow.findViewById(R.id.pop_alipay_account);
        TextView amountTv = (TextView) popupWindow.findViewById(R.id.pop_amount_tv);
        TextView wcoinTv = (TextView) popupWindow.findViewById(R.id.pop_wcoin_tv);
        alipayCount.setText(alipayAccount);
        amountTv.setText(mExchangeAmount.getText().toString().trim());
        wcoinTv.setText(mExchangeAmount.getText().toString().trim());

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExchangeData(mCashType);
            }
        });

        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }
}
