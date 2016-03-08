
package com.seastar.wasai.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.seastar.wasai.service.UserActionLogService;
import com.seastar.wasai.utils.thread.ThreadPool;
import com.seastar.wasai.views.MainActivity;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



/**
 * 未捕获的异常处理
 *  jamie
 * <pre>
 * a.将异常信息打印到log，方便定位问题（保持上下文连贯）
 * b.针对OOM开发阶段直接导出堆栈信息，定位问题
 * </pre>
 * 
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = "UncaughtExceptionManager";

    private final static String LOG_DIR_NAME = "/wasai/crash/";

    // default log out time, 7days.
    private final static long LOG_OUT_TIME = 1000 * 60 * 60 * 24 * 7;

    private final static int LOGCAT_MAX_LENGTH = 2000 * 1024;
    
    private PreferencesWrapper mPreferencesWrapper = null;
    
    private final static String[] LOGCAT_COMMAND = new String[] {
            "logcat", "-d"
    };

    private final static String PREFERENCE_NAME = "UncaughtExceptionManager";
    private final static String PREFERENCE_REPORT_LOG_TIMESTAMP = "report_log_timestamp";
    private final static String PREFERENCE_REPORT_HPROF_TIMESTAMP = "report_hprof_timestamp";

    private final static Thread.UncaughtExceptionHandler sDefaultParent = Thread.getDefaultUncaughtExceptionHandler();

    private static volatile String sLineSeparator;

    private static ThreadLocal<SimpleDateFormat> sLocalDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        }
    };

    private final Context mContext;
    private volatile Thread.UncaughtExceptionHandler mParent;
    private volatile UncaughtExceptionInterceptor mInterceptor;

    private volatile UncaughtExceptionReporter mReporter;
    private final Object mReportLogLock = new Object();
    private final Object mReportHprofLock = new Object();

    private volatile PackageInfo mPackageInfo;

    private UncaughtExceptionHandler(Context context) {
        mContext = context.getApplicationContext();
        mPreferencesWrapper = new PreferencesWrapper(mContext);
    }

    /** 
     * Register this uncaught exception manager.
     */
    public void register() {
        if (this != Thread.getDefaultUncaughtExceptionHandler()) {
            synchronized (this) {
                Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (this != defaultHandler) {
                    assignParent(defaultHandler);
                    Thread.setDefaultUncaughtExceptionHandler(this);
                }
            }
        }
    }

    /**
     * Set uncaught exception interceptor.
     * 
     * @param interceptor uncaught exception interceptor.
     */
    public void setInterceptor(UncaughtExceptionInterceptor interceptor) {
        mInterceptor = interceptor;
    }

    /**
     * Delete outmoded logs.
     */
    public void deleteLogs() {
        deleteLogs(LOG_OUT_TIME);
    }

    /**
     * Delete outmoded logs.
     * 
     * @param timeout outmoded timeout.
     */
    public void deleteLogs(final long timeout) {
        final File logDir = getLogDir();
        if (logDir == null) {
            return;
        }
        ThreadPool.getInstance().submit(new ThreadPool.Job<Object>() {
            @Override
            public Object run(ThreadPool.JobContext jc) {
                try {
                    final long currTime = System.currentTimeMillis();
                    File[] files = logDir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            File f = new File(dir, filename);
                            return currTime - f.lastModified() > timeout;
                        }
                    });
                    if (files != null) {
                        for (File f : files) {
                            FileUtil.delete(f);
                        }
                    }
                } catch (Exception e) {
                }
                return null;
            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        UncaughtExceptionInterceptor interceptor = mInterceptor;
        if (interceptor != null && interceptor.onInterceptExceptionBefore(thread, ex)) {
            return;
        }

        Thread mThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
		        UserActionLogService logs = new UserActionLogService();
		        Gson gson = new Gson();
		        String message =gson.toJson(getLogcat(LOGCAT_MAX_LENGTH));
		        if(logs.log("debug", message)){
		        	Message msg = new Message();
		    		msg.what = 0;
		    		msg.arg1=1;
		    		handler.sendMessage(msg);
		        }
			}
		});
        mThread.start();

        
        
        // handle by self.
