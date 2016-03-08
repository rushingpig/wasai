

package com.seastar.wasai.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
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
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetPasswordActivity extends CanBeClosedActivity {
    EditText edit_passwd;
    Button buttonNext;
    ImageView backimg;
    CheckBox checkbox;
    String para_mob;
    WaitingDlg waiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setpasswd);
        waiting = new WaitingDlg(this);
        Intent intent = getIntent();
        para_mob = intent.getStringExtra("MOB");
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        edit_passwd = (EditText) findViewById(R.id.edit_passwd);
        edit_passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isContainChineseCharacters(edit_passwd.getText().toString())) {
                    Toast.makeText(SetPasswordActivity.this, "密码不能包含汉字，请重新输入", Toast.LENGTH_SHORT).show();
                    edit_passwd.setText("");
                }
            }
        });
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_passwd.length() <= 0) {
                    Toast.makeText(getApplicationContext(), ToastMessage.PLEASE_SET_PASSWORD,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                setPassword();
            }

        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    edit_passwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edit_passwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        backimg = (ImageView) findViewById(R.id.action_back);
        backimg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    void setPassword() {
        waiting.showWaitingDlg(true);
        User user = new User();
        user.setNickname(para_mob);
        user.setPassword(Encrypt.MD5(edit_passwd.getText().toString()));
        user.setPictureUrl("");
        user.setSex("male");
        user.setIdentifyId(para_mob);
        user.setIdentifyType("phone");
        createUser(user);
    }

    private void createUser(final User user) {
        String url = InterfaceConstant.USER;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
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

                        Intent intent = new Intent();
                        intent.setAction("REGISTER_SUCCESS");
                        SetPasswordActivity.this.sendBroadcast(intent);
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
        requestBody.put("password", Encrypt.MD5(edit_passwd.getText().toString()));
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }
}
