
package com.seastar.wasai.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.seastar.wasai.R;

import java.lang.ref.WeakReference;

import cn.jpush.android.api.InstrumentedActivity;

/**
 * 启动页
 */
public class StartActivity extends InstrumentedActivity {
    private static final int SPLASH_DISPLAY_LENGHT = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
        new Handler().postDelayed(new TimeToTargetActivity(this), SPLASH_DISPLAY_LENGHT);
    }

    private static class TimeToTargetActivity implements Runnable {
        private WeakReference<StartActivity> mRef;

        public TimeToTargetActivity(StartActivity activity) {
            mRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            StartActivity activity = mRef.get();
            Intent intent;
            SharedPreferences setting = activity.getSharedPreferences("myApp", MODE_PRIVATE);
            Boolean isFirst = setting.getBoolean("FIRST_START_GUIDE", true);
            if (isFirst) {
                setting.edit().putBoolean("FIRST_START_GUIDE", false).commit();
                intent = new Intent(activity, StartGuideActivity.class);
            }else{
                intent = new Intent(activity, MainActivity.class);
            }
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
