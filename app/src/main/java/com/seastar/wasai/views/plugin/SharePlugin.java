package com.seastar.wasai.views.plugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.seastar.wasai.Entity.Award;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.Store;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.R;
import com.seastar.wasai.service.AppService;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.ShareButtonView;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by yangteng on 2015/6/6.
 */
public class SharePlugin implements PluginEvent {
    private static boolean bExited = false;
    private IWXAPI wxApi;
    private AppWeiboPlugin weibo;
    private AppQQPlugin qq;
    private AppWechatPlugin weixin;
    private static WaitingDlg waiting;
    private static PopupWindow sharePopWindow;
    private ShareButtonView shareWxTView;
    private ShareButtonView shareWxFView;
    private ShareButtonView shareSinaView;
    private ShareButtonView shareQZoneView;
    private ShareButtonView shareQQView;
    private ShareButtonView openInBrowserView;
    private View shareCancelView;
    private static ImageView shadowView;
    private Handler mMainHandler = null;

    private Activity parentView;
    private View positionView;
    private Object shareObject;

    public SharePlugin(Activity parentView, View positionView) {
        this.parentView = parentView;
        this.positionView = positionView;
    }

    public void setShareObject(Object shareObject) {
        this.shareObject = shareObject;
    }

    public void init() {
        initPopWindow();
        initShareHandler();
        initSharePlugin();
        bindShareEvents();
    }

