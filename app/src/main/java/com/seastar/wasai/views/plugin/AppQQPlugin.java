package com.seastar.wasai.views.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.seastar.wasai.Entity.User;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppQQPlugin {
    private final static String TAG = "AppQQPlugin";
    private UserInfo mInfo;
    private Activity activity;
    private PluginEvent pluginEvent;
    private Tencent mTencent = null;

    public AppQQPlugin(Activity act, PluginEvent event) {
        this.activity = act;
        this.pluginEvent = event;
        mTencent = Tencent.createInstance(MyApplication.QQ_APPID, activity.getApplicationContext());
    }

    public boolean installed() {
        return CommonUtil.isInstalled(activity.getApplicationContext(), "com.tencent.mobileqq");
    }

    public boolean actionWithQQ(String action, JSONObject para) throws JSONException {
        try {
            if (action.equals("loginWithQQ")) {
                mTencent.login(activity, "all", loginListener);
                return true;
            } else if (action.equals("shareToQQFriend")) {
                final Bundle params = new Bundle();
                JSONObject temp = para;
                String title = temp.getString("title");
                String description = temp.getString("description");
                String picUrl = temp.getString("picUrl");
                String pageUrl = temp.getString("pageUrl");
                params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, picUrl);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, pageUrl);
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
                mTencent.shareToQQ(activity, params, qqShareListener);
                return true;
            } else if (action.equals("shareToQQZone")) {
                final Bundle params = new Bundle();
                int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
                JSONObject temp = para;
                String title = temp.getString("title");
                String description = temp.getString("description");
                String picUrl = temp.getString("picUrl");
                String pageUrl = temp.getString("pageUrl");
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, description);
                if (shareType != QzoneShare.SHARE_TO_QZONE_TYPE_APP) {
                    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, pageUrl);
                }
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add(picUrl);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                doShareToQzone(params);
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception :" + e.toString());
            return false;
        }
        return true;
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            User user = new User();
                            try {
                                String nickname = json.getString("nickname");
                                user.setNickname(nickname);
                                user.setPassword(null);
                                String figureUrl = json.getString("figureurl_qq_2");
                                user.setPictureUrl(figureUrl);
                                String gender = json.getString("gender");
                                if (gender.equals("男")) {
                                    gender = "male";
                                }
                                user.setSex(gender);
                                String openid = mTencent.getOpenId();
                                user.setIdentifyId(openid);
                                user.setIdentifyType("qq");
                                pluginEvent.actionResult(PluginEvent.RET_SUCCESS, user);
                            } catch (JSONException e) {
                                pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
                                Log.e(TAG, "createUser JSONException:" + e.toString());
                                e.printStackTrace();
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {
                    pluginEvent.actionResult(PluginEvent.RET_CANCEL, null);
                }
            };
            mInfo = new UserInfo(activity.getApplicationContext(), mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        }
    }

    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
        }

        @Override
        public void onCancel() {
            pluginEvent.actionResult(PluginEvent.RET_CANCEL,null);
        }
    };

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            pluginEvent.actionResult(PluginEvent.RET_CANCEL, null);
        }

        @Override
        public void onComplete(Object response) {
            pluginEvent.actionResult(PluginEvent.RET_SUCCESS, null);
        }

        @Override
        public void onError(UiError e) {
            pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
        }
    };

    IUiListener qZoneShareListener = new IUiListener() {

        @Override
        public void onCancel() {
            pluginEvent.actionResult(PluginEvent.RET_CANCEL, null);
        }

        @Override
        public void onError(UiError e) {
            pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
        }

        @Override
        public void onComplete(Object response) {
            pluginEvent.actionResult(PluginEvent.RET_SUCCESS, null);
        }
    };

    /**
     * 用异步方式启动分享
     *
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQzone(activity, params, qZoneShareListener);
            }
        }).start();
    }


    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
        }

        @Override
        public void onCancel() {

        }
    }
}
