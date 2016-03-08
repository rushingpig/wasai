package com.seastar.wasai.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.login.callback.LogoutCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.Entity.User;
import com.seastar.wasai.Entity.WalletMainEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SettingActionView;
import com.seastar.wasai.views.login.LoginActivity;
import com.seastar.wasai.views.order.OrdersActivity;
import com.seastar.wasai.views.plugin.SharePlugin;
import com.seastar.wasai.views.setting.MoreActivity;
import com.seastar.wasai.views.user.FavoriteActivity;
import com.seastar.wasai.views.user.UserInfoActivity;
import com.seastar.wasai.views.wallet.WalletMainActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends Activity implements View.OnClickListener {
    private RoundedImageView avatar;
    private TextView nickNameView;
    private View userAvatarFrame;
    private View userableMoneyView;
    //    private SettingActionView myShopingCarView;
    private SettingActionView myOrdersView;
    private SettingActionView myFavoritesView;
    private SettingActionView myPointsView;
    private SettingActionView shareView;
    private SettingActionView moreView;
    private SharePlugin sharePlugin;

    private TextView moneyView;
    private TextView wCoinView;

    private DisplayImageOptions imageDisplayOptions;
    private final static int CODE_LOGIN = 0;
    private long lastClickTime;
    private String[] dialogItems = new String[]{"查看资料", "退出登录", "切换淘宝账号"};

    private BroadcastReceiver orderReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.userActivityLiving = true;
        setContentView(R.layout.activity_user);
        imageDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        nickNameView = (TextView) findViewById(R.id.user_nick_name);
        avatar = (RoundedImageView) findViewById(R.id.avatar);
        userAvatarFrame = findViewById(R.id.user_avatar_frame);
        userAvatarFrame.setOnClickListener(this);
        userableMoneyView = findViewById(R.id.userable_money);
        userableMoneyView.setOnClickListener(this);
//        myShopingCarView = (SettingActionView) findViewById(R.id.my_shopping_car);
//        myShopingCarView.setOnClickListener(this);
        myOrdersView = (SettingActionView) findViewById(R.id.my_orders);
        myOrdersView.setOnClickListener(this);
        myFavoritesView = (SettingActionView) findViewById(R.id.my_favorites);
        myFavoritesView.setOnClickListener(this);
        myPointsView = (SettingActionView) findViewById(R.id.my_points);
        myPointsView.setOnClickListener(this);
        shareView = (SettingActionView) findViewById(R.id.setting_share_app);
        shareView.setOnClickListener(this);
        moreView = (SettingActionView) findViewById(R.id.setting_more);
        moreView.setOnClickListener(this);
        initWalletView();

        sharePlugin = new SharePlugin(this, this.findViewById(R.id.setting_scroll));
        sharePlugin.init();

    }

    private void initWalletView() {
        moneyView = (TextView) findViewById(R.id.money);
        wCoinView = (TextView) findViewById(R.id.wcoin);
    }

    @Override
    public void onClick(View v) {
        long currentTime = new Date().getTime();
        if (currentTime - lastClickTime > 1000) {
            switch (v.getId()) {
                case R.id.user_avatar_frame:
                    if (!MyApplication.isLogin()) {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, LoginActivity.class);
                        startActivityForResult(intent, CODE_LOGIN);
                    } else {
                        showDialog();
                    }
                    break;
                case R.id.userable_money:
                    if (MyApplication.isLogin()) {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, WalletMainActivity.class);
                        startActivityForResult(intent, CODE_LOGIN);
                    }
                    break;
//                case R.id.my_shopping_car:
//                    CartService cartService = AlibabaSDK.getService(CartService.class);
//                    cartService.showCart(this, new TradeProcessCallback() {
//
//                        @Override
//                        public void onPaySuccess(TradeResult tradeResult) {
//                            Toast.makeText(UserActivity.this, "支付成功", Toast.LENGTH_SHORT)
//                                    .show();
//                            String orders = "";
//                            for(int i = 0; i < tradeResult.paySuccessOrders.size(); i++){
//                                if(i < tradeResult.paySuccessOrders.size() - 1){
//                                    orders += tradeResult.paySuccessOrders.get(i) + ",";
//                                }else{
//                                    orders += tradeResult.paySuccessOrders.get(i);
//                                }
//                            }
//                            postOrders(orders);
//                        }
//
//                        @Override
//                        public void onFailure(int code, String msg) {
//                            if (code == ResultCode.QUERY_ORDER_RESULT_EXCEPTION.code) {
//                                Toast.makeText(UserActivity.this, "确认交易订单失败",
//                                        Toast.LENGTH_SHORT).show();
//                            } else {
////                                Toast.makeText(UserActivity.this, "交易异常",
////                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                    break;
                case R.id.my_orders:
                    if (MyApplication.isLogin()) {
                        myOrdersView.setRedPointViewVisible(false);
                        PreferencesWrapper wrapper = new PreferencesWrapper(this);
                        wrapper.setBooleanValueAndCommit("hasNewOrderNotification", false);
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, OrdersActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, LoginActivity.class);
                        startActivityForResult(intent, CODE_LOGIN);
                    }
                    break;
                case R.id.my_favorites:
                    if (MyApplication.isLogin()) {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, FavoriteActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, LoginActivity.class);
                        startActivityForResult(intent, CODE_LOGIN);
                    }
                    break;
                case R.id.my_points:
                    if (MyApplication.isLogin()) {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, WalletMainActivity.class);
                        startActivity(intent);
                        myPointsView.setRedPointViewVisible(false);
                        PreferencesWrapper wrapper = new PreferencesWrapper(this);
                        wrapper.setBooleanValueAndCommit("hasNewMoneyNotification", false);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(UserActivity.this, LoginActivity.class);
                        startActivityForResult(intent, CODE_LOGIN);
                    }
                    break;
                case R.id.setting_share_app:
                    sharePlugin.showPop();
                    break;
                case R.id.setting_more:
                    Intent intent = new Intent();
                    intent.setClass(UserActivity.this, MoreActivity.class);
                    startActivity(intent);
                    break;

            }
            lastClickTime = currentTime;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CODE_LOGIN) {
            sharePlugin.dealResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: setUserInfo
     * @Description: 设置用户信息
     */
    private void setUserInfo() {
        if (MyApplication.isLogin()) {
            User user = MyApplication.getCurrentUser();
            nickNameView.setText(user.getNickname());
            ImageLoader.getInstance().displayImage(user.getPictureUrl(), avatar, imageDisplayOptions);
            userableMoneyView.setVisibility(View.VISIBLE);
            getWalletData();
            setWalletRedPoint();
            setOrderRedPoint();
        } else {
            nickNameView.setText("未登录");
            avatar.setImageResource(R.drawable.avatar);
            userableMoneyView.setVisibility(View.GONE);
            myPointsView.setRedPointViewVisible(false);
            myOrdersView.setRedPointViewVisible(false);
        }
    }

    private void setWalletRedPoint() {
        PreferencesWrapper wrapper = new PreferencesWrapper(this);
        if (wrapper.getBooleanValue("hasNewMoneyNotification", false)) {
            myPointsView.setRedPointViewVisible(true);
        } else {
            myPointsView.setRedPointViewVisible(false);
        }
    }

    private void setOrderRedPoint() {
        PreferencesWrapper wrapper = new PreferencesWrapper(this);
        if (wrapper.getBooleanValue("hasNewOrderNotification", false)) {
            myOrdersView.setRedPointViewVisible(true);
        } else {
            myOrdersView.setRedPointViewVisible(false);
        }
    }

    private void setWallet(WalletMainEntity wallet) {
        moneyView.setText(wallet.availAmount);
        wCoinView.setText(wallet.wcoin);
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(this).setTitle(null).setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                switch (which) {
                    case 0:
                        if (!MyApplication.isLogin()) {
                            intent.setClass(UserActivity.this, LoginActivity.class);
                        } else {
                            intent.setClass(UserActivity.this, UserInfoActivity.class);
                        }
                        startActivityForResult(intent, CODE_LOGIN);
                        break;
                    case 1:
                        logout();
                        break;
                    case 2:
                        changeTaobaoAccount();
                        break;
                }
            }
        }).show();
    }

    /**
     * 切换淘宝账号
     */
    private void changeTaobaoAccount() {
        final LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.logout(UserActivity.this, new LogoutCallback() {
            @Override
            public void onSuccess() {
                loginTaobao(loginService);
            }

            @Override
            public void onFailure(int i, String s) {
                loginTaobao(loginService);
            }
        });
    }

    private void loginTaobao(LoginService loginService) {
        loginService.showLogin(UserActivity.this, new LoginCallback() {
            @Override
            public void onSuccess(Session session) {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void logout() {
        nickNameView.setText("未登录");
        avatar.setImageResource(R.drawable.avatar);
        userableMoneyView.setVisibility(View.GONE);
        myPointsView.setRedPointViewVisible(false);
        myOrdersView.setRedPointViewVisible(false);

        String url = InterfaceConstant.LOGINOUT;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Boolean flag = gson.fromJson(dataJsonStr, Boolean.class);
                    if (flag) {
                        MyApplication.clearCurrentUser();
                        MyApplication.setJpushAlias(false);
                    } else {
                        setUserInfo();
                    }
                }
                Log.d(TAG, "退出接口调用成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    private void getWalletData() {
        String url = InterfaceConstant.FANLI_WALLET_AMOUNT + "uuid=" + MyApplication.getCurrentUser().getUuid();
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new Gson();
                    WalletMainEntity wallet = gson.fromJson(dataJsonStr, WalletMainEntity.class);
                    if (wallet != null) {
                        setWallet(wallet);
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.userActivityLiving = false;
    }

    private class OrderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra("type",0) == TypeConstant.USER){
                myOrdersView.setRedPointViewVisible(true);
            }else if(intent.getIntExtra("type",0) == TypeConstant.WALLET_NOTIFICATION){
                myPointsView.setRedPointViewVisible(true);
            }
        }
    }

    @Override
    protected void onStart() {
        orderReceiver = new OrderReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.seastar.wasai.views.UserActivity");
        registerReceiver(orderReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(orderReceiver);
        super.onStop();
    }
}
