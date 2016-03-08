package com.seastar.wasai.views.extendedcomponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seastar.wasai.R;

/**
 * @PackageName: extendedcomponent
 * @ClassName: LoadRebateView
 * @Author: 黄锦鹏
 * @CreateDate : 2015.7.3 10:04：02
 * @Description: 返利商品的加载页
 * @SvnVersion: $$Rev$$
 * @UpdateAuthor: $$Author$$
 * @UpdateTime: $$Date$$
 * @UpdateDescription:
 */
public class LoadRebateView extends RelativeLayout {

    private BallView mBallView;
    private TextView mLoadRebatePayTv;
    private TextView mLoadRebatePayTvR;
    private TextView mLoadRebateRebateTvR;
    private TextView mLoadRebateRebateTv;
    private TextView mLoadRebateRebateTvM;
    private TextView mLoadRebateRebateTvM1;
    private RelativeLayout mLoadRebateRebate;
    private RelativeLayout mLoadRebatePay;
    private ImageView mLoadRebateLogo;
    private LinearLayout mLoadRebateView;
    private OnTvAnimationListener listener;
    private LinearLayout mLoadRebatePayAndRebate;

    public View priceFrame;

    public LoadRebateView(Context context) {
        super(context);
        initView(context);
    }

    public LoadRebateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.load_rebate_view, this);
        priceFrame = findViewById(R.id.price_frame);
        mLoadRebateView = (LinearLayout) findViewById(R.id.load_rebate_view);
        mLoadRebateLogo = (ImageView) findViewById(R.id.load_rebate_logo);//logo改变
        mLoadRebatePay = (RelativeLayout) findViewById(R.id.load_rebate_pay);
        mLoadRebateRebate = (RelativeLayout) findViewById(R.id.load_rebate_rebate);
        mLoadRebatePayAndRebate = (LinearLayout) findViewById(R.id.load_rebate_pay_and_rebate);
        mLoadRebateRebateTv = (TextView) findViewById(R.id.load_rebate_rebate_tv);
        mLoadRebateRebateTvR = (TextView) findViewById(R.id.load_rebate_rebate_tv_r);//需修改的值
        mLoadRebateRebateTvM = (TextView) findViewById(R.id.load_rebate_rebate_tv_m);
        mLoadRebateRebateTvM1 = (TextView) findViewById(R.id.load_rebate_rebate_tv_r1);
        mLoadRebatePayTv = (TextView) findViewById(R.id.load_rebate_pay_tv);
        mLoadRebatePayTvR = (TextView) findViewById(R.id.load_rebate_pay_tv_r);//需修改的值
        mBallView = (BallView) findViewById(R.id.load_rebate_ball);

        View msgView = findViewById(R.id.login_msg);
        if(MyApplication.isLogin()){
            msgView.setVisibility(View.GONE);
        }else{
            msgView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 播放textView进入动画（监听球的弹跳起动画播放结束后才播放）
     */
    public void startFlyInAnima() {
        final TranslateAnimation tranAnimPay = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        tranAnimPay.setDuration(500);
        tranAnimPay.setFillAfter(true);
        final TranslateAnimation tranAnimRebate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        tranAnimRebate.setDuration(500);
        tranAnimRebate.setFillAfter(true);

        mBallView.setOnUpListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBallView.setVisibility(View.GONE);
                mBallView.stopBallAnima();

                mLoadRebatePay.startAnimation(tranAnimPay);
                mLoadRebatePay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tranAnimPay.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadRebatePayTv.setVisibility(View.VISIBLE);
                mLoadRebatePayTvR.setVisibility(View.VISIBLE);
                mLoadRebateRebate.startAnimation(tranAnimRebate);
                mLoadRebateRebate.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tranAnimRebate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadRebateRebateTv.setVisibility(View.VISIBLE);
                mLoadRebateRebateTvM.setVisibility(View.VISIBLE);
                mLoadRebateRebateTvM1.setVisibility(View.VISIBLE);
                mLoadRebateRebateTvR.setVisibility(View.VISIBLE);
                //动画结束回调
                listener.finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 监听TextView动画播放结束后调用此方法
     * @param listener  复写监听器
     */
    public void setOnTvAnimationListener(OnTvAnimationListener listener) {
        this.listener = listener;
    }


    public interface OnTvAnimationListener {
        void finish();
    }

    /**
     * 播放球的弹跳动画
     * @param duration  一次弹跳时间
     * @param offset    弹跳的高度，像素
     */
    public void startBallAnima(int duration, int offset) {
        if (offset == 0){
            mLoadRebatePayAndRebate.measure(0,0);
            offset = mLoadRebatePayAndRebate.getMeasuredHeight();
        }
        mBallView.startBallAnima(duration, offset);
    }

    /**
     * 设置logo图片
     * @param resId
     */
    public void setLogo(int resId){
        mLoadRebateLogo.setImageResource(resId);
    }

    /**
     * 设置付款金额
     * @param payPrice
     */
    public  void setPayPrice(String payPrice){
        mLoadRebatePayTvR.setText(payPrice);
    }

    /**
     * 设置返利金额
     * @param rebatePrice
     */
    public  void setRebatePrice(String rebatePrice){
        mLoadRebateRebateTvR.setText(rebatePrice);
    }


}
