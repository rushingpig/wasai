package com.seastar.wasai.utils;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by yangteng on 2015/6/5.
 */
public class VolleyExceptionHelper {
    private static final Logger logger = LoggerFactory.getLogger(VolleyExceptionHelper.class);
    private static long lastToastTime;

    private static void helper(VolleyError error, View errorView) {
//        logger.debug("访问接口出错了，快ping吧");
//        ping(1, "www.baidu.com");
//        ping(1, "api.91wasai.com");
//        ping(1,"112.74.91.233");
        if (error instanceof NoConnectionError) {
            if (errorView != null) {
                errorView.setVisibility(View.VISIBLE);
            } else {
                showToast(ToastMessage.NET_WORK_NOT_WORK);
            }
        } else if (error instanceof NetworkError) {
            if (errorView != null) {
                errorView.setVisibility(View.VISIBLE);
            } else {
                showToast(ToastMessage.SERVER_NOT_WORK);
            }
        } else if (error instanceof AuthFailureError) {
            showToast(ToastMessage.ERROR);
        } else if (error instanceof ServerError) {
            showToast(ToastMessage.ERROR);
        } else if (error instanceof ParseError) {
            Log.e("ERROR", error.getMessage());
            showToast(ToastMessage.ERROR);
        } else if (error instanceof TimeoutError) {
            if (errorView != null) {
                errorView.setVisibility(View.VISIBLE);
            } else {
                showToast(ToastMessage.NET_WORK_NOT_WORK);
            }
        }
    }

    public static void helper(VolleyError error, View errorView, View loadingView) {
        if (loadingView != null) {
            loadingView.setVisibility(View.INVISIBLE);
        }
        helper(error, errorView);
    }

    public static void helper(VolleyError error, View errorView, View loadingView, PullToRefreshAdapterViewBase listView) {
        if (listView != null) {
            listView.onRefreshComplete();
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.INVISIBLE);
        }
        helper(error, errorView);
    }

    private static void showToast(String msg) {
        long currentTime = new Date().getTime();
        if (currentTime - lastToastTime > 5 * 1000) {
            Toast.makeText(MyApplication.getContextObject(), msg, Toast.LENGTH_SHORT).show();
            lastToastTime = currentTime;
        }
    }

    private static void ping(int pingNum,String address) {
        logger.debug("开始ping " + address + "," + pingNum + "次");
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("/system/bin/ping -c " + pingNum + " " + address);
            int status = p.waitFor();

            if (status == 0) {
                logger.debug("ping " + address + "," + pingNum + "次，结果：成功！" );
            } else {
                logger.debug("ping " + address + "," + pingNum + "次，结果：失败！" );
            }
            String lost = new String();
            String delay = new String();
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String str = new String();

            //读出所有信息并显示
            while ((str = buf.readLine()) != null) {
                str = str + "\r\n";
            }
            logger.debug("详细：" );
            logger.debug(str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
