package com.seastar.wasai.views.extendedcomponent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.session.SessionListener;
import com.alibaba.sdk.android.session.model.Session;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ObjectType;
import com.seastar.wasai.Entity.Template;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.R;
import com.seastar.wasai.service.UserService;
import com.seastar.wasai.service.WasaiService;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.utils.UncaughtExceptionHandler;
import com.seastar.wasai.utils.networkchange.NetworkMonitor;
import com.seastar.wasai.utils.networkchange.NetworkMonitorReceiver;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.sdk.modelbase.BaseResp;

import org.json.JSONObject;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 杨腾
 * @ClassName: MyApplication
 * @Description: 自定义application，为了方便获取上下文对象
 * @date 2015年4月16日 下午1:21:11
 */
public class MyApplication extends Application {
    private static String TAG = "MyApplication";
    private static Context context;
    private static String sessionId;
    public static int versionNum = 0;
    private static User currentUser;
    public static String userPreference;
    public static boolean userActivityLiving = false;
    private static MyApplication mInstance = null;

    // 分享
    public static final int WEIBO_SHARE_CLIENT = 1;
    public static final int WEIBO_SHARE_ALL_IN_ONE = 2;

    public static final String QQ_APPID = "1104522923";
    public static final String WEIXIN_APP_ID = "wx8f3dea3efec192f6";
    public static final String WEIXIN_APP_SECRET = "fe3e15b2a9d65227737465d34f8d72c5";
    public static String ALIMAMA_PID = "mm_30123955_8684652_33714208";
    public static String WEIXIN_AUTH_CODE;

    public static BaseResp weixin_resp;
    private static RequestQueue mRequestQueue;

    private static Map<Integer, ObjectType> templateMap = new HashMap<Integer, ObjectType>();

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UncaughtExceptionHandler.getInstance(getApplicationContext()).register();
        mInstance = this;
        versionNum = GeneralUtil.getAppVersionCode(getApplicationContext());
        NetworkMonitor.getInstance();
        NetworkMonitorReceiver.getInstance().registerReceiver();
        context = getApplicationContext();
        initImageLoader(context);
        startUserActionService();
        initTemplateMap();
        initJpush();
        setUserProfile();

        PropertyConfigurator.getConfigurator(context).configure();
        LeakCanary.install(this);