    private void initPopWindow() {
        View popView = LayoutInflater.from(parentView).inflate(R.layout.share_popup_window, null);
        sharePopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sharePopWindow.setBackgroundDrawable(new ColorDrawable(0));
        sharePopWindow.setAnimationStyle(R.style.share_window_anim);
        shareWxTView = (ShareButtonView) popView.findViewById(R.id.shareToWxT);
        shareWxFView = (ShareButtonView) popView.findViewById(R.id.shareToWxF);
        shareSinaView = (ShareButtonView) popView.findViewById(R.id.shareToSina);
        shareQZoneView = (ShareButtonView) popView.findViewById(R.id.shareToQZone);
        shareQQView = (ShareButtonView) popView.findViewById(R.id.shareToQQ);
        openInBrowserView = (ShareButtonView) popView.findViewById(R.id.openInBrowser);
        shareCancelView = popView.findViewById(R.id.shareCancelView);
        shadowView = (ImageView) parentView.findViewById(R.id.shadow_view);
        shareCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePop();
            }
        });
        sharePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(shadowView != null){
                    shadowView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public static void closePop() {
        if(shadowView != null){
            shadowView.setVisibility(View.INVISIBLE);
        }
        sharePopWindow.dismiss();
    }

    public void showPop() {
        sharePopWindow.showAtLocation(positionView, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
        sharePopWindow.setFocusable(true);
        sharePopWindow.setOutsideTouchable(true);
        sharePopWindow.update();
        if(shadowView != null){
            shadowView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initShareHandler
     * @Description: 初始化分享的handler
     */
    private void initShareHandler() {
        mMainHandler = new HandleImp(parentView);
//        mMainHandler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                int para1 = msg.arg1;
//                int para2 = msg.arg2;
//                super.handleMessage(msg);
//                if (bExited) {
//                    return;
//                }
//                if (para1 == 4) {
//                    waiting.showWaitingDlg(false);
//                    if (para2 == 0) {
//                        Toast.makeText(parentView, ToastMessage.SUCCESS_TO_SHARE, Toast.LENGTH_SHORT).show();
//                        closePop();
//                    } else if (para2 == 1) {
//                        Toast.makeText(parentView, ToastMessage.CANCLE_TO_SHARE, Toast.LENGTH_SHORT).show();
//                        closePop();
//                    } else {
//                        Toast.makeText(parentView, ToastMessage.FAILED_TO_SHARE, Toast.LENGTH_SHORT).show();
//                    }
//                } else if (para1 == 5) {
//                    waiting.showWaitingDlg(false);
//                    closePop();
//                } else if (para1 == 6) {
//                    waiting.showWaitingDlg(false);
//                    closePop();
//                }
//            }
//        };
    }
    static class HandleImp extends Handler{
        WeakReference<Activity> weakReference;
        public HandleImp(Activity activity){
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = weakReference.get();
            if(activity != null){
                int para1 = msg.arg1;
                int para2 = msg.arg2;
                if (bExited) {
                    return;
                }
                if (para1 == 4) {
                    waiting.showWaitingDlg(false);
                    if (para2 == 0) {
                        Toast.makeText(activity, ToastMessage.SUCCESS_TO_SHARE, Toast.LENGTH_SHORT).show();
                        closePop();
                    } else if (para2 == 1) {
                        Toast.makeText(activity, ToastMessage.CANCLE_TO_SHARE, Toast.LENGTH_SHORT).show();
                        closePop();
                    } else {
                        Toast.makeText(activity, ToastMessage.FAILED_TO_SHARE, Toast.LENGTH_SHORT).show();
                    }
                } else if (para1 == 5) {
                    waiting.showWaitingDlg(false);
                    closePop();
                } else if (para1 == 6) {
                    waiting.showWaitingDlg(false);
                    closePop();
                }
            }

        }
    }

    private void initSharePlugin() {
        weibo = new AppWeiboPlugin(parentView, this);
        qq = new AppQQPlugin(parentView, this);
        weixin = new AppWechatPlugin(parentView, this);
        wxApi = WXAPIFactory.createWXAPI(parentView, MyApplication.WEIXIN_APP_ID, false);
        // 将该app注册到微信
        wxApi.registerApp(MyApplication.WEIXIN_APP_ID);
        waiting = new WaitingDlg(parentView);
    }

    @Override
    public void actionResult(int status,Object data) {
        Message toMain = mMainHandler.obtainMessage();
        toMain.arg1 = 4;
        toMain.arg2 = status;
        mMainHandler.sendMessage(toMain);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: bindShareEvents
     * @Description: 绑定分享的事件
     */
    private void bindShareEvents() {
        shareWxTView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                if (!weixin.installed()) {
                    Toast.makeText(parentView, ToastMessage.WECHAT_NOT_INSTALL, Toast.LENGTH_SHORT).show();
                } else {
                    shareTo(Constant.SHARE_WECHAT_CIRCLE);
                }

            }
        });

        shareWxFView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                if (!weixin.installed()) {
                    Toast.makeText(parentView, ToastMessage.WECHAT_NOT_INSTALL, Toast.LENGTH_SHORT).show();
                } else {
                    shareTo(Constant.SHARE_WECHAT_FRIEND);
                }

            }
        });

        shareSinaView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                if (!weibo.installed()) {
                    Toast.makeText(parentView, ToastMessage.WEIBO_NOT_INSTALL, Toast.LENGTH_SHORT).show();
                } else {
                    shareTo(Constant.SHARE_WEIBO);
                }

            }
        });

        shareQZoneView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                if (!qq.installed()) {
                    Toast.makeText(parentView, ToastMessage.QQ_NOT_INSTALL, Toast.LENGTH_SHORT).show();
                } else {
                    shareTo(Constant.SHARE_QQ_ZONE);
                }

            }
        });

        shareQQView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                if (!qq.installed()) {
                    Toast.makeText(parentView, ToastMessage.QQ_NOT_INSTALL, Toast.LENGTH_SHORT).show();
                } else {
                    shareTo(Constant.SHARE_QQ_FRIEND);
                }

            }
        });

        openInBrowserView.setOnClick(new ShareButtonView.ICallBack() {
            @Override
            public void onClick() {
                shareTo(Constant.OPEN_IN_BROWSER);
            }
        });
    }

    /**
     * @param @param where 平台
     * @return void
     * @throws
     * @Title: shareTo
     * @Description: 分享
     */
    private void shareTo(final String where) {
//        waiting.showWaitingDlg(true);
        new Thread(new Runnable() {
            public void run() {
                String picUrl;
                String title;
                String description;
                String pageUrl;

                if (shareObject != null) {
                    if (shareObject instanceof Guide) {
                        Guide guide = (Guide) shareObject;
                        picUrl = guide.getPic(Image.SIZE_THUMB);
                        title = guide.getTitle();
                        description = guide.getDescription();
                        pageUrl = InterfaceConstant.GUIDE_SHARE + "/" + guide.getGuideId();
                    } else if (shareObject instanceof Item) {
                        Item item = (Item) shareObject;
                        picUrl = item.getPic(Image.SIZE_THUMB);
                        title = item.getItemName();
                        description = item.getItemDesc();
                        pageUrl = InterfaceConstant.ITEM_SHARE + "/" + item.getItemId();
                    } else if (shareObject instanceof Award) {
                        Award award = (Award) shareObject;
                        picUrl = award.getPic(Image.SIZE_THUMB);
                        title = award.getName();
                        description = award.getDesc();
                        pageUrl = InterfaceConstant.AWARD_SHARE + "/" + award.getId();
                    } else if (shareObject instanceof com.seastar.wasai.Entity.Activity) {
                        com.seastar.wasai.Entity.Activity activity = (com.seastar.wasai.Entity.Activity) shareObject;
                        picUrl = activity.getPic(Image.SIZE_THUMB);
                        title = activity.getTitle();
                        description = activity.getDescription();
                        pageUrl = InterfaceConstant.ACTIVITY_SHARE + "/" + activity.getTemplateName() + "/" + activity.getActivityId() + (MyApplication.isLogin()?("?uuid=" + MyApplication.getCurrentUser().getUuid()):"");
                        if(StringUtil.isNotEmpty(activity.getPageUrl())){
                            pageUrl = activity.getPageUrl();
                        }
                    }else if(shareObject instanceof Store){
                        Store store = (Store) shareObject;
                        picUrl = store.getPic(Image.SIZE_THUMB);
                        title = store.getTitle();
                        description = store.getDescription();
                        pageUrl = InterfaceConstant.STORE_SHARE + "/" + store.getId();
                    }else{
                        AppService appService = new AppService();
                        Upgrade upgrade = appService.getVersion("android");
                        picUrl = upgrade.getLogo();
                        title = "哇塞宝贝";
                        description = "哇塞宝贝辣妈神器";
                        pageUrl = InterfaceConstant.APP_SHARE;
                    }
                } else {
                    AppService appService = new AppService();
                    Upgrade upgrade = appService.getVersion("android");
                    picUrl = upgrade.getLogo();
                    title = "哇塞宝贝";
                    description = "哇塞宝贝辣妈神器";
                    pageUrl = InterfaceConstant.APP_SHARE;
                }

                JSONObject para = new JSONObject();
                try {
                    para.put("title", title);
                    para.put("description", description);
                    para.put("picUrl", picUrl);
                    para.put("pageUrl", pageUrl);
                    para.put("defaultText", "");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                try {
                    if (where.equals(Constant.SHARE_WECHAT_CIRCLE) || where.equals(Constant.SHARE_WECHAT_FRIEND)) {
                        weixin.actionWithWeixin(where, para);
                        Message toMain = mMainHandler.obtainMessage();
                        toMain.arg1 = 5;
                        toMain.arg2 = 0;
                        mMainHandler.sendMessage(toMain);
                    }
                    if (where.equals(Constant.SHARE_WEIBO)) {
                        weibo.actionWithWeibo(where, para);
                        Message toMain = mMainHandler.obtainMessage();
                        toMain.arg1 = 6;
                        toMain.arg2 = 0;
                        mMainHandler.sendMessage(toMain);
                    }
                    if (where.equals(Constant.SHARE_QQ_FRIEND) || where.equals(Constant.SHARE_QQ_ZONE)) {
                        qq.actionWithQQ(where, para);
                    }
                    if(where.equals(Constant.OPEN_IN_BROWSER)){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(para.getString("pageUrl")));
                        parentView.startActivity(intent);
                        Message toMain = mMainHandler.obtainMessage();
                        toMain.arg1 = 6;
                        mMainHandler.sendMessage(toMain);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void dealResult(int requestCode, int resultCode, Intent data) {
//        if (shareMode.equals("weibo")) {
//            weibo.onActivityResult(requestCode, resultCode, data);
//        } else if (shareMode.equals("qq")) {
//            qq.onActivityResult(requestCode, resultCode, data);
//        } else if (shareMode.equals("weixin")) {
//            weixin.onActivityResult(requestCode, resultCode, data);
//        }
    }
}
