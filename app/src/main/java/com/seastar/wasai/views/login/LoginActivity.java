package com.seastar.wasai.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.Encrypt;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.utils.VolleyExceptionHelper;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.plugin.AppQQPlugin;
import com.seastar.wasai.views.plugin.AppWechatPlugin;
import com.seastar.wasai.views.plugin.AppWeiboPlugin;
import com.seastar.wasai.views.plugin.PluginEvent;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends CanBeClosedActivity implements PluginEvent, View.OnClickListener {
    private String TAG = "LoginActivity";
    private EditText editMobile, editPassword;
    private Button buttonLogin;
    private TextView txtForgetPwd, txtRegister;
    private ImageView backBtn, sinaLoginBtn, qqLoginBtn, weixinLoginBtn;
    private AppWeiboPlugin weiboPlugin;
    private AppQQPlugin qqPlugin;
    private AppWechatPlugin weixinPlugin;
    private Handler mMainHandler = null;
    private WaitingDlg waiting;
    private String loginMode;
    private String weixinCode;

    @Override
    public void onResume() {
        super.onResume();
        if ((MyApplication.weixin_resp != null) && (MyApplication.weixin_resp.getType() == ConstantsAPI.COMMAND_SENDAUTH)) {
            BaseResp resp = MyApplication.weixin_resp;
            switch (resp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    weixinCode = ((SendAuth.Resp) MyApplication.weixin_resp).code;
                    getWechatAccessToken();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    actionResult(RET_CANCEL,null);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    actionResult(RET_ERROR,null);
                    break;
            }
        }
    }

    /**
     * 获取access_token的URL（微信）
     *
     * @return URL
     */
    private String getWechatAccessTokenUrl() {
        String result = InterfaceConstant.WX_ACCESS_TOKEN;
        result = result.replace("APPID",
                urlEnodeUTF8(MyApplication.WEIXIN_APP_ID));
        result = result.replace("SECRET",
                urlEnodeUTF8(MyApplication.WEIXIN_APP_SECRET));
        result = result.replace("CODE", urlEnodeUTF8(weixinCode));
        return result;
    }

    /**
     * 获取用户个人信息的URL（微信）
     *
     * @param access_token 获取access_token时给的
     * @param openid       获取access_token时给的
     * @return URL
     */
    public static String getWechatUserInfoUrl(String access_token, String openid) {
        String result = InterfaceConstant.WX_USER_INFO;
        result = result.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        result = result.replace("OPENID",
                urlEnodeUTF8(openid));
        return result;
    }

    public static String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getWechatAccessToken() {
        Response.Listener<JSONObject> sucessListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String accessToken = response.getString("access_token");
                    String openId = response.getString("openid");
                    getWechatUserInfo(accessToken, openId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyExceptionHelper.helper(error, null, null, null);
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getWechatAccessTokenUrl(), sucessListener, errorListener);
        MyApplication.addToRequestQueue(request);
    }

    private void getWechatUserInfo(String accessToken, final String openId) {
        Response.Listener<JSONObject> sucessListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    User user = new User();
                    user.setNickname(response.getString("nickname"));
                    user.setPassword(null);
                    user.setPictureUrl(response.getString("headimgurl"));
                    user.setSex(response.getInt("sex") == 1 ? "male" : "female");
                    user.setIdentifyId(openId);
                    user.setIdentifyType("weixin");
                    createUser(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyExceptionHelper.helper(error, null, null, null);
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getWechatUserInfoUrl(accessToken, openId), sucessListener, errorListener);
        MyApplication.addToRequestQueue(request);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        MyApplication.weixin_resp = null;
        waiting = new WaitingDlg(this);
        editMobile = (EditText) findViewById(R.id.edit_mob);
        editPassword = (EditText) findViewById(R.id.edit_password);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        txtForgetPwd = (TextView) findViewById(R.id.txtforgetpwd);
        txtForgetPwd.setOnClickListener(this);
        txtRegister = (TextView) findViewById(R.id.txtregiste);
        txtRegister.setOnClickListener(this);
        backBtn = (ImageView) findViewById(R.id.action_back);
        backBtn.setOnClickListener(this);
        sinaLoginBtn = (ImageView) findViewById(R.id.sina);
        sinaLoginBtn.setOnClickListener(this);
        qqLoginBtn = (ImageView) findViewById(R.id.qq);
        qqLoginBtn.setOnClickListener(this);
        weixinLoginBtn = (ImageView) findViewById(R.id.weixin);
        weixinLoginBtn.setOnClickListener(this);
        weiboPlugin = new AppWeiboPlugin(this, this);
        qqPlugin = new AppQQPlugin(this, this);
        weixinPlugin = new AppWechatPlugin(this, this);
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                waiting.showWaitingDlg(false);
                switch (msg.what) {
                    case RET_SUCCESS:
                        Bundle bundle = msg.getData();
                        createUser((User) bundle.getSerializable("user"));
                        break;
                    case RET_CANCEL:
                        Toast.makeText(getApplicationContext(), ToastMessage.CANCLE_TO_LOGIN,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case RET_ERROR:
                        Toast.makeText(getApplicationContext(), ToastMessage.FAILED_TO_LOGIN,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case RET_APP_NOT_EXIST:
                        String str = null;
                        if (loginMode.equals("qq")) {
                            str = ToastMessage.QQ_NOT_INSTALL_TO_LOGIN;
                        } else if (loginMode.equals("weibo")) {
                            str = ToastMessage.WEIBO_NOT_INSTALL_TO_LOGIN;
                        } else if (loginMode.equals("weixin")) {
                            str = ToastMessage.WECHAT_NOT_INSTALL_TO_LOGIN;
                        }
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    /**
     * 处理第三方登录回调
     *
     * @param status
     */
    public void actionResult(int status, Object data) {
        Message msg = mMainHandler.obtainMessage();
        msg.what = status;
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",(User)data);
        msg.setData(bundle);
        mMainHandler.sendMessage(msg);
    }

    private void login(String username, String password, String loginType) {
        String url = InterfaceConstant.LOGIN;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    User user = gson.fromJson(dataJsonStr, User.class);
                    if (user != null) {
                        MyApplication.setCurrentUser(user);
                        MyApplication.setJpushAlias(true);
                        Toast.makeText(getApplicationContext(), ToastMessage.SUCCESS_TO_LOGIN,
                                Toast.LENGTH_SHORT).show();
                        waiting.showWaitingDlg(false);
                        finish();
                    }
                }
                Log.d(TAG, "手机登录成功：" + dataJsonStr);
            }

            @Override
            public void doErrorResponse(JSONObject response) {
                waiting.showWaitingDlg(false);
                Toast.makeText(getApplicationContext(), ToastMessage.MOBILE_OR_PWD_WRONG,
                        Toast.LENGTH_SHORT).show();
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("identify_id", username);
        requestBody.put("identify_type", loginType);
        requestBody.put("password", StringUtil.isNotEmpty(password) ? Encrypt.MD5(password) : null);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (loginMode.equals("weibo")) {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
            weiboPlugin.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent handleWeiboResponse");
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        weiboPlugin.onNewIntent(intent);
    }

    private void createUser(final User user) {
        String url = InterfaceConstant.USER;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    String uuid = gson.fromJson(dataJsonStr, String.class);
                    user.setUuid(uuid);
                    login(user.getIdentifyId(), user.getPassword(), user.getIdentifyType());
                }
                Log.d(TAG, "创建用户成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> params = new HashMap<String, String>();
        params.put("identify_id", user.getIdentifyId());
        params.put("identify_type", user.getIdentifyType());
        params.put("nickname", user.getNickname());
        params.put("password", user.getPassword());
        params.put("picture_url", user.getPictureUrl());
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, params));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonLogin:
                if (editMobile.length() != 11 || editPassword.length() < 1) {
                    Toast.makeText(getApplicationContext(), ToastMessage.MOBILE_NO_WRONG,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editPassword.length() < 1) {
                    Toast.makeText(getApplicationContext(), ToastMessage.PLEASE_INPUT_PASSWORD,
                            Toast.LENGTH_SHORT).show();
                }
                waiting.showWaitingDlg(true);
                login(editMobile.getText().toString(), editPassword.getText().toString(), "phone");
                break;
            case R.id.txtforgetpwd:
                intent = new Intent();
                intent.setClass(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
                break;
            case R.id.txtregiste:
                intent = new Intent();
                intent.setClass(LoginActivity.this, RegisteActivity.class);
                startActivity(intent);
                break;
            case R.id.action_back:
                finish();
                break;
            case R.id.sina:
                if (!weiboPlugin.installed()) {
                    Toast.makeText(getApplicationContext(), ToastMessage.WEIBO_NOT_INSTALL_TO_LOGIN,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    waiting.showWaitingDlg(true);
                    loginMode = "weibo";
                    weiboPlugin.actionWithWeibo("loginWithWeibo", null);
                } catch (JSONException e) {
                    waiting.showWaitingDlg(false);
                    e.printStackTrace();
                }
                break;
            case R.id.qq:
                if (!qqPlugin.installed()) {
                    Toast.makeText(getApplicationContext(), ToastMessage.QQ_NOT_INSTALL_TO_LOGIN,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    waiting.showWaitingDlg(true);
                    loginMode = "qq";
                    qqPlugin.actionWithQQ("loginWithQQ", null);
                } catch (JSONException e) {
                    waiting.showWaitingDlg(false);
                    e.printStackTrace();
                }
                break;
            case R.id.weixin:
                if (!weixinPlugin.installed()) {
                    Toast.makeText(getApplicationContext(), ToastMessage.WECHAT_NOT_INSTALL_TO_LOGIN,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    waiting.showWaitingDlg(true);
                    loginMode = "weixin";
                    weixinPlugin.actionWithWeixin("loginWithWechat", null);
                } catch (JSONException e) {
                    waiting.showWaitingDlg(false);
                    e.printStackTrace();
                }
                break;
        }
    }
}
