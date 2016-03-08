package com.seastar.wasai.views.adapters;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.IntegralListEntity;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.signin.MyIntegral;

import java.util.List;

/**
 * Created by Jamie on 2015/6/19.
 */
public class MyIntegralAdapter extends BaseAdapter{
    private List<IntegralListEntity> mList;
    private MyIntegral mContext;
    private DisplayImageOptions logoImageDisplayOptions;
    private PreferencesWrapper mPreferencesWrapper;

    public MyIntegralAdapter(MyIntegral context){
        this.mContext = context;
        mPreferencesWrapper = new PreferencesWrapper(context);
        logoImageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(false).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setData(List<IntegralListEntity> list){
        this.mList = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_integral,null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.explain = (TextView) convertView.findViewById(R.id.explain);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            holder.beforePrice = (TextView) convertView.findViewById(R.id.before_price);
            holder.integral = (TextView) convertView.findViewById(R.id.integral);
            holder.exchange = (TextView) convertView.findViewById(R.id.exchange);
//            holder.bottomLayout = (FrameLayout) convertView.findViewById(R.id.bottom_framelayout);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        final IntegralListEntity body = mList.get(position);
        holder.title.setText(body.name);
        holder.explain.setText(body.desc);
        holder.beforePrice.setText(String.valueOf(body.price));
        holder.beforePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        int mScores = body.scores;
        holder.integral.setText(mScores + "");
        ImageLoader.getInstance().displayImage(body.getPic(Image.SIZE_BIG), holder.imageView,
                logoImageDisplayOptions);
//        holder.exchange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView tv = (TextView) v.getId().findViewById(R.id.integral);
//                String str = String.valueOf(tv.getText());
//                int mSc = Integer.valueOf(str);
//                int text = mPreferencesWrapper.getIntValue(MyIntegral.TOTALPOINTS,0);
//                if(mSc > text){
//                    mContext.showPopupwindow(String.valueOf(mSc-text));
//                }
//            }
//        });
        holder.exchange.setOnClickListener(new LvButtonListener(position));
        return convertView;
    }
    private class ViewHolder{
        private TextView title;
        private TextView explain;
        private ImageView imageView;
        private TextView beforePrice;
        private TextView integral;
        private TextView exchange;
        private FrameLayout bottomLayout;
    }

    private class LvButtonListener implements View.OnClickListener {
        private int position;

        LvButtonListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int text = mPreferencesWrapper.getIntValue(MyIntegral.TOTALPOINTS, 0);
            Log.e("info", "text= " + (mList.get(position).scores - text));
            int points = mList.get(position).scores;
            if (points > text) {
                mContext.showPopupwindow(String.valueOf(mList.get(position).scores - text));
            }else {
                mContext.showPopupwindowTemp(mList.get(position).prize_id,points);
            }

        }
    }
}
