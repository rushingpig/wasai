package com.seastar.wasai.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.ResultCode;
import com.alibaba.sdk.android.trade.ItemService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ObjectType;
import com.seastar.wasai.Entity.PromotionActivity;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.R;
import com.seastar.wasai.views.MainActivity;
import com.seastar.wasai.views.common.ActWebActivity;
import com.seastar.wasai.views.common.ActWithoutShareWebActivity;
import com.seastar.wasai.views.common.GuideListActivity;
import com.seastar.wasai.views.common.LotteryWebActivity;
import com.seastar.wasai.views.common.ProductListActivity;
import com.seastar.wasai.views.common.ProductWebActivity;
import com.seastar.wasai.views.common.RedPackActWebActivity;
import com.seastar.wasai.views.common.SignInWebActivity;
import com.seastar.wasai.views.common.StoreWebActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.RedPackageDialog;
import com.seastar.wasai.views.promotion.StoreListActivity;
import com.taobao.tae.sdk.webview.TaeWebViewUiSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杨腾
 * @ClassName: CommonUtil
 * @Description: 公共工具类
 * @date 2015年4月28日 下午1:59:11
 */
public class CommonUtil {

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: CheckNetWork
     * @Description: 检查网络状况
     */
    public static boolean checkNetWork() {
        if (isNetworkConnected(MyApplication.getContextObject())) {
            if (isWifiConnected(MyApplication.getContextObject()) || isMobileConnected(MyApplication.getContextObject())) {
//                if(ping()){
//                    return true;
//                }
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param @param  context
     * @param @param  packageName
     * @param @return
     * @return boolean
     * @throws
     * @Title: isInstalled
     * @Description: 获取指定应用是否已安装
     */
    public static boolean isInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * @param @param context
     * @param @param shopUrl
     * @param @param packageName
     * @param @param className
     * @return void
     * @throws
     * @Title: forwardApp
     * @Description: 跳转到第三方应用商品详情页
     */
    public static void forwardApp(Context context, String shopUrl, String packageName, String className) {
        ComponentName componetName = new ComponentName(packageName, className);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(componetName);
        intent.setData(Uri.parse(shopUrl));
        context.startActivity(intent);
    }

    /**
     * @param @param context
     * @param @param shopUrl
     * @return void
     * @throws
     * @Title: forwardWeb
     * @Description: 跳转到商品WEBVIEW
     */
    public static void forwardWeb(Context context, long itemId) {
        Intent intent = new Intent(context, ProductWebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("itemId", itemId);
        bundle.putString("pageTitle", Constant.TITLE_PRD);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * @param @param context
     * @param @param product
     * @return void
     * @throws
     * @Title: forwardToDetailPage
     * @Description: 跳转到商品详情页
     */
    public static void forwardToDetailPage(Context context, Item product) {
//        if (Constant.SHOP_TYPE_TB.equals(product.getPlatform())) {
//            if (CommonUtil.isInstalled(context, Constant.TB_PACKAGE)) {
//                CommonUtil.forwardApp(context, product.getShopUrl().getMobileUrl(), Constant.TB_PACKAGE,
//                        Constant.TB_CLASS);
//            } else {
//                CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//            }
//        } else if (Constant.SHOP_TYPE_TM.equals(product.getPlatform())) {
//            if (CommonUtil.isInstalled(context, Constant.TM_PACKAGE)) {
//                CommonUtil.forwardApp(context, product.getShopUrl().getMobileUrl(), Constant.TM_PACKAGE,
//                        Constant.TM_CLASS);
//            } else {
//                CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//            }
//        } else if (Constant.SHOP_TYPE_JD.equals(product.getPlatform())
//				&& StringUtil.isNotEmpty(product.getShopUrl().getNativeUrl())) {
//			if (CommonUtil.isInstalled(context, Constant.JD_PACKAGE)) {
//				CommonUtil.forwardApp(context, product.getShopUrl().getNativeUrl(), Constant.JD_PACKAGE,
//						Constant.JD_CLASS);
//			} else {
//				CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//			}
//		} else if (Constant.SHOP_TYPE_YHD.equals(product.getPlatform())
//				&& StringUtil.isNotEmpty(product.getShopUrl().getNativeUrl())) {
//			if (CommonUtil.isInstalled(context, Constant.YHD_PACKAGE)) {
//				CommonUtil.forwardApp(context, product.getShopUrl().getNativeUrl(), Constant.YHD_PACKAGE,
//						Constant.YHD_CLASS);
//			} else {
//				CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//			}
//		} else if (Constant.SHOP_TYPE_SN.equals(product.getPlatform())
//				&& StringUtil.isNotEmpty(product.getShopUrl().getNativeUrl())) {
//			if (CommonUtil.isInstalled(context, Constant.SN_PACKAGE)) {
//				CommonUtil.forwardApp(context, product.getShopUrl().getNativeUrl(), Constant.SN_PACKAGE,
//						Constant.SN_CLASS);
//			} else {
//				CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//			}
//		} else if (Constant.SHOP_TYPE_MYBB.equals(product.getPlatform())
//				&& StringUtil.isNotEmpty(product.getShopUrl().getNativeUrl())) {
//			if (CommonUtil.isInstalled(context, Constant.MYBB_PACKAGE)) {
//				CommonUtil.forwardApp(context, product.getShopUrl().getNativeUrl(), Constant.MYBB_PACKAGE,
//						Constant.MYBB_CLASS);
//			} else {
//				CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//			}
//		} else {
//			CommonUtil.forwardWeb(context, product.getShopUrl().getMobileUrl(), product.getPlatform());
//		}
        CommonUtil.forwardWeb(context, product.getItemId());
    }

    public static void forwardToDetailPage(Activity activity, Item product) {

        if (Constant.SHOP_TYPE_TB.equals(product.getPlatform()) || Constant.SHOP_TYPE_TM.equals(product.getPlatform())) {
            Log.d("BAICHUAN", "TAO BAO ID: " + product.getOpid());
            if (product.getItemId() == 0) {
                showTaokeItemDetailByOpenId(activity, product.getOpid());
            } else {
                showTaokeItemDetailByItemId(activity, product.getOpid());
            }
        } else {
            forwardWeb(activity, product.getItemId());
        }

    }

    private static class TradeCallback implements TradeProcessCallback {
        private WeakReference<Activity> mActivityReference;

        public TradeCallback(Activity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void onPaySuccess(TradeResult tradeResult) {
            Toast.makeText(mActivityReference.get(), "支付成功", Toast.LENGTH_SHORT).show();
            String orders = "";
            for (int i = 0; i < tradeResult.paySuccessOrders.size(); i++) {
                if (i < tradeResult.paySuccessOrders.size() - 1) {
                    orders += tradeResult.paySuccessOrders.get(i) + ",";
                } else {
                    orders += tradeResult.paySuccessOrders.get(i);
                }
            }
            postOrders(orders, mActivityReference.get());
        }

        @Override
        public void onFailure(int code, String msg) {
//            Dialog dialog = new RedPackageDialog(mActivityReference.get(), R.style.dialog, 5);
//            dialog.show();
            if (code != ResultCode.USER_CANCEL.code) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivityReference.get());
                builder.setTitle("订单记录失败");
                builder.setMessage("code : " + code + "\n" + "msg : " + msg);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    public static void showTaokeItemDetailByItemId(final Activity activity, String itemId) {
        TaeWebViewUiSettings taeWebViewUiSettings = new TaeWebViewUiSettings();
        TaokeParams taokeParams = new TaokeParams();
        taokeParams.pid = MyApplication.ALIMAMA_PID;
        taokeParams.unionId = MyApplication.getCurrentUser().getUnid();
        ItemService itemService = AlibabaSDK.getService(ItemService.class);
        itemService.showTaokeItemDetailByItemId(activity, new TradeCallback(activity), taeWebViewUiSettings, Long.parseLong(itemId), 1, null, taokeParams);
    }

    public static void showTaokeItemDetailByOpenId(final Activity activity, String itemId) {
        TaeWebViewUiSettings taeWebViewUiSettings = new TaeWebViewUiSettings();
        TaokeParams taokeParams = new TaokeParams();
        taokeParams.pid = MyApplication.ALIMAMA_PID;
        taokeParams.unionId = MyApplication.getCurrentUser().getUnid();
        ItemService itemService = AlibabaSDK.getService(ItemService.class);
        itemService.showTaokeItemDetailByOpenItemId(activity, new TradeCallback(activity), taeWebViewUiSettings, itemId, 1, null, taokeParams);
    }

    private static void postOrders(final String orderIds, final Activity activity) {
        String url = InterfaceConstant.POST_ORDERS;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                loadCurrentActivity(activity, orderIds);
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, null, null);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("uuid", MyApplication.getCurrentUser().getUuid());
        requestBody.put("unid", MyApplication.getCurrentUser().getUnid());
        requestBody.put("order_id", orderIds);
        Log.d("BAICHUAN", "uuid : " + MyApplication.getCurrentUser().getUuid());
        Log.d("BAICHUAN", "unid : " + MyApplication.getCurrentUser().getUnid());
        Log.d("BAICHUAN", "order_id : " + orderIds);
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    private static void loadCurrentActivity(final Activity activity, final String orderId) {
        String url = InterfaceConstant.INTERFACE_HOST + "/activity/redenvelope/info";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    PromotionActivity promotionActivity = gson.fromJson(dataJsonStr, PromotionActivity.class);
                    Log.d("BAICHUAN", "获取当前活动成功：" + dataJsonStr);
                    if (promotionActivity.status) {
                        showDialogIfIsFirstOrder(activity, orderId, promotionActivity.id);
                    }
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private static void showDialogIfIsFirstOrder(final Activity activity, String orderId, final long actId) {
        String url = InterfaceConstant.ACTIVITY_HOST + "/api/order/first_order/" + MyApplication.getCurrentUser().getUuid() + "/" + orderId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("BAICHUAN","获取是否首单：" + response.getInt("code") + "----------" + response.getString("data"));
                            if (response.getInt("code") == MyReqSucessListener.SUCCESS) {
                                if (response.has("data")) {
                                    String dataJson = response.getString("data");
                                    Gson gson = new Gson();
                                    boolean flag = gson.fromJson(dataJson, Boolean.class);
                                    if (flag) {
                                        Dialog dialog = new RedPackageDialog(activity, R.style.dialog, actId);
                                        dialog.show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BAICHUAN","获取是否首单失败：" + error.getMessage());
            }
        });
        MyApplication.addToRequestQueue(jsonObjectRequest);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void forwardToGuideDetail(Context context, Guide guide) {
        ObjectType objType = MyApplication.getObjectType(guide.getType());
        Intent intent = new Intent(context, MyApplication.getTemplate(objType.getTemplateId()));
        Bundle bundle = new Bundle();
        bundle.putSerializable("guideId", guide.getGuideId());
        bundle.putSerializable("pageTitle", objType.getTitle());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void forwardToGuideDetailForResult(Activity activity, Guide guide, int code) {
        ObjectType objType = MyApplication.getObjectType(guide.getType());
        Intent intent = new Intent(activity, MyApplication.getTemplate(objType.getTemplateId()));
        Bundle bundle = new Bundle();
        bundle.putSerializable("guideId", guide.getGuideId());
        bundle.putSerializable("pageTitle", objType.getTitle());
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, code);
    }


    /**
     * 跳转逻辑
     *
     * @param context
     * @param type
     * @param link
     * @param pageTitle
     */
    public static void forwardCommon(Context context, int type, String link, String pageTitle) {
        if (type > 0 && type <= 100) {          //导购详情
            ObjectType objType = MyApplication.getObjectType(type);
            Intent intent = new Intent(context, MyApplication.getTemplate(objType.getTemplateId()));
            Bundle bundle = new Bundle();
            bundle.putLong("guideId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 100 && type <= 200) {          //商品详情
            Intent intent = new Intent(context, ProductWebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("itemId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 200 && type <= 300) {          //活动详情
            Class clazz;
            switch (type) {
                case 201:
                    clazz = ActWebActivity.class;
                    break;
                case 202:
                    clazz = ActWithoutShareWebActivity.class;
                    break;
                case 203:
                    clazz = SignInWebActivity.class;
                    break;
                case 204:
                    clazz = LotteryWebActivity.class;
                    break;
                case 205:
                    clazz = RedPackActWebActivity.class;
                    break;
                default:
                    clazz = ActWebActivity.class;
                    break;
            }
            Intent intent = new Intent(context, clazz);
            Bundle bundle = new Bundle();
            bundle.putLong("actId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 300 && type <= 400) {       //店铺详情
            Intent intent = new Intent(context, StoreWebActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong("storeId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1000 && type <= 1100) {      //导购列表
            Intent intent = new Intent(context, GuideListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1100 && type <= 1200) {         //商品列表
            Intent intent = new Intent(context, ProductListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1200 && type <= 1300) {         //店铺列表
            Intent intent = new Intent(context, StoreListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {                                  //广告，通过浏览器跳转
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    public static void pushForwardCommon(Context context, int type, String link, String pageTitle) {
        if (type > 0 && type <= 100) {          //导购详情
            ObjectType objType = MyApplication.getObjectType(type);
            Intent intent = new Intent(context, MyApplication.getTemplate(objType.getTemplateId()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putLong("guideId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 100 && type <= 200) {          //商品详情
            Intent intent = new Intent(context, ProductWebActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putLong("itemId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 200 && type <= 300) {          //活动详情
            Class clazz;
            switch (type) {
                case 201:
                    clazz = ActWebActivity.class;
                    break;
                case 202:
                    clazz = ActWithoutShareWebActivity.class;
                    break;
                case 203:
                    clazz = SignInWebActivity.class;
                    break;
                case 204:
                    clazz = LotteryWebActivity.class;
                    break;
                case 205:
                    clazz = RedPackActWebActivity.class;
                    break;
                default:
                    clazz = ActWebActivity.class;
                    break;
            }
            Intent intent = new Intent(context, clazz);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putLong("actId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 300 && type <= 400) {       //店铺详情
            Intent intent = new Intent(context, StoreWebActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putLong("storeId", Long.parseLong(link));
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1000 && type <= 1100) {      //导购列表
            Intent intent = new Intent(context, GuideListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1100 && type <= 1200) {         //商品列表
            Intent intent = new Intent(context, ProductListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type > 1200 && type <= 1300) {         //店铺列表
            Intent intent = new Intent(context, StoreListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putString("apiUrl", link);
            bundle.putString("pageTitle", pageTitle);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type == TypeConstant.USER || type == TypeConstant.WALLET_NOTIFICATION) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClass(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("type", type);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {                                  //广告，通过浏览器跳转
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    public static String wrapPlatform(String platformNick) {
        String platform = "淘宝";
        switch (platformNick) {
            case "tb":
                platform = "淘宝";
                break;
            case "tm":
                platform = "天猫";
                break;
            case "yhd":
                platform = "一号店";
                break;
            case "sn":
                platform = "苏宁";
                break;
            case "ymx":
                platform = "亚马逊";
                break;
            case "jd":
                platform = "京东";
                break;
            case "dd":
                platform = "当当";
                break;
            case "mybb":
                platform = "蜜芽宝贝";
                break;
            case "jmyp":
                platform = "聚美优品";
                break;
            case "htc":
                platform = "海淘城";
                break;
        }
        return platform;
    }

    public static String getImageURL(String urlSet, int type) {
        if (urlSet == null) {
            return "";
        }
        if (urlSet.indexOf(",") == -1) {
            return urlSet;
        }
        String picPrefix = "_small";
        switch (type) {
            case Image.SIZE_SMALL:
                break;
            case Image.SIZE_MIDDLE:
                picPrefix = "_middle";
                break;
            case Image.SIZE_BIG:
                picPrefix = "_big";
                break;
            case Image.SIZE_LARGE:
                picPrefix = "_large";
                break;
            case Image.SIZE_LOGO:
                picPrefix = "logo-";
                break;
            case Image.SIZE_THUMB:
                picPrefix = "_thumb";
                break;
            default:
                picPrefix = "_small";
                break;
        }
        String picUrl = "";
        String[] guidePics = urlSet.split(",");
        for (int i = 0; i < guidePics.length; i++) {
            if (guidePics[i].indexOf(picPrefix) >= 0) {
                picUrl = guidePics[i];
                break;
            }
        }
        return picUrl;
    }

    public static String wrapOrderStatus(int status) {
        String statusStr = "预计返利";
        switch (status) {
            case 0:
                statusStr = "预计返利";
                break;
            case 1:
                statusStr = "近期将存入";
                break;
            case 2:
                statusStr = "已经存入";
                break;
            case 3:
                statusStr = "订单关闭";
                break;
            case 4:
                statusStr = "退单成功";
                break;
        }
        return statusStr;
    }

    public static String wrapOrderStatus(int status, int type) {
        String statusStr = "";
        if(type == 0){
            switch (status) {
                case 0:
                    statusStr = "预计返利";
                    break;
                case 1:
                    statusStr = "近期将存入";
                    break;
                case 2:
                    statusStr = "已经存入";
                    break;
                case 3:
                    statusStr = "订单关闭";
                    break;
                case 4:
                    statusStr = "退单成功";
                    break;
            }
        }else{
            switch (status) {
                case 1:
                    statusStr = "近期将扣除";
                    break;
                case 2:
                    statusStr = "已经扣除";
                    break;
            }
        }

        return statusStr;
    }

    public static int[] wrapOrderStatusColor(int status) {
        int[] color = new int[]{26, 188, 156};
        switch (status) {
            case 0:
                color = new int[]{26, 188, 156};
                break;
            case 1:
                color = new int[]{252, 130, 0};
                break;
            case 2:
                color = new int[]{255, 123, 115};
                break;
        }
        return color;
    }

    public static int wrapOrderStatusBackground(int status) {
        int drawable = R.drawable.bg_action_button_confirm;
        switch (status) {
            case 0:
                drawable = R.drawable.bg_action_button_confirm;
                break;
            case 1:
                drawable = R.drawable.bg_action_button_will;
                break;
            case 2:
                drawable = R.drawable.bg_action_button_already;
                break;
        }
        return drawable;
    }
}
