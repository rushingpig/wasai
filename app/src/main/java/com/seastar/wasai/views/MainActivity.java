package com.seastar.wasai.views;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.AppMessage;
import com.seastar.wasai.Entity.AppMessageChild;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.PromotionActivity;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.Entity.UserActionOfGuide;
import com.seastar.wasai.R;
import com.seastar.wasai.db.WasaiContentProviderUtils;
import com.seastar.wasai.service.UserActionLogService;
import com.seastar.wasai.utils.DeviceInfo;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.utils.networkchange.NetworkMonitor;
import com.seastar.wasai.views.common.RedPackActWebActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.UpgradeDialog;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杨腾
 * @ClassName: MainActivity
 * @Description: 主界面
 * @date 2015年4月9日 下午5:26:52
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener, LocationListener {
    private final static String TAG = "MainActivity";
    public static final int LOCATION_TAG = 12;
    private TabHost mTabHost;
    private Intent homeIntent;
    private Intent productIntent;
    private Intent guideIntent;
    private Intent userIntent;
    private RadioButton homeBtn;
    private RadioButton guideBtn;
    private RadioButton productBtn;
    private RadioButton userBtn;
    private PreferencesWrapper mPreferencesWrapper = null;
    private View redPackView;
    private ImageView goToRedPack;
    private TextView activityDateRange;

    private ImageView mGuidePageOfHome = null;

    public static int forwardParam;

    public static long PROMOTION_ACTIVITY_ID;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        forwardTarget(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mPreferencesWrapper = new PreferencesWrapper(this);
        mGuidePageOfHome = (ImageView) findViewById(R.id.guide_page);
        if (mPreferencesWrapper.getBooleanValue("show_guide_page1", true)) {
            mGuidePageOfHome.setVisibility(View.VISIBLE);
            mGuidePageOfHome.setVisibility(View.GONE);
            mPreferencesWrapper.setBooleanValueAndCommit("show_guide_page1", false);
        } else {
            mGuidePageOfHome.setVisibility(View.GONE);
        }

        mGuidePageOfHome.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGuidePageOfHome.setVisibility(View.GONE);
            }
        });

        goToRedPack = (ImageView) findViewById(R.id.goToRedPack);
        goToRedPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RedPackActWebActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("actId", PROMOTION_ACTIVITY_ID);
                bundle.putString("pageTitle", "活动详情");
                intent.putExtras(bundle);
                startActivity(intent);
                redPackView.setVisibility(View.GONE);
            }
        });
        redPackView = findViewById(R.id.redPack);
        redPackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackView.setVisibility(View.GONE);
            }
        });
        activityDateRange = (TextView) findViewById(R.id.activity_date_range);

        this.homeIntent = new Intent(this, HomeActivity.class);
        this.productIntent = new Intent(this, ProductMainActivity.class);
        this.guideIntent = new Intent(this, GuideMainActivity.class);
        this.userIntent = new Intent(this, UserActivity.class);

        homeBtn = ((RadioButton) findViewById(R.id.radio_button_home));
        guideBtn = ((RadioButton) findViewById(R.id.radio_button_guide));
        productBtn = ((RadioButton) findViewById(R.id.radio_button_product));
        userBtn = ((RadioButton) findViewById(R.id.radio_button_user));
        homeBtn.setOnCheckedChangeListener(this);
        guideBtn.setOnCheckedChangeListener(this);
        productBtn.setOnCheckedChangeListener(this);
        userBtn.setOnCheckedChangeListener(this);

        setupIntent();
        basicMsg();

        // 后台保存session
        createSession();
        // /第一次启动app 需要创建桌面快捷方式
        if (mPreferencesWrapper.getBooleanValue("isFirstLogin", true)) {
            addShortcutToDesktop();
            mPreferencesWrapper.setBooleanValueAndCommit("isFirstLogin", false);
        }
        saveLog();
        forwardTarget(this.getIntent());
        loadCurrentActivity();
    }

    private void forwardTarget(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && (bundle.getInt("type") == TypeConstant.USER || bundle.getInt("type") == TypeConstant.WALLET_NOTIFICATION)) {
            homeBtn.setChecked(false);
            guideBtn.setChecked(false);
            productBtn.setChecked(false);
            userBtn.setChecked(true);
        } else if (bundle != null && bundle.getInt("type") == TypeConstant.SUPER_REBATE) {
            forwardParam = bundle.getInt("categoryId");
            homeBtn.setChecked(false);
            guideBtn.setChecked(false);
            productBtn.setChecked(true);
            userBtn.setChecked(false);
        }else if(bundle != null && bundle.getInt("type") == TypeConstant.FORWARD_PRODUCT){
            homeBtn.setChecked(false);
            guideBtn.setChecked(false);
            productBtn.setChecked(true);
            userBtn.setChecked(false);
        }
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
                    Upgrade upgrade = gson.fromJson(dataJsonStr, Upgrade.class);
                    Log.d(TAG, "创建session成功：" + dataJsonStr);
                    if (upgrade.getNeedUpdate()) {
                        showUpgradeDialog(upgrade);
                    }
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

    /**
     * @param
     * @return void
     * @throws
     * @Title: saveLog
     * @Description: 记录启动日志
     */
    private void saveLog() {
        UserActionOfGuide myAction = new UserActionOfGuide();
        myAction.session_id = MyApplication.getSessionId();
        myAction.target = "app";
        myAction.target_id = "";
        myAction.operation = "start";
        myAction.timestamp = new Date().getTime();
        WasaiContentProviderUtils.getInstance(this).addUserAtionMessage(myAction);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_button_home:
                    this.mTabHost.setCurrentTabByTag("HOME_TAB");
                    break;
                case R.id.radio_button_guide:
                    this.mTabHost.setCurrentTabByTag("GUIDE_TAB");
                    break;
                case R.id.radio_button_product:
                    this.mTabHost.setCurrentTabByTag("PRODUCT_TAB");
                    break;
                case R.id.radio_button_user:
                    this.mTabHost.setCurrentTabByTag("USER_TAB");
                    break;
            }
        }
    }

    /**
     * @return void
     * @Title: setupIntent
     * @Description: 初始化Intent
     */
    private void setupIntent() {
        this.mTabHost = getTabHost();
        TabHost localTabHost = this.mTabHost;
        localTabHost.addTab(buildTabSpec("HOME_TAB", R.string.txthome, R.drawable.tab_home_btn, this.homeIntent));
        localTabHost.addTab(buildTabSpec("GUIDE_TAB", R.string.guide, R.drawable.tab_guide_btn, this.guideIntent));
        localTabHost.addTab(buildTabSpec("PRODUCT_TAB", R.string.product, R.drawable.tab_product_btn,
                this.productIntent));
        localTabHost.addTab(buildTabSpec("USER_TAB", R.string.txtme, R.drawable.tab_selfinfo_btn, this.userIntent));

    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
        return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel), getResources().getDrawable(resIcon))
                .setContent(content);
    }

    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                GeneralUtil.showToastShort(getApplicationContext(), getResources().getString(R.string.EXIT_APP));
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 创建快捷方式
     */
    private void addShortcutToDesktop() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, this.getString(R.string.app_name));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(this, this.getClass()).setAction(Intent.ACTION_MAIN));
        sendBroadcast(shortcut);
    }


    private LocationManager locationManager;

    private void basicMsg() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            Log.v(TAG, "无法定位");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        final String message = GetBasicMessage(mLatitude, mLongitude);
        Thread mThread = new Thread(new Runnable() {

            @Override
            public void run() {
                UserActionLogService logHttp = new UserActionLogService();
                Message msg = new Message();
                msg.what = LOCATION_TAG;
                if (logHttp.log("info", "{" + "\"basic\"" + ":" + message + "}")) {
                    locationManager.removeUpdates(MainActivity.this);
                }
            }
        });
        mThread.start();
    }

    private String GetBasicMessage(double mLatitude, double mLongitude) {
        ApplicationInfo info = null;
        try {
            info = MainActivity.this.getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String visitor_source = info.metaData.getString("channel");
        AppMessage mAppMessage = new AppMessage();
        mAppMessage.session_id = MyApplication.getSessionId();
        mAppMessage.visitor_source = visitor_source;
        String mNetwork_env = "";
        switch (NetworkMonitor.getInstance().getNetworkStateFlag()) {
            case 0:
                mNetwork_env = "未知";
                break;
            case 1:
                mNetwork_env = "2G";
                break;
            case 2:
                mNetwork_env = "3G";
                break;
            case 3:
                mNetwork_env = "WIFI";
                break;
            case 4:
                mNetwork_env = "4G";
                break;
            default:
                break;
        }
        mAppMessage.network_env = mNetwork_env;
        mAppMessage.latitude = mLatitude;
        mAppMessage.longitude = mLongitude;
        AppMessageChild child = new AppMessageChild();
        child.phone_type = DeviceInfo.getModel();
        child.company = "Android";
        child.system_v = DeviceInfo.getSDKVersion();
        child.width = DeviceInfo.getWidth();
        child.height = DeviceInfo.getHeight();
        mAppMessage.terminal_info = child;
        mAppMessage.wasai_version = GeneralUtil.getAppVersionCode(MainActivity.this);
        mAppMessage.start_timestamp = new Date().getTime();
        Gson gson = new Gson();
        final String message = gson.toJson(mAppMessage);
        return message;
    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String arg0) {

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

    }

    private void showUpgradeDialog(Upgrade upgrade) {
        UpgradeDialog upgradeDialog = new UpgradeDialog(this, upgrade);
        upgradeDialog.show();
    }

    /**
     * 查找当前的活动
     */
    private void loadCurrentActivity() {
        String url = InterfaceConstant.INTERFACE_HOST + "/activity/redenvelope/info";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    PromotionActivity promotionActivity = gson.fromJson(dataJsonStr, PromotionActivity.class);
                    Log.d(TAG, "获取当前活动成功：" + dataJsonStr);
                    if (promotionActivity.status) {
                        activityDateRange.setText(promotionActivity.startTime + "-" + promotionActivity.endTime);
                        redPackView.setVisibility(View.VISIBLE);
                        PROMOTION_ACTIVITY_ID = promotionActivity.id;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                redPackView.setVisibility(View.GONE);
//                            }
//                        }, 3000);
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}