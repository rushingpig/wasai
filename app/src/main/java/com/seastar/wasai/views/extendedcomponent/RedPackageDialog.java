package com.seastar.wasai.views.extendedcomponent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.plugin.SharePlugin;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

import org.json.JSONObject;

/**
 * 杨腾
 * 红包提示
 */
public class RedPackageDialog extends Dialog {
    private Context context;
    private SharePlugin sharePlugin;
    private Activity pageActivity;
    private long actId;
    private WaitingDlg dlg;

    public RedPackageDialog(Context context, int theme, long actId) {
        super(context, theme);
        this.context = context;
        this.actId = actId;
        if (context instanceof Activity) {
            this.pageActivity = (Activity) context;
        }
        this.dlg = new WaitingDlg(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_red);
        this.setCancelable(true);
        sharePlugin = new SharePlugin(pageActivity, pageActivity.getWindow().getDecorView().findViewById(android.R.id.content));
        sharePlugin.init();
        findViewById(R.id.call_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.showWaitingDlg(true);
                getActDetailAndShowPop();
            }
        });
    }

    private void getActDetailAndShowPop() {
        String url = InterfaceConstant.ACTIVITY_DETAIL + "/" + actId;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    com.seastar.wasai.Entity.Activity act = gson.fromJson(dataJsonStr, com.seastar.wasai.Entity.Activity.class);
                    sharePlugin.setShareObject(act);
                    RedPackageDialog.this.dismiss();
                    sharePlugin.showPop();
                    dlg.showWaitingDlg(false);
                    Log.d(TAG, "获取活动详情成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}