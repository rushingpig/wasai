package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.seastar.wasai.R;
import com.seastar.wasai.utils.DisplayUtil;
import com.seastar.wasai.utils.thread.ThreadPool;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * @PackageName: extendedcomponent
 * @ClassName: NewProgressWebView
 * @Author: 黄锦鹏
 * @CreateDate : 2015.7.3 12:04：02
 * @Description: webview页面的进度条及奶瓶等待页面或返利等待页面
 * @SvnVersion: $$Rev: 3625 $$
 * @UpdateAuthor: $$Author: yangteng $$
 * @UpdateTime: $$Date: 2015-07-31 09:40:00 +0800 (Fri, 31 Jul 2015) $$
 * @UpdateDescription: 将进度条和奶瓶等待和返利等待整合，实现更佳的用户体验，优化重定向的等待体验
 */
@SuppressWarnings("deprecation")
public class NewProgressWebView extends WebView implements LoadRebateView.OnTvAnimationListener, Animation.AnimationListener {

    private Context context;
    private ProgressBar progressbar;
    private LoadRebateView mLoadRebateView;
    private LoadMessageView mLoadMessageView;
    private int tempProgress;
    private Boolean tempProgressFlag = false;

    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) {
            // ignored
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        this.removeAllViews();
        try {
            if( sConfigCallback!=null )
                sConfigCallback.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public NewProgressWebView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.load_progress_webview, null);

        mLoadRebateView = (LoadRebateView) view.findViewById(R.id.container_load_rebate);
        mLoadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        addView(view);
        setWebChromeClient(new WebChromeClient(this));
    }

    private static class WebChromeClient extends android.webkit.WebChromeClient {
        private WeakReference<NewProgressWebView> mRef;

        public WebChromeClient(NewProgressWebView webView) {
            mRef = new WeakReference<>(webView);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mRef.get().tempProgress = newProgress;
            if (newProgress >= 100) {
                mRef.get().progressbar.setVisibility(GONE);
            } else {
                if (mRef.get().progressbar.getVisibility() == GONE)
                    mRef.get().progressbar.setVisibility(VISIBLE);
                mRef.get().progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mLoadRebateView.getVisibility() == View.VISIBLE) {
            mLoadRebateView.setVisibility(View.GONE);
        } else {
            mLoadMessageView.setVisibility(View.GONE);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 显示针对有重定向的加载返利商品的等待页面，在oncreate里调用，网页加载完毕自动消失
     */
    public void showLoadRebateViwRef() {
        showLoadingView(mLoadRebateView);
    }

    /**
     * 显示针对有重定向的加载普通商品奶瓶的等待页面，在oncreate里调用，网页加载完毕自动消失
     */
    public void showLoadMessageViewRef() {
        showLoadingView(mLoadMessageView);
    }

    private void showLoadingView(final View loadingView) {
        loadingView.setVisibility(View.VISIBLE);
        if (loadingView instanceof LoadRebateView) {
            mLoadRebateView.startBallAnima(500, DisplayUtil.dip2px(context, 80));//开启球弹跳动画，设置时间及偏移像素
            mLoadRebateView.setOnTvAnimationListener(this);
        } else if (loadingView instanceof LoadMessageView) {
            finishListener(loadingView);
        }
    }

    /**
     * 判断webview什么时候真正加载完成的方案
     *
     * @param loadingView
     */
    public void finishListener(final View loadingView) {
        //判断什么时候真正加载完成的方案
        ThreadPool.getInstance().submit(new DismissAnimationTask(this, loadingView));
    }

    private static class DismissAnimationTask implements ThreadPool.Job<Object> {
        private WeakReference<NewProgressWebView> mRef;
        private WeakReference<View> loadingMsgViewRef;

        public DismissAnimationTask(NewProgressWebView webView, View loadingView) {
            mRef = new WeakReference<>(webView);
            loadingMsgViewRef = new WeakReference<>(loadingView);
        }

        @Override
        public Object run(ThreadPool.JobContext jc) {
            boolean flag = true;
            while (flag) {
                Log.v("进度", mRef.get().tempProgress + "");
                if (mRef.get().tempProgress >= 100) {
                    if (mRef.get().tempProgressFlag == false) {
                        Log.v("进度", "tempProgressFlag  " + mRef.get().tempProgressFlag);
                        mRef.get().tempProgressFlag = true;
                        SystemClock.sleep(1000);
                    } else {
                        flag = false;
                        Log.v("进度", "flag  " + flag);
                        mRef.get().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mRef.get().dismissAnimation(loadingMsgViewRef.get(), 500);
                            }
                        });
                    }
                } else {
                    SystemClock.sleep(800);
                }
            }
            return null;
        }
    }

    /**
     * 关闭加载等待页面的动画
     *
     * @param loadingView
     * @param duration
     */
    private void dismissAnimation(final View loadingView, int duration) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0, 1.0f, 0,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0);
//        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setAnimationListener(this);
        loadingView.startAnimation(animationSet);
    }

    /**
     * 显示针对没有重定向的加载普通商品奶瓶的等待页面，在oncreate里调用，网页加载完毕不会自动消失
     */
    public void showmLoadMessageView() {
        mLoadMessageView.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭针对没有重定向的加载普通商品奶瓶的等待页面，需要在webViewClient的onFinishPage调用
     */
    public void dismissLoadMessageView() {
        dismissAnimation(mLoadMessageView, 500);
    }

    /**
     * 显示针对没有重定向的加载返利商品的等待页面，在oncreate里调用，网页加载完毕不会自动消失
     */
    public void showLoadRebateViw() {
        mLoadRebateView.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭针对没有重定向的加载返利商品的等待页面，需要在webViewClient的onFinishPage调用
     */
    public void dismissLoadRebateViw() {
        mLoadRebateView.setVisibility(View.GONE);
    }

    /**
     * 播放进入动画
     */
    public void startFlyInAnima() {
        mLoadRebateView.startFlyInAnima();
    }

    /**
     * 获得返利加载View对象，便于调用其方法设置内容
     *
     * @return
     */
    public LoadRebateView getLoadRebateView() {
        return mLoadRebateView;
    }

    @Override
    public void finish() {
        finishListener(mLoadRebateView);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // do nothing
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.v("进度", "动画结束");
        mLoadMessageView.setVisibility(View.GONE);
        mLoadRebateView.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}