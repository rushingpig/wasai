package com.seastar.wasai.views.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.AlipayBindEntity;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
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
public class BindAlipayActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public void finishActivity() {
    }

    private TextView mTitleName;
    private LinearLayout mBack;
    private EditText mZhifubaoAccount; //支付宝账号
    private TextView mCanUseTv; //可使用
    private TextView mBoundTv; //已绑定
    private EditText mPhoneNumEt; //输入的手机号码
    private Button mGetBindCodeBtn; //获取验证码
    private EditText mSecurityCode; //输入的验证码
    private Button mBindButton;
    private int recLen = 60;
    private Timer timer;
    private TextView mShowBoundPhoneNum;

    private String phoneNumber;
    private RelativeLayout mPhoneNumberRl;

    private String phoneNumberInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_alipay_or_mobile_activity);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            phoneNumber = bundle.getString("user_phone_number");
        }
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mTitleName = (TextView) findViewById(R.id.titleName);
        mBack = (LinearLayout) findViewById(R.id.leftButton);
        mZhifubaoAccount = (EditText) findViewById(R.id.zhifubao_account);
        mCanUseTv = (TextView) findViewById(R.id.can_use_tv);
        mBoundTv = (TextView) findViewById(R.id.bound_tv);
        mPhoneNumEt = (EditText) findViewById(R.id.phone_number);
        mGetBindCodeBtn = (Button) findViewById(R.id.get_bind_code_tv);
        mSecurityCode = (EditText) findViewById(R.id.security_code);
        mBindButton = (Button) findViewById(R.id.bind_button);
        mShowBoundPhoneNum = (TextView) findViewById(R.id.show_bound_phone_number);
        mPhoneNumberRl = (RelativeLayout) findViewById(R.id.phone_number_rl);
    }

    private void initData() {
        mBindButton.setEnabled(false);
        mTitleName.setText("绑定支付宝");
        if(StringUtil.isNotEmpty(phoneNumber)){
            mPhoneNumberRl.setVisibility(View.GONE);
            mShowBoundPhoneNum.setVisibility(View.VISIBLE);
            String maskNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length());
            mShowBoundPhoneNum.setText("绑定手机： " + maskNumber);
        }else {
            phoneNumberInput = mPhoneNumEt.getText().toString().trim();
            mPhoneNumberRl.setVisibility(View.VISIBLE);
            mShowBoundPhoneNum.setVisibility(View.GONE);
        }

    }

    private void setListener() {
        mBack.setOnClickListener(this);
        mGetBindCodeBtn.setOnClickListener(this);
        mBindButton.setOnClickListener(this);
        mSecurityCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6 && mZhifubaoAccount.getText().length() > 0) {
                    mBindButton.setBackgroundResource(R.drawable.rounded_confirm_bg);
                    mBindButton.setEnabled(true);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(mSecurityCode.getApplicationWindowToken(), 0);
                    }
                } else {
                    mBindButton.setBackgroundResource(R.drawable.rounded_confirm_bg_gray);
                    mBindButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                finish();
                break;
            case R.id.get_bind_code_tv: //获取验证码
                if(mZhifubaoAccount.getText().length() <= 0){
                    GeneralUtil.showToastShort(this, "请输入支付宝账号");
                    return;
                }

                String mAccount = mZhifubaoAccount.getText().toString().trim();
                if(mAccount.contains("@") || isMobileNum(mAccount)){
                    if(StringUtil.isNotEmpty(phoneNumber)){
                        GetSmsCode(phoneNumber);
                    }else {
                        phoneNumberInput = mPhoneNumEt.getText().toString().trim();
                        if(isMobileNum(phoneNumberInput)){
                            GetSmsCode(phoneNumberInput);
                        }else {
                            GeneralUtil.showToastShort(BindAlipayActivity.this, "请输入正确手机号");
                            return;
                        }
                    }
                    mGetBindCodeBtn.setEnabled(false);
                    mGetBindCodeBtn.setText("请在 " + recLen + " 秒内输入");
                    timer = new Timer();
                    timer.schedule(new RemindTask(), 1000, 1000);
                }else {
                    GeneralUtil.showToastShort(BindAlipayActivity.this, "请输入正确的邮箱号或者手机号");
                }

                break;

            case R.id.bind_button: //立即绑定
                if(StringUtil.isNotEmpty(phoneNumber)){
                    bindAlipay(phoneNumber);
                }else {
                    bindAlipay(phoneNumberInput);
                }


                break;
        }
    }
    private  boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();

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
                        mGetBindCodeBtn.setText("重新获取验证码");
                        mGetBindCodeBtn.setEnabled(true);
                        mGetBindCodeBtn.setClickable(true);
                    } else {
                        mGetBindCodeBtn.setText("请在 " + recLen + " 秒内输入");
                        mGetBindCodeBtn.setClickable(false);
                    }
                    break;
                default:
                    break;
            }

        }
    };


    private void GetSmsCode(String str) {
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

//            @Override
//            public void doErrorResponse(JSONObject response) {
//                try {
//                    if (response.getInt("code") == 10006) {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("mob", str);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    private void bindAlipay(final String num) {
        Log.e("info","Alipay- " +num);
        String url = InterfaceConstant.BIND_ALIPAY;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    AlipayBindEntity body = gson.fromJson(dataJsonStr, AlipayBindEntity.class);
                    if (body.result == 1) {
                        GeneralUtil.showToastShort(BindAlipayActivity.this, "绑定成功");
                        User user = MyApplication.getCurrentUser();
                        user.setMobile(num);
                        user.setAlipay(mZhifubaoAccount.getText().toString().trim());
                        MyApplication.setCurrentUser(user);
                        backWithdrawalsMainActivity(num);
                    } else if(body.result == 2){
                        GeneralUtil.showToastShort(BindAlipayActivity.this,"支付宝已被绑定过");
                    }else {
                        GeneralUtil.showToastShort(BindAlipayActivity.this,"手机号码已经被绑定过");
                    }
                    mSecurityCode.setText("");
                    timer.cancel();
                    recLen = 60;
                    mGetBindCodeBtn.setText("重新获取验证码");
                    mGetBindCodeBtn.setEnabled(true);
                    mGetBindCodeBtn.setClickable(true);
                }
            }
            @Override
            public void doErrorResponse(JSONObject response) throws JSONException {
//                super.doErrorResponse(response);
                int code = response.getInt("code");
                if(code == 10007){
                    GeneralUtil.showToastShort(BindAlipayActivity.this, ToastMessage.VERIFY_CODE_EXPIRED);
                }else if(code == 10005){
                    GeneralUtil.showToastShort(BindAlipayActivity.this, ToastMessage.VERIFY_CODE_WRONG);
                }
            }

        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("mobile", num);
        requestBody.put("alipay", mZhifubaoAccount.getText().toString().trim());
        requestBody.put("smscode", mSecurityCode.getText().toString().trim());
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }


    private void backWithdrawalsMainActivity(String str){
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("zhifubao_account", mZhifubaoAccount.getText().toString().trim());
        bundle.putString("phone_number", str);
        mIntent.putExtras(bundle);
        setResult(WithdrawalsMainActivity.BIND_ALIPAY_CODE, mIntent);
        finish();
    }
}
