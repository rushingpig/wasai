package com.seastar.wasai.views.plugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.seastar.wasai.service.UserService;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.weibo.AccessTokenKeeper;
import com.seastar.wasai.weibo.Constants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class AppWeiboPlugin implements IWeiboHandler.Response {
    private final static String TAG = "AppWeiboPlugin";
    private AuthInfo mAuthInfo = null;
    JSONObject mShareParaJson = new JSONObject();
    private boolean testFlag = false;
    private Activity activity;
    private PluginEvent pluginEvent;

    private int mWeiboShareType = MyApplication.WEIBO_SHARE_ALL_IN_ONE;
    private IWeiboShareAPI mWeiboShareAPI = null;
    private Oauth2AccessToken mWeiboAccessToken = null;
    private SsoHandler mWeiboSsoHandler = null;

    public AppWeiboPlugin(Activity act, PluginEvent event) {
        this.activity = act;
        this.pluginEvent = event;
        mWeiboShareType = MyApplication.WEIBO_SHARE_ALL_IN_ONE;
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(act.getApplicationContext(), Constants.APP_KEY);
        mWeiboShareAPI.registerApp();
    }

    public boolean installed() {
        mAuthInfo = new AuthInfo(activity.getApplicationContext(), Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mWeiboSsoHandler = new SsoHandler(activity, mAuthInfo);
        if (mWeiboSsoHandler != null) {
            return mWeiboSsoHandler.isWeiboAppInstalled();
        } else {
            return false;
        }

    }

    public boolean actionWithWeibo(String action, JSONObject para) throws JSONException {
        mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), this);
        try {
            if (action.equals("loginWithWeibo")) {
                if (mAuthInfo == null) {
                    mAuthInfo = new AuthInfo(activity.getApplicationContext(), Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
                }
                if (mWeiboSsoHandler == null) {
                    mWeiboSsoHandler = new SsoHandler(activity, mAuthInfo);
                }
                mWeiboSsoHandler.authorize(new AuthListener());
                return true;
            } else if (action.equals("shareToWeibo")) {
                boolean hasText = true;
                boolean hasImage = true;
                boolean hasWebpage = true;
                boolean hasMusic = false;
                boolean hasVideo = false;
                boolean hasVoice = false;
                mShareParaJson = para;
                sendMessage(hasText, hasImage, hasWebpage,
                        hasMusic, hasVideo, hasVoice);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.toString());
            return false;
        }
        return true;
    }


    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            mWeiboAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mWeiboAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(activity.getApplicationContext(), mWeiboAccessToken);
                UsersAPI mUsersAPI = new UsersAPI(activity.getApplicationContext(), Constants.APP_KEY, mWeiboAccessToken);
                long uid = Long.parseLong(mWeiboAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
            }
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "onCancel ");
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    }


    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(final String response) {
            new Thread() {

                @Override
                public void run() {
                    if (!TextUtils.isEmpty(response)) {
                        com.sina.weibo.sdk.openapi.models.User weiboUser = User.parse(response);
                        if (weiboUser != null) {
                            com.seastar.wasai.Entity.User newUser = new com.seastar.wasai.Entity.User();
                            String nickname = weiboUser.screen_name;
                            newUser.setNickname(nickname);
                            String avatar_large = weiboUser.avatar_large;
                            newUser.setPictureUrl(avatar_large);
                            String gender = weiboUser.gender;
                            if (gender.equals("m")) {
                                gender = "male";
                            } else {
                                gender = "female";
                            }
                            newUser.setSex(gender);
                            String id = weiboUser.id;
                            newUser.setIdentifyId(id);
                            newUser.setIdentifyType("weibo");
                            pluginEvent.actionResult(PluginEvent.RET_SUCCESS, newUser);
                        } else {
                            pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
                        }
                    } else {
                        pluginEvent.actionResult(PluginEvent.RET_ERROR, null);
                    }
                }

            }.start();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };


    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     *
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage(boolean hasText, boolean hasImage,
                             boolean hasWebpage, boolean hasMusic, boolean hasVideo, boolean hasVoice) {
        int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
        if (mWeiboShareType == MyApplication.WEIBO_SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                    sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                } else {
                    sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
                }
            }
        } else if (mWeiboShareType == MyApplication.WEIBO_SHARE_ALL_IN_ONE) {
            sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo, hasVoice);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mWeiboShareType == MyApplication.WEIBO_SHARE_CLIENT) {
            mWeiboShareAPI.sendRequest(activity, request);
        } else if (mWeiboShareType == MyApplication.WEIBO_SHARE_ALL_IN_ONE) {
            AuthInfo authInfo = new AuthInfo(activity.getApplicationContext(), Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity.getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(activity, request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0) {

                }

                @Override
                public void onComplete(Bundle bundle) {
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(activity.getApplicationContext(), newToken);
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                   boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        Log.v(TAG, "sendSingleMessage ");
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getImageObj();
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj();
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj();
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        Log.v(TAG, "sendSingleMessage sendRequest");
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(activity, request);
    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();

        String title = "";
        try {
            title = mShareParaJson.getString("title");
        } catch (JSONException e) {
            Log.v(TAG, "JSONException:" + e.toString());
        }

        if (testFlag) {
            title = "测试 中美俄最先进军机释放干扰弹对比(组图)";
        }
        Log.v(TAG, "getTextObj:" + title);
        textObject.text = title;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        String picUrl = "";
        try {
            picUrl = mShareParaJson.getString("picUrl");
        } catch (JSONException e) {
            Log.v(TAG, "JSONException:" + e.toString());
        }
        if (testFlag) {
            picUrl = "http://pic.baike.soso.com/p/20100121/bki-20100121212916-812033732.jpg";
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(picUrl).openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            imageObject.setImageObject(bitmap);
        }
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        String title = "";
        String picUrl = "";
        String pageUrl = "";
        String description = "";
        String defaultText = "";
        try {
            title = mShareParaJson.getString("title");
            picUrl = mShareParaJson.getString("picUrl");
            pageUrl = mShareParaJson.getString("pageUrl");
            description = mShareParaJson.getString("description");
            defaultText = mShareParaJson.getString("defaultText");
        } catch (JSONException e) {
            Log.v(TAG, "JSONException:" + e.toString());
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(picUrl).openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;

        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = pageUrl;
        mediaObject.defaultText = defaultText;
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj() {
        MusicObject musicObject = new MusicObject();
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj() {
        VideoObject videoObject = new VideoObject();
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj() {
        VoiceObject voiceObject = new VoiceObject();
        return voiceObject;
    }

    // @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 微博的 private static final int REQUEST_CODE_SSO_AUTH = 32973;
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mWeiboSsoHandler != null) {
            mWeiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), this);
        }
    }


    /**
     * @see {@link Activity#onNewIntent}
     */
    public void onNewIntent(Intent intent) {
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        if (mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(intent, this);
        }
    }

    /*
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseRequest 微博请求数据对象
     * @see {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#handleWeiboRequest}
     */

    @Override
    public void onResponse(BaseResponse baseResp) {
        Log.v(TAG, "onResponse");
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Log.v(TAG, " onResponse ERR_OK");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Log.v(TAG, " onResponse ERR_CANCEL");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Log.v(TAG, " onResponse ERR_FAIL ");
                break;
        }
    }
}
