package com.seastar.wasai.views.plugin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.seastar.wasai.utils.ImageUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AppWechatPlugin {
    private final static String TAG = "AppWechatPlugin";
    private Activity activity;
    private PluginEvent pluginEvent;
    private IWXAPI wxApi = null;

    public AppWechatPlugin(Activity act, PluginEvent event) {
        this.activity = act;
        this.pluginEvent = event;
        wxApi = WXAPIFactory.createWXAPI(act.getApplicationContext(), MyApplication.WEIXIN_APP_ID, false);
        // 将该app注册到微信
        wxApi.registerApp(MyApplication.WEIXIN_APP_ID);
    }

    public boolean installed() {
        return wxApi != null && wxApi.isWXAppInstalled();
    }

    public boolean actionWithWeixin(String action,
                                    JSONObject para) throws JSONException {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(activity.getApplicationContext(), MyApplication.WEIXIN_APP_ID);
        }
        if (!(wxApi.isWXAppInstalled())) {
            pluginEvent.actionResult(pluginEvent.RET_APP_NOT_EXIST,null);
            return false;
        }
        try {
            if (action.equals("loginWithWechat")) {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "none";
                wxApi.sendReq(req);
                return true;
            } else if (action.equals("shareToWechatCircle")) {
                JSONObject temp = para;
                String title = temp.getString("title");
                String description = temp.getString("description");
                String picUrl = temp.getString("picUrl");
                String pageUrl = temp.getString("pageUrl");
                WXWebpageObject webPage = new WXWebpageObject();
                webPage.webpageUrl = pageUrl;
                WXMediaMessage msg = new WXMediaMessage(webPage);
                msg.title = title;
                msg.description = description;
                Bitmap thumb = BitmapFactory.decodeStream(new URL(picUrl).openStream());

                msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webPage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                wxApi.sendReq(req);
                return true;
            } else if (action.equals("shareToWechatFriend")) {
                JSONObject temp = para;
                String title = temp.getString("title");
                String description = temp.getString("description");
                String picUrl = temp.getString("picUrl");
                String pageUrl = temp.getString("pageUrl");
                WXWebpageObject webPage = new WXWebpageObject();
                webPage.webpageUrl = pageUrl;
                WXMediaMessage msg = new WXMediaMessage(webPage);
                msg.title = title;
                msg.description = description;
                Bitmap thumb = BitmapFactory.decodeStream(new URL(picUrl).openStream());
                msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webPage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                wxApi.sendReq(req);
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception called" + e.toString());
            return false;
        }
        return true;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