        AlibabaSDK.asyncInit(this, new InitResultCallback() {

            @Override
            public void onSuccess() {
//                Toast.makeText(context, "初始化成功", Toast.LENGTH_SHORT)
//                        .show();
            }

            @Override
            public void onFailure(int code, String message) {
//                Toast.makeText(context, "初始化异常", Toast.LENGTH_SHORT)
//                        .show();
            }

        });
    }

    private void setUserProfile() {
        if (MyApplication.isLogin()) {
            final User currentUser = MyApplication.getCurrentUser();
            if (!StringUtil.isNotEmpty(currentUser.getUuid()) || !StringUtil.isNotEmpty(currentUser.getUnid())) {
                String url = InterfaceConstant.USER_SESSION;
                Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
                    @Override
                    public void doResponse(String dataJsonStr) {
                        if (dataJsonStr != null) {
                            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                            User user = gson.fromJson(dataJsonStr, User.class);
                            currentUser.setUuid(user.getUuid());
                            currentUser.setUnid(user.getUnid());
                            MyApplication.setCurrentUser(currentUser);
                            setJpushAlias(true);
                            Log.d(TAG, "获取用户UUID成功：" + dataJsonStr);
                        }
                    }
                };
                MyGsonRequest request = new MyGsonRequest(url, null, null);
                MyApplication.addToRequestQueue(request.getRequest(sucessListener));
            }
        }
    }

    /**
     * 初始化JPush
     */
    private void initJpush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        PreferencesWrapper wrapper = new PreferencesWrapper(getContextObject());
        boolean isPushAble = wrapper.getBooleanValue("isPushAble", true);
        if (isPushAble) {
            resumeJpush();
        } else {
            stopJpush();
        }
    }

    // 返回
    public static Context getContextObject() {
        return context;
    }

    public static String getSessionId() {
        if (sessionId == null || "".equals(sessionId)) {
            setSessionId();
        }
        return sessionId;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: setSessionId
     * @Description: 设置sessionId
     */
    public static void setSessionId() {
        SharedPreferences sessionSp = context.getSharedPreferences("session", MODE_PRIVATE);
        String tempSessionId = sessionSp.getString("sessionId", null);
        if (tempSessionId == null || "".equals(tempSessionId.trim())) {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            String imei = TelephonyMgr.getDeviceId();
            String serial = "" + TelephonyMgr.getSimSerialNumber();
            String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            String bSerial = android.os.Build.SERIAL;

            sessionId = StringUtil.MD5(imei + serial + androidId + bSerial);
            Editor editor = sessionSp.edit();
            editor.putString("sessionId", sessionId);
            editor.commit();
        } else {
            sessionId = tempSessionId;
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: setSessionId
     * @Description: 设置sessionId
     */
    public static void setCurrentUser(User user) {
        SharedPreferences userSp = context.getSharedPreferences("user", MODE_PRIVATE);
        Editor editor = userSp.edit();
        editor.putString("identifyId", user.getIdentifyId());
        editor.putString("nickname", user.getNickname());
        editor.putString("picUrl", user.getPictureUrl());
        editor.putString("uuid", user.getUuid());
        editor.putString("unid", user.getUnid());
        editor.putString("mobile",user.getMobile());
        editor.putString("alipay",user.getAlipay());
        editor.commit();
        currentUser = user;
    }

    /**
     * @param @param btnName
     * @return void
     * @throws
     * @Title: setBtnClicked
     * @Description: 将按钮设置为已点击过
     */
    public static void setBtnClicked(String btnName) {
        SharedPreferences btnSp = context.getSharedPreferences("btnClicked", MODE_PRIVATE);
        Editor editor = btnSp.edit();
        editor.putInt(btnName, 1);
        editor.commit();
    }

    /**
     * @param @param  btnName
     * @param @return
     * @return boolean
     * @throws
     * @Title: isBtnClicked
     * @Description: 获取按钮是否被点击过
     */
    public static boolean isBtnClicked(String btnName) {
        SharedPreferences btnSp = context.getSharedPreferences("btnClicked", MODE_PRIVATE);
        if (btnSp != null) {
            return btnSp.getInt(btnName, 0) == 1;
        }
        return false;
    }

    /**
     * @param @param tags
     * @return void
     * @throws
     * @Title: setUserPreferences
     * @Description: 设置用户偏好
     */
    public static void setUserPreference(String ids) {
        SharedPreferences userSp = context.getSharedPreferences("userPreference", MODE_PRIVATE);
        Editor editor = userSp.edit();
        editor.putString("ageTags", ids);
        editor.commit();
        userPreference = ids;
    }

    public static void clearCurrentUser() {
        SharedPreferences userSp = context.getSharedPreferences("user", MODE_PRIVATE);
        userSp.edit().clear().commit();
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            currentUser = new User();
            SharedPreferences userSp = context.getSharedPreferences("user", MODE_PRIVATE);
            currentUser.setIdentifyId(userSp.getString("identifyId", null));
            currentUser.setNickname(userSp.getString("nickname", null));
            currentUser.setPictureUrl(userSp.getString("picUrl", null));
            currentUser.setUuid(userSp.getString("uuid", null));
            currentUser.setUnid(userSp.getString("unid", null));
            currentUser.setMobile(userSp.getString("mobile", null));
            currentUser.setAlipay(userSp.getString("alipay", null));
        }
        Log.v(TAG, "getCurrentUser");
        return currentUser;
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: isLogin
     * @Description: 是否登录
     */
    public static boolean isLogin() {
        SharedPreferences userSp = context.getSharedPreferences("user", MODE_PRIVATE);
        return (userSp.getString("identifyId", null) != null);
    }

    /**
     * @param @param context
     * @return void
     * @throws
     * @Title: initImageLoader
     * @Description: 初始化图片异步加载框架
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.memoryCache(new WeakMemoryCache());
        config.memoryCacheSize(10 * 1024 * 1024);// 10 MiB
        config.diskCacheSize(300 * 1024 * 1024); // 300 MiB
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());// 文件URL用MD5加密
        config.threadPoolSize(3);// 线程池内加载的数量
        config.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000));
        config.writeDebugLogs(); // Remove for release app

        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 启动上传用户行为日志service
     */
    private void startUserActionService() {
        Intent sIntent = new Intent(getApplicationContext(), WasaiService.class);
        startService(sIntent);
    }

    /**
     * 获取volley请求队列
     *
     * @return
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getContextObject());
        }
        return mRequestQueue;
    }

    /**
     * 向请求队列中添加一个请求
     *
     * @param req
     * @param tag
     * @param <T>
     */
    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    /**
     * 向请求队列中添加一个请求
     *
     * @param req
     * @param <T>
     */
    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public static void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * 取消请求
     */
    public static void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    private static void initTemplateMap() {
        templateMap.put(0, new ObjectType(Constant.TITLE_COMMON, Template.GUIDE.getId()));
        templateMap.put(1, new ObjectType(Constant.TITLE_GUIDE, Template.GUIDE.getId()));
        templateMap.put(2, new ObjectType(Constant.TITLE_EVA, Template.EVALUATION.getId()));
        templateMap.put(3, new ObjectType(Constant.TITLE_STRA, Template.GUIDE.getId()));
        templateMap.put(4, new ObjectType(Constant.TITLE_STRA, Template.GUIDE.getId()));
        templateMap.put(5, new ObjectType(Constant.TITLE_EVA, Template.EVALUATION.getId()));
        templateMap.put(6, new ObjectType(Constant.TITLE_GUIDE, Template.GUIDE.getId()));
        templateMap.put(7, new ObjectType(Constant.TITLE_PERIODICAL, Template.GUIDE.getId()));
        templateMap.put(8, new ObjectType(Constant.TITLE_PERIODICAL, Template.GUIDE.getId()));
    }

    public static ObjectType getObjectType(int type) {
        ObjectType objType = templateMap.get(type);
        if (objType == null) {
            objType = new ObjectType(Constant.TITLE_COMMON, Template.GUIDE.getId());
        }
        return objType;
    }

    public static Class getTemplate(int id) {
        Class clazz = Template.GUIDE.getClazz();
        for (Template temp : Template.values()) {
            if (temp.getId() == id) {
                clazz = temp.getClazz();
                break;
            }
        }
        return clazz;
    }

    public static void stopJpush() {
        if (!JPushInterface.isPushStopped(getContextObject())) {
            JPushInterface.stopPush(getContextObject());
        }
    }

    public static void resumeJpush() {
        if (JPushInterface.isPushStopped(getContextObject())) {
            JPushInterface.resumePush(getContextObject());
        }
    }

    public static void setJpushAlias(boolean flag) {
        TagAliasCallback callback = new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        };
        if (flag) {
            JPushInterface.setAlias(context, getCurrentUser().getUuid().replace("-", "_"), callback);
        } else {
            JPushInterface.setAlias(context, "", callback);
        }
    }
}