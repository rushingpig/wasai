package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.seastar.wasai.R;

/**
 * @PackageName: com.seastar.wasai.views.extendedcomponent
 * @ClassName: BallView
 * @Author: jakey.huang(黄锦鹏)
 * @CreateDate : 2015/7/6 10:48
 * @Description: TODO
 * @SvnVersion: $$Rev: 3363 $$
 * @UpdateAuthor: $$Author: jakey.huang@sz-seastar.com $$
 * @UpdateTime: $$Date: 2015-07-10 10:58:15 +0800 (Fri, 10 Jul 2015) $$
 * @UpdateDescription:
 */
public class BallView extends RelativeLayout{
    private Animation mAniDown;
    private Animation mAniUp;
    private Animation mAniShadowDown;
    private Animation mAniShadowUp;
    private SwitchAnimationListener mSwitchListener;


    private ImageView mView;//准备显示跳跃动画的View(一般是ImageView)
    //动画阴影(一般是半透明阴影图像的ImageView)，因为需要将阴影放于动画后面，因此View的上级Layout类型应选用允许相互覆盖的如RelativeLayout
    private ImageView mShadow;


    public BallView(Context context) {
        super(context);
        initView(context);
    }

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.load_ball_view, this);
        mView = (ImageView) findViewById(R.id.imageViewItem);
        mShadow = (ImageView) findViewById(R.id.imageViewShadow);

    }

    /**
     * 跳跃动画的构造函数
     * @param duration 每次跳跃和落下的动画持续时间
     * @param offset 动画跳起的高度(像素)
     */
    public void startBallAnima(int duration, int offset){
        jumper(duration, offset);
        attachToView();
    }
    public void stopBallAnima(){
        disattachToView();
    }



    /**
     * 跳跃动画的构造函数
     * @param duration 每次跳跃和落下的动画持续时间
     * @param offset 动画跳起的高度(像素)
     */
    private void jumper(int duration, int offset) {
        mAniDown = new TranslateAnimation(0, 0, -offset, 0);
        mAniUp   = new TranslateAnimation(0, 0, 0, -offset);
        mAniDown.setInterpolator(new AccelerateInterpolator());
        mAniUp.setInterpolator(new DecelerateInterpolator());
        mAniDown.setDuration(duration);
        mAniUp.setDuration(duration);

        mAniShadowUp = new ScaleAnimation(1.0f, 0.7f, 1.0f, 0.7f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mAniShadowDown   = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mAniShadowUp.setDuration(duration);
        mAniShadowDown.setDuration(duration);

        mSwitchListener = new SwitchAnimationListener();

        mAniDown.setAnimationListener(mSwitchListener);
        mAniUp.setAnimationListener(mSwitchListener);
        mAniShadowDown.setAnimationListener(mSwitchListener);
        mAniShadowUp.setAnimationListener(mSwitchListener);
    }

    /**
     * 将跳跃动画附到一个View
     */
    private void attachToView() {

        if (mView != null)
            mView.startAnimation(mAniDown);

        if (mShadow != null)
            mShadow.startAnimation(mAniShadowDown);
    }
    /**
     * 清除动画
     */
    private void disattachToView() {

        if (mView != null)
            mView.clearAnimation();

        if (mShadow != null)
            mShadow.clearAnimation();
    }

    public void setOnUpListener(Animation.AnimationListener listener){
        mAniUp.setAnimationListener(listener);
    }

    /** 动画切换侦听器 */
    private class SwitchAnimationListener implements Animation.AnimationListener {

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (mView == null)
                return;

            if (animation == mAniUp) {
                if (mView!=null) mView.startAnimation(mAniDown);
            } else if (animation == mAniDown) {
                if (mView!=null) mView.startAnimation(mAniUp);
            } else if (animation == mAniShadowUp) {
                if (mShadow!=null) mShadow.startAnimation(mAniShadowDown);
            } else if (animation == mAniShadowDown) {
                if (mShadow!=null) mShadow.startAnimation(mAniShadowUp);
            }
        }

        public void onAnimationRepeat(Animation animation) {

        }
    }
}