//        onUncaughtException(thread, ex); //////注释不写入文件
        // report to exception manager.
        

        
        if (interceptor != null && interceptor.onInterceptExceptionAfter(thread, ex)) {
            return;
        }
        // parent & clear job.
        boolean handled = false;
        try {
            handled = deliverUncaughtExceptionToParent(thread, ex);
        } finally {
            if (!handled) {
                // Try everything to make sure this process goes away.
//                
                exit();
            }
        }
    }
    
	final Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
					if(msg.arg1 == 1){
						deleteLogs();
					}else {
						
					}
				}
				break;

			}
		}
	};


    /**
     * Called when uncaught exception occurs.
     */
    protected void onUncaughtException(Thread thread, Throwable ex) {
        BufferedWriter writer = null;
        try {
            File dir = getLogDir();
            if (dir == null) {
                return;
            }

            File logFile = new File(dir, getLogName());
            writer = new BufferedWriter(new FileWriter(logFile));

            writer.write("\t\n==================BasicInfo==================\t\n");
            writeBasicInfo(writer);
            writeException(writer, ex);
            // flush before writing logcat.
            writer.flush();
            writeLogcat(writer);

        } catch (IOException t) {
            Log.d(TAG, "exception occurs when handling uncaught exception: " + ex.getMessage(), t);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // empty.
                }
            }
        }
    }

    private void assignParent(Thread.UncaughtExceptionHandler parent) {
        if (parent != this) {
            mParent = parent;
        }
    }

    private boolean deliverUncaughtExceptionToParent(Thread thread, Throwable ex) {
        Thread.UncaughtExceptionHandler parent = mParent;
        if (parent == null || parent == this) {
            parent = sDefaultParent;
        }
        if (parent == null || parent == this) {
            return false;
        }
        parent.uncaughtException(thread, ex);
        return true;
    }

    private void writeBasicInfo(Writer writer) throws IOException {
        // prepare package info.
        PackageInfo pkgInfo = getPackageInfo();
        // perform write.
        writer.write("APP_VERSION:" + (pkgInfo != null ? pkgInfo.versionName : null) + "|"
                + (pkgInfo != null ? pkgInfo.versionCode : null) + "\t\n");
        writer.write("PHONE_MODEL:" + Build.MODEL + "\t\n");
        writer.write("ANDROID_SDK:" + Build.VERSION.SDK + "|" + Build.VERSION.SDK_INT + "\t\n");
        writer.write("PROCESS:" + android.os.Process.myPid() + "\t\n");
        writer.write(getDate() + "\t\n");
    }

    private void writeException(Writer writer, Throwable ex) throws IOException {
        writer.write(Log.getStackTraceString(ex));
    }

    private void writeLogcat(Writer writer) throws IOException {
        writer.write(getLogcat(LOGCAT_MAX_LENGTH));
    }

    private PackageInfo getPackageInfo() {
        if (mPackageInfo == null) {
            synchronized (this) {
                if (mPackageInfo == null) {
                    try {
                        mPackageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        // empty.
                    }
                }
            }
        }
        return mPackageInfo;
    }

    private String getLogcat(int maxLength) {

        final StringBuilder log = new StringBuilder();

        Process process = null;
        BufferedReader reader = null;
        try {
            String[] commandLine = LOGCAT_COMMAND;
            process = Runtime.getRuntime().exec(commandLine);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null && log.length() < maxLength) {
                log.append(line);
                log.append(getLineSeparator());
                
            }
        } catch (Throwable e) {
            // empty.
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // empty.
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
//        System.out.println("log=> " + log.toString());
        return log.toString();
    }

    private File getLogDir() {
        String path = GeneralUtil.getStorePath(mContext, LOG_DIR_NAME);
        
        if (path == null) {
            return null;
        }
        File dir = new File(path);
        if (dir.isFile()) {
            // in case.
            FileUtil.delete(dir);
        }
        if (!dir.exists()) {
            return dir.mkdirs() ? dir : null;
        } else {
            return dir;
        }
    }

    // ---------------- report ----------------
    public void setReporter(UncaughtExceptionReporter reporter) {
        if (mReporter == reporter) {
            return;
        }
        synchronized (this) {
            if (mReporter == reporter) {
                return;
            }
            mReporter = reporter;
            if (reporter != null) {
                // do report when new reporter prepared.
                ThreadPool.getInstance().submit(new ThreadPool.Job<Object>() {
                    @Override
                    public Object run(ThreadPool.JobContext jc) {
                        handleReportLog();
                        return null;
                    }
                });
            }
        }
    }

    private void handleReportLog() {
        UncaughtExceptionReporter reporter = mReporter;
        if (reporter == null) {
            return;
        }
        synchronized (mReportLogLock) {
            // obtain log files.
            File dir = getLogDir();
            if (dir == null) {
                return;
            }
            final long now = System.currentTimeMillis();
            final long last = mPreferencesWrapper.getLongValue(PREFERENCE_REPORT_LOG_TIMESTAMP, 0);
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.lastModified() > last;
                }
            });
            // perform report.
            boolean reported = true;
            if (files != null && files.length > 0) {
                reported = reporter.onReportLog(files);
            }
            if (reported) {
            	mPreferencesWrapper.setLongValueAndCommit(PREFERENCE_REPORT_LOG_TIMESTAMP, now);
            }
        }
    }

   

    // -------------- preference --------------
