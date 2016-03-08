package com.seastar.wasai.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.ui.RotateTextView;
import com.seastar.wasai.views.HomeActivity;

import java.util.List;

/**
 * Created by yangteng on 2015/7/24.
 */
public class SuperRebateAdapter extends RecyclerView.Adapter<SuperRebateAdapter.ViewHolder> {
    private Activity activity;
    private List<Item> itemList;
    private DisplayImageOptions imageDisplayOptions;

    public SuperRebateAdapter(Activity activity, List<Item> itemList) {
        this.activity = activity;
        this.itemList = itemList;
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(R.drawable.item_middle_holder).showImageOnLoading(R.drawable.item_middle_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(activity).inflate(R.layout.super_rebate_item, null);
        return new ViewHolder(v);
    }

    //将数据绑定到子View，会自动复用View
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Item product = itemList.get(i);
        if (product.getRemainTime() >= 0) {
            long hour = product.getRemainTime() / (1000 * 60 * 60);
            long minute = (product.getRemainTime() % (1000 * 60 * 60)) / (1000 * 60);
            long second = (product.getRemainTime() % (1000 * 60)) / 1000;
            if (hour < 10) {
                viewHolder.hour1.setText("0");
                viewHolder.hour2.setText(String.valueOf(hour));
            } else {
                viewHolder.hour1.setText(String.valueOf(hour / 10));
                viewHolder.hour2.setText(String.valueOf(hour % 10));
            }

            if (minute < 10) {
                viewHolder.minute1.setText("0");
                viewHolder.minute2.setText(String.valueOf(minute));
            } else {
                viewHolder.minute1.setText(String.valueOf(minute / 10));
                viewHolder.minute2.setText(String.valueOf(minute % 10));
            }

            if (second < 10) {
                viewHolder.second1.setText("0");
                viewHolder.second2.setText(String.valueOf(second));
            } else {
                viewHolder.second1.setText(String.valueOf(second / 10));
                viewHolder.second2.setText(String.valueOf(second % 10));
            }
        }
        if (product.getRemainTime() <= 0) {
            viewHolder.hour1.setText("0");
            viewHolder.hour2.setText("0");
            viewHolder.minute1.setText("0");
            viewHolder.minute2.setText("0");
            viewHolder.second1.setText("0");
            viewHolder.second2.setText("0");
            viewHolder.endView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.endView.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(product.getPic(Image.SIZE_MIDDLE), viewHolder.imageView,
                imageDisplayOptions);
        viewHolder.priceView.setText(product.getNowPrice());
        viewHolder.saleMoneyText.setText(product.getFanli() + "%");
        viewHolder.beforePrice.setText(product.getBeforePrice());
        viewHolder.beforePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        if (product.getSale().equals("-100%") || product.getSale().equals("-0%")) {
            viewHolder.saleFrameLayout.setVisibility(View.GONE);
        } else {
            viewHolder.saleFrameLayout.setVisibility(View.VISIBLE);
            viewHolder.saleTopText.setText(product.getSale());
        }
    }

    //RecyclerView显示数据条数
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //自定义的ViewHolder,减少findViewById调用次数
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView beforePrice;
        public TextView priceView;
        public ImageView imageView;
        public TextView saleMoneyText;
        public RotateTextView saleTopText;
        public FrameLayout saleFrameLayout;

        public TextView hour1;
        public TextView hour2;
        public TextView minute1;
        public TextView minute2;
        public TextView second1;
        public TextView second2;

        public View endView;

        public ViewHolder(View itemView) {
            super(itemView);
            beforePrice = (TextView) itemView.findViewById(R.id.before_price);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            priceView = (TextView) itemView.findViewById(R.id.item_price);
            saleMoneyText = (TextView) itemView.findViewById(R.id.sale_money);
            saleTopText = (RotateTextView) itemView.findViewById(R.id.sale_top_text);
            saleFrameLayout = (FrameLayout) itemView.findViewById(R.id.sale_frame_layout);

            hour1 = (TextView) itemView.findViewById(R.id.hour_1);
            hour2 = (TextView) itemView.findViewById(R.id.hour_2);
            minute1 = (TextView) itemView.findViewById(R.id.minute_1);
            minute2 = (TextView) itemView.findViewById(R.id.minute_2);
            second1 = (TextView) itemView.findViewById(R.id.second_1);
            second2 = (TextView) itemView.findViewById(R.id.second_2);

            endView = itemView.findViewById(R.id.end_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Item product = itemList.get(getPosition());
                    if (product.isSuperItem() && product.getRemainTime() > 0) {
                        CommonUtil.forwardToDetailPage(activity, product);
                    }
                }
            });
        }
    }
}
