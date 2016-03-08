package com.seastar.wasai.views.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.seastar.wasai.views.search.SearchMainActivity;

/**
 * Created by jamie on 2015/6/26.
 */
public class NotifyOfDataBaseActivity extends BaseActivity{

    private MyReceiver mMyReceiver = null;
    private boolean registerReceiver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    public void nofityOfData(Intent intent) {

    }

    private void registerReceiver() {
        mMyReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchMainActivity.NOTI_OF_DATA);
        registerReceiver(mMyReceiver, filter);
        registerReceiver = true;
    }

    private void unregisterReceiver() {
        if (registerReceiver) {
            unregisterReceiver(mMyReceiver);
            registerReceiver = false;
        }
        mMyReceiver = null;
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SearchMainActivity.NOTI_OF_DATA)) {
                try {
                    nofityOfData(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }



    @Override
    public void finishActivity() {

    }
}
