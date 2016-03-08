
package com.seastar.wasai.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.LoginService;
import com.seastar.wasai.utils.Encrypt;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;

public class SetPasswordForgetActivity extends CanBeClosedActivity {
    String TAG = "SetPasswordForgetActivity";
    EditText edit_passwd;
    Button buttonNext;
    ImageView backimg;
    CheckBox checkbox;
    String para_mob, para_uuid;
    private Handler mMainHandler = null;
    WaitingDlg waiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setpasswdforget);
        Intent intent = getIntent();
        waiting = new WaitingDlg(this);
        para_mob = intent.getStringExtra("MOB");
        para_uuid = intent.getStringExtra("UUID");
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        edit_passwd = (EditText) findViewById(R.id.edit_passwd);
        edit_passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isContainChineseCharacters(edit_passwd.getText().toString())) {
                    Toast.makeText(SetPasswordForgetActivity.this, "密码不能包含汉字，请重新输入", Toast.LENGTH_SHORT).show();
                    edit_passwd.setText("");
                }
            }
        });
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_passwd.length() <= 0) {
                    Toast.makeText(getApplicationContext(), ToastMessage.PLEASE_SET_PASSWORD,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                alterPassword();
            }

        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    edit_passwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edit_passwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        backimg = (ImageView) findViewById(R.id.action_back);
        backimg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int para1 = msg.arg1;
                super.handleMessage(msg);
                waiting.showWaitingDlg(false);
                if (para1 == 0) {
                    Toast.makeText(getApplicationContext(), ToastMessage.SUCCESS_SET_PASSWORD,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction("REGISTER_SUCCESS");
                    SetPasswordForgetActivity.this.sendBroadcast(intent);
                } else if (para1 == 1) {
                    Toast.makeText(getApplicationContext(), ToastMessage.OPERATION_FAILED,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    void alterPassword() {
        waiting.showWaitingDlg(true);
        new Thread(new Runnable() {
            public void run() {
                Log.v(TAG, "passwd:" + edit_passwd.getText().toString());
                Log.v(TAG, "para_uuid:" + para_uuid);
                Log.v(TAG, "para_mob:" + para_mob);
                LoginService loginService = new LoginService();
                Message toMain = mMainHandler.obtainMessage();
                if (loginService.alterPassword(para_uuid, Encrypt.MD5(edit_passwd.getText().toString()))) {
                    toMain.arg1 = 0;
                } else {
                    toMain.arg1 = 1;
                }
                toMain.arg2 = 0;
                mMainHandler.sendMessage(toMain);
            }
        }).start();

    }
}
