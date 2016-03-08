
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
import android.widget.TextView;
import android.widget.Toast;

import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.LoginService;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

public class CheckSmsActivity extends CanBeClosedActivity {
    String TAG = "CheckSmsActivity";
    TextView txtsmstip2;
    EditText edit_sms;
    Button buttonNext, buttonReget;
    ImageView backimg;
    String para_mob;
    private Handler mMainHandler = null;
    WaitingDlg waiting;


    final int CMD_CHECK = 0x100000;
    final int CMD_RESEND = 0x200000;
    final int CMD_TIMER = 0x300000;
    final int TAG_SENDSMS_SUCCESS = 0x400000;
    final int TAG_SENDSMS_FAIL = 0x500000;
    int RESENDTIME = 60;
    boolean runFlag = true, resendEnable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checksms);
        Intent intent = getIntent();
        para_mob = intent.getStringExtra("MOB");
        waiting = new WaitingDlg(this);
        txtsmstip2 = (TextView) findViewById(R.id.txtsmstip2);
        txtsmstip2.setText("验证码已发送到" + para_mob + "，请查收");
        edit_sms = (EditText) findViewById(R.id.edit_sms);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_sms.length() <= 0) {
                    Toast.makeText(getApplicationContext(), ToastMessage.PLEASE_INPUT_VERIFY_CODE,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                checksms();
            }

        });

        buttonReget = (Button) findViewById(R.id.buttonReget);
        buttonReget.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonReget.setEnabled(false);
                edit_sms.setText("");
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

                if (para1 == 0) {
                    waiting.showWaitingDlg(false);
                    Intent intent = new Intent();
                    intent.putExtra("MOB", "" + para_mob);
                    intent.setClass(CheckSmsActivity.this, SetPasswordActivity.class);
                    startActivity(intent);
                } else if (para1 == 1) {
                    waiting.showWaitingDlg(false);
                    Toast.makeText(getApplicationContext(), ToastMessage.VERIFY_CODE_WRONG,
                            Toast.LENGTH_SHORT).show();
                } else if (para1 == 2) {
                    waiting.showWaitingDlg(false);
                    Toast.makeText(getApplicationContext(), ToastMessage.VERIFY_CODE_EXPIRED,
                            Toast.LENGTH_SHORT).show();
                } else if (para1 == CMD_TIMER) {
                    if (para2 == 1000) {
                        buttonReget.setEnabled(true);
                        buttonReget.setText("获取验证码");
                    } else {
                        buttonReget.setText("" + para2 + "秒");
                    }
                } else if (para1 == TAG_SENDSMS_SUCCESS) {
                    waiting.showWaitingDlg(false);
                    Toast.makeText(getApplicationContext(), ToastMessage.VERIFY_CODE_SENDED,
                            Toast.LENGTH_SHORT).show();
                    buttonReget.setEnabled(false);
                    resendEnable = false;
                } else if (para1 == TAG_SENDSMS_FAIL) {
                    waiting.showWaitingDlg(false);
                    Toast.makeText(getApplicationContext(), ToastMessage.OPERATION_FAILED,
                            Toast.LENGTH_SHORT).show();
                }


            }
        };

        buttonReget.setEnabled(false);
        // 开启定时器
        startTimer();

    }

    void checksms() {
        waiting.showWaitingDlg(true);
        new Thread(new Runnable() {
            public void run() {
                Message toMain = mMainHandler.obtainMessage();
                Log.v(TAG, "reg:" + edit_sms.getText().toString());
                LoginService loginService = new LoginService();
                toMain.arg1 = loginService.checkSmsCode(para_mob, edit_sms.getText().toString());
                toMain.arg2 = 0;
                mMainHandler.sendMessage(toMain);
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "call on onDestroy!");
        runFlag = false;
        super.onDestroy();
    }

    public void startTimer() {
        Log.v(TAG, "call countChatTime ");
        new Thread(new Runnable() {
            public void run() {
                int times = RESENDTIME;
                String str = "times:";
                while (runFlag) {
                    Log.v(TAG, "go 1 !");
                    if (!resendEnable) {
                        Message toMain = mMainHandler.obtainMessage();
                        times--;
                        Log.v(TAG, "go 2 !");
                        if (times == 0) {
                            times = RESENDTIME;
                            resendEnable = true;
                            toMain.arg1 = CMD_TIMER;
                            toMain.arg2 = 1000;
                            mMainHandler.sendMessage(toMain);
                            continue;
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Log.v(TAG, "thread ex:at !" + str);
                        }


                        toMain.arg1 = CMD_TIMER;
                        toMain.arg2 = times;
                        mMainHandler.sendMessage(toMain);
                    } else {
                        times = RESENDTIME;
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Log.v(TAG, "thread ex:at !" + str);
                        }
                    }

                }

            }

        }).start();
    }

    void getsms() {
        waiting.showWaitingDlg(true);
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "reg:" + para_mob);
                LoginService loginService = new LoginService();
                Message toMain = mMainHandler.obtainMessage();

                if (loginService.sendSms(para_mob)) {
                    toMain.arg1 = TAG_SENDSMS_SUCCESS;

                } else {
                    toMain.arg1 = TAG_SENDSMS_FAIL;
                }

                toMain.arg2 = 0;
                mMainHandler.sendMessage(toMain);
            }
        }).start();

    }
}
