package com.seastar.wasai.views.base;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

/**
 * 所有activity都继承此类
 *
 * @author Jamie
 */
public abstract class BaseActivity extends FragmentActivity {

    protected SlideFrame frameView;
    protected View contentView;

    @Override
    public void setContentView(int layoutResID) {
        if (frameView == null) {
            frameView = new SlideFrame(this);
        } else {
            frameView.removeAllViews();
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(layoutResID, null);
        frameView.addView(contentView, params);
        super.setContentView(frameView);
    }

    @Override
    public void setContentView(View view) {
        if (frameView == null) {
            frameView = new SlideFrame(this);
        } else {
            frameView.removeAllViews();
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1);
        contentView = view;
        frameView.addView(contentView, params);
        super.setContentView(frameView);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (frameView == null) {
            frameView = new SlideFrame(this);
        } else {
            frameView.removeAllViews();
        }
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(-1, -1);
        contentView = view;
        frameView.addView(contentView, fp);
        super.setContentView(frameView, params);
    }

    protected void onSlideFinish() {
        finishActivity();
        finish();
    }

    public abstract void finishActivity();

    public void slideTo(int position) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            contentView.setX(position);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView
                    .getLayoutParams();
            params.setMargins(position, 0, -position, 0);
            contentView.setLayoutParams(params);
        }
    }

    public int getSlide() {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            return (int) contentView.getX();
        } else {
            return ((FrameLayout.LayoutParams) contentView
                    .getLayoutParams()).leftMargin;
        }
    }

    public class SlideFrame extends FrameLayout {
        private final static int DEFAULT_SLIDE_DUMPING = 8;
        private final static int DEFAULT_DO_DUMPING = 100;
        private int startX, currentX, startY, currentY;
        private float downX;
        private boolean doNotIntercept, hasChecked;
        private int slideDumping;
        private int doDumping;
        protected SlideAnimation slideAnimation;

        private final int scaledTouchSlop = 30;

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            super.onInterceptTouchEvent(ev);
            if ((!doNotIntercept) && hasChecked) {
                return true;
            }
            return false;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startX = (int) ev.getX();
                downX = ev.getRawX();
                startY = (int) ev.getY();
                doNotIntercept = false;
                hasChecked = false;
            } else if (!doNotIntercept) {
                currentX = (int) ev.getX();
                currentY = (int) ev.getY();
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (hasChecked) {
                            doSlide();
                        } else {
                            doCheck();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        doNotIntercept = false;
                        hasChecked = false;
                        if (Math.abs(currentX - startX) > doDumping && downX < dip2px(scaledTouchSlop) && getChildCount() == 1) {
                            if (currentX > startX) {
                                slideAnimation = new SlideAnimation(getSlide(),
                                        contentView.getWidth(), 0);
                                slideAnimation
                                        .setAnimationListener(new AnimationListener() {
                                            @Override
                                            public void onAnimationStart(
                                                    Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationRepeat(
                                                    Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(
                                                    Animation animation) {
                                                onSlideFinish();
                                            }
                                        });
                                startAnimation(slideAnimation);
                            }
                        } else {
                            slideAnimation = new SlideAnimation(getSlide(), 0, 0);
                            startAnimation(slideAnimation);
                        }
                        break;
                    default:
                        break;
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        private void doCheck() {
            if (Math.abs(startY - currentY) > slideDumping) {
                hasChecked = true;
                doNotIntercept = true;
                slideTo(0);
//			} else if (currentX - startX > slideDumping) {
            } else if (currentX - startX > slideDumping && downX < dip2px(scaledTouchSlop) && getChildCount() == 1) {

                hasChecked = true;
                doNotIntercept = false;
            }
        }

        private void doSlide() {
            if (currentX > startX && downX < dip2px(scaledTouchSlop) && getChildCount() == 1) {
                slideTo(currentX - startX);
            } else {
                slideTo(0);
            }
        }

        public void setSlideDumping(int dpValue) {
            slideDumping = dip2px(dpValue);
        }

        public void setDoDumping(int dpValue) {
            doDumping = dip2px(dpValue);
        }

        private void initilize() {
            setSlideDumping(DEFAULT_SLIDE_DUMPING);
            setDoDumping(DEFAULT_DO_DUMPING);
            doNotIntercept = false;
            hasChecked = false;
            setClickable(true);
            setFocusable(true);
            setFocusableInTouchMode(true);
        }

        public SlideFrame(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initilize();
        }

        public SlideFrame(Context context, AttributeSet attrs) {
            super(context, attrs);
            initilize();
        }

        public SlideFrame(Context context) {
            super(context);
            initilize();
        }

        private int dip2px(float dipValue) {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }
    }

    public class SlideAnimation extends Animation {
        private float from, to;

        public SlideAnimation(int from, int to, int startOffset) {
            this.from = from;
            this.to = to;
            setFillEnabled(false);
            setDuration(200);
            setRepeatCount(0);
            setStartOffset(startOffset);
            setInterpolator(new DecelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            float current = from + (to - from) * interpolatedTime;
            slideTo((int) current);
            super.applyTransformation(interpolatedTime, t);
        }
    }


}
