package com.seastar.wasai.views.extendedcomponent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seastar.wasai.Entity.Constant;
import com.seastar.wasai.Entity.Upgrade;
import com.seastar.wasai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 杨腾
 * 升级对话框
 */
public class UpgradeDialog extends Dialog {
    private Button btnUpdateNow;
    private Button btnUpdateLater;
    private TextView newVerInfo;
    private TextView updateContent;
    private Upgrade upgrade;
    private Context context;
    private Handler mHandler = null;

    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;

    private ProgressBar mProgress;
    private int progress;

    private boolean cancelUpdate;
    private String mSavePath;
    private TextView percentView;

    public UpgradeDialog(Context context,Upgrade upgrade) {
        super(context);
        this.context = context;
        this.upgrade = upgrade;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_upgrade);
        this.setCancelable(false);
        newVerInfo = (TextView) findViewById(R.id.newVerInfo);
        newVerInfo.setText(upgrade.getVerString());

        updateContent = (TextView) findViewById(R.id.updateContent);
        updateContent.setText(upgrade.getDescription().replace("\\n", "\n"));

        btnUpdateNow = (Button) findViewById(R.id.buttonUpdateNow);

        btnUpdateNow.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog();
            }
        });

        btnUpdateLater = (Button) findViewById(R.id.buttonUpdateLater);
        btnUpdateLater.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constant.FORCE_UPGRADE == upgrade.getForce()){
                    System.exit(0);
                }else{
                    UpgradeDialog.this.dismiss();
                    cancelUpdate = true;
                }

            }
        });

        mHandler = new Handler(context.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD:
                        mProgress.setProgress(progress);
                        percentView.setText(progress + "%");
                        break;
                    case DOWNLOAD_FINISH:
                        installApk();
                        break;
                    default:
                        break;
                }
            };
        };
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        final LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.layout_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        percentView =  (TextView) v.findViewById(R.id.progress_percent);
        percentView.setText("0%");
        builder.setView(v);
        AlertDialog mDownloadDialog = builder.create();
        mDownloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.show();
        downloadApk();
    }

    /**
     * @Title: downloadApk
     * @Description: 下载APK
     * @return void
     * @throws
     */
    private void downloadApk() {
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     *
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String sdPath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdPath + "download";
                    URL url = new URL(upgrade.getUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "wasai.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numRead = is.read(buf);
                        count += numRead;
                        progress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numRead <= 0) {
                            progress = 100;
                            mHandler.sendEmptyMessage(DOWNLOAD);
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        fos.write(buf, 0, numRead);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UpgradeDialog.this.dismiss();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkFile = new File(mSavePath, "wasai.apk");
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}