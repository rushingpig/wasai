package com.seastar.wasai.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI wxApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxApi = WXAPIFactory.createWXAPI(this.getApplicationContext(), MyApplication.WEIXIN_APP_ID, false);
        wxApi.handleIntent(this.getIntent(), this);

    }

    /**
     * @see {@link Activity#onNewIntent}
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (wxApi != null) {
            setIntent(intent);
            wxApi.handleIntent(intent, this);
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        MyApplication.weixin_resp = resp;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    String code = ((SendAuth.Resp) resp).code;
                    MyApplication.WEIXIN_AUTH_CODE = code;
                } else {
                    Toast.makeText(getApplicationContext(), ToastMessage.SUCCESS_TO_SHARE, Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp.getType() != ConstantsAPI.COMMAND_SENDAUTH) {
                    Toast.makeText(getApplicationContext(), ToastMessage.CANCLE_TO_SHARE, Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (resp.getType() != ConstantsAPI.COMMAND_SENDAUTH) {
                    Toast.makeText(getApplicationContext(), ToastMessage.NO_PERMISSION_TO_SHARE, Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}