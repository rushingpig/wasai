
package com.seastar.wasai.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.LoginService;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

public class ForgetPassword extends CanBeClosedActivity {
    String TAG = "ForgetPassword";
    EditText edit_mob;
    Button buttonForgetpwd;
    ImageView backimg;
    String uuid;

    static int TAG_SENDSMS_SUCCESS = 0;
    static int TAG_SENDSMS_FAIL = 1;
    static int TAG_MOBNOTEXIST = 2; // 手机号码不存在

    private Handler mMainHandler = null;
    WaitingDlg waiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgetpasswd);
        waiting = new WaitingDlg(this);
        edit_mob = (EditText) findViewById(R.id.edit_mob);
        buttonForgetpwd = (Button) findViewById(R.id.buttonForgetpwd);
        buttonForgetpwd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_mob.length() != 11) {
                    Toast.makeText(getApplicationContext(), ToastMessage.MOBILE_NO_WRONG,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                getsms();
            }

        });

        backimg = (ImageView) findViewById(R.id.action_back);
        backimg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int para1 = msg.arg1;
                int para2 = msg.arg2;
                super.handleMessage(msg);
                // Log.v(TAG,"main get msg now !");
                waiting.showWaitingDlg(false);
                if (para1 == TAG_SENDSMS_SUCCESS) {
                    Intent intent = new Intent();
                    intent.putExtra("MOB", "" + edit_mob.getText().toString());
                    intent.putExtra("UUID", "" + uuid);
                    intent.setClass(ForgetPassword.this, CheckSmsForgetActivity.class);
                    startActivity(intent);
                } else if (para1 == TAG_SENDSMS_FAIL) {
                    Toast.makeText(getApplicationContext(), ToastMessage.OPERATION_FAILED,
                            Toast.LENGTH_SHORT).show();
                } else if (para1 == TAG_MOBNOTEXIST) {
                    Toast.makeText(getApplicationContext(), ToastMessage.MOBILE_NO_NOT_FOUND,
                            Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    // 13530855304  123456

    void getsms() {
        waiting.showWaitingDlg(true);
        new Thread(new Runnable() {
            public void run() {
                Message toMain = mMainHandler.obtainMessage();

                Log.v(TAG, "reg:" + edit_mob.getText().toString());
                LoginService loginService = new LoginService();

                // 检测输入的电话号码是否存在
                uuid = loginService.checkIdExisted(edit_mob.getText().toString(), "phone");
                if (uuid != null) {
                    toMain.arg1 = 0;
                    if (loginService.sendSms(edit_mob.getText().toString())) {
                        toMain.arg1 = TAG_SENDSMS_SUCCESS;
                    } else {
                        toMain.arg1 = TAG_SENDSMS_FAIL;
                    }
                } else {
                    toMain.arg1 = TAG_MOBNOTEXIST;
                }
                toMain.arg2 = 0;
                mMainHandler.sendMessage(toMain);
            }
        }).start();

    }
}