//    private SharedPreferences obtainPreference() {
//        return PreferenceUtil.getGlobalPreference(mContext, PREFERENCE_NAME);
//    }

    // ----------------- utils ----------------
    private static String getLogName() {
        return getDate() + ".log";
    }

    private static String getLineSeparator() {
        if (sLineSeparator == null) {
            sLineSeparator = System.getProperty("line.separator");
        }
        return sLineSeparator;
    }

    private static String getDate() {
        return sLocalDateFormat.get().format(new Date(System.currentTimeMillis()));
    }

    private static void exit() {
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(10);
    	Intent mIntent = new Intent(MyApplication.getInstance(), MainActivity.class);
    	MyApplication.getInstance().startActivity(mIntent);
    }

    public static interface UncaughtExceptionInterceptor {
        /**
         * Called before this uncaught exception be handled by {@link UncaughtExceptionHandler}.
         * 
         * @return true if intercepted, which means this event won't be handled by
         *         {@link UncaughtExceptionHandler}.
         */
        public boolean onInterceptExceptionBefore(Thread t, Throwable ex);

        /**
         * Called after this uncaught exception be handled by {@link UncaughtExceptionHandler} (but
         * before {@link UncaughtExceptionHandler}'s parent).
         * 
         * @return true if intercepted, which means this event won't be handled by
         *         {@link UncaughtExceptionHandler}'s parent.
         */
        public boolean onInterceptExceptionAfter(Thread t, Throwable ex);
    }

    public static interface UncaughtExceptionReporter {
        /**
         * Called when should report crash logs. This is called async.
         * 
         * @param logFiles log files.
         */
        public boolean onReportLog(File[] logFiles);

        /**
         * Called when should report hprof files. This is called async.
         * 
         * @param hprofFiles hprof files.
         */
        public boolean onReportHprof(File[] hprofFiles);
    }


    // -------------- singleton --------------

    private static volatile UncaughtExceptionHandler sInstance;

    public static UncaughtExceptionHandler getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UncaughtExceptionHandler.class) {
                if (sInstance == null) {
                    sInstance = new UncaughtExceptionHandler(context);
                }
            }
        }
        return sInstance;
    }
}
