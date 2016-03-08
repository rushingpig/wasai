package com.seastar.wasai.views.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.DataCleanManager;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.base.BaseActivity;
import com.seastar.wasai.views.common.ContactActivity;
import com.seastar.wasai.views.common.HelpCenterActivity;
import com.seastar.wasai.views.common.SchoolActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SettingActionView;
import com.seastar.wasai.views.extendedcomponent.UpgradeDialog;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨腾
 * @ClassName: MoreActivity
 * @Description: 设置
 * @date 2015年5月11日 下午4:10:19
 */
public class MoreActivity extends BaseActivity implements View.OnClickListener {
    private SettingActionView questionView;
    private SettingActionView feedbackView;
//    private SettingActionView contributeView;
    private SettingActionView aboutView;
    private SettingActionView contactView;
    private SettingActionView checkUpdateView;
    private SettingActionView clearCacheView;
    private View actionBack;
    private Upgrade upgrade;
    private long lastClickTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        questionView = (SettingActionView) findViewById(R.id.setting_question);
        questionView.setOnClickListener(this);
        feedbackView = (SettingActionView) findViewById(R.id.setting_feedback);
        feedbackView.setOnClickListener(this);
//        contributeView = (SettingActionView) findViewById(R.id.setting_contribute);
//        contributeView.setOnClickListener(this);
        aboutView = (SettingActionView) findViewById(R.id.setting_about);
        aboutView.setOnClickListener(this);
        contactView = (SettingActionView) findViewById(R.id.setting_contact);
        contactView.setOnClickListener(this);
        clearCacheView = (SettingActionView) findViewById(R.id.setting_clear_cache);
        clearCacheView.setDescText(DataCleanManager.getTotalCacheSize(this));
        clearCacheView.setOnClickListener(this);
        checkUpdateView = (SettingActionView) findViewById(R.id.setting_check_update);
        checkUpdateView.setOnClickListener(this);
        actionBack = findViewById(R.id.action_back);
        actionBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createSession();

        questionView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        long currentTime = new Date().getTime();
        if (currentTime - lastClickTime > 1000) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.setting_question:
                    intent.setClass(MoreActivity.this, HelpCenterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting_feedback:
                    intent.setClass(MoreActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                    break;
//                case R.id.setting_contribute:
//                    intent.setClass(MoreActivity.this, SchoolActivity.class);
//                    startActivity(intent);
//                    break;
                case R.id.setting_about:
                    intent.setClass(MoreActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting_contact:
                    intent.setClass(MoreActivity.this, ContactActivity.class);
                    startActivity(intent);
                    break;
                case R.id.setting_clear_cache:
                    DataCleanManager.clearAllCache(MoreActivity.this);
                    clearCacheView.setDescText("0B");
                    Toast.makeText(MoreActivity.this, ToastMessage.SUCCESS_CLEAR_CACHE, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.setting_check_update:
                    if (upgrade != null && upgrade.getNeedUpdate()) {
                        UpgradeDialog dialog = new UpgradeDialog(MoreActivity.this, upgrade);
                        dialog.show();
                    } else {
                        Toast.makeText(MoreActivity.this, ToastMessage.VERSION_NEWEST, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        lastClickTime = currentTime;
    }

    @Override
    public void finishActivity() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        clearCacheView.setDescText(DataCleanManager.getTotalCacheSize(this));
    }

    /**
     * 创建session
     */
    private void createSession() {
        String url = InterfaceConstant.USERSESSION;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    upgrade = gson.fromJson(dataJsonStr, Upgrade.class);
                    if (upgrade.getNeedUpdate()) {
                        checkUpdateView.setDescText("检测到更" + upgrade.getVerString());
                    } else {
                        checkUpdateView.setDescText("当前已是最" + upgrade.getVerString());
                    }
                    Log.d(TAG, "创建session成功：" + dataJsonStr);
                } else {
                    Log.d(TAG, "创建session失败");
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("system", "android");
        requestBody.put("app_version", GeneralUtil.getAppVersionCode(this) + "");
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }
}
