package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.WishListService;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.ui.RotateTextView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import java.util.List;

/**
 * Created by Jamie on 2015/6/9.
 */
public class SearchProductAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<Item> itemList;
    private DisplayImageOptions imageDisplayOptions;
    private Context context;

    public SearchProductAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(R.drawable.item_middle_holder).showImageOnLoading(R.drawable.item_middle_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
    }

    @Override
    public int getCount() {
        return itemList != null ? itemList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Item product = (Item) getItem(position);
            convertView = mInflater.inflate(R.layout.home_item_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
            holder.imageView.setTag(product.getPic(Image.SIZE_MIDDLE));
            holder.priceView = (TextView) convertView.findViewById(R.id.item_price);
            holder.beforePrice = (TextView) convertView.findViewById(R.id.before_price);
            holder.saleMoneyText = (TextView) convertView.findViewById(R.id.sale_money);
            holder.counterView = (ImageView) convertView.findViewById(R.id.item_favourite);
            holder.saleTopText = (RotateTextView) convertView.findViewById(R.id.sale_top_text);
            holder.saleFrameLayout = (FrameLayout) convertView.findViewById(R.id.sale_frame_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (itemList != null && itemList.size() > 0) {
            final Item product = (Item) getItem(position);
            if(!holder.imageView.getTag().equals(product.getPic(Image.SIZE_MIDDLE))){
                holder.imageView.setImageBitmap(null);
            }
            ImageLoader.getInstance().displayImage(product.getPic(Image.SIZE_MIDDLE), holder.imageView,
                    imageDisplayOptions);
//            holder.titleView.setText(product.getItemName());
            holder.priceView.setText(product.getNowPrice());
            holder.saleMoneyText.setText(product.getFanli() + "%");
            if((product.getNowPrice()).equals((product.getBeforePrice()))){
                holder.beforePrice.setVisibility(View.GONE);
            }else {
                holder.beforePrice.setVisibility(View.VISIBLE);
                holder.beforePrice.setText(product.getBeforePrice());
                holder.beforePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }
            if(product.getSale().equals("-100%") || product.getSale().equals("-0%")){
                holder.saleFrameLayout.setVisibility(View.GONE);
            }else {
                holder.saleFrameLayout.setVisibility(View.VISIBLE);
                holder.saleTopText.setText(product.getSale());
            }
            if(product.getNowPrice().equals(product.getBeforePrice())){
                holder.beforePrice.setVisibility(View.GONE);
            }else {
                holder.beforePrice.setText(product.getBeforePrice());
                holder.beforePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }

//            holder.saleMoneyText.setText("省" + product.getOff());
            if (product.getWishId() > 0) {
                holder.counterView.setImageResource(R.drawable.like_h);
//                holder.saleMoneyText.setText("已收藏");
            } else {
//                holder.saleMoneyText.setText("省" + product.getOff());
                holder.counterView.setImageResource(R.drawable.like_n);
            }
            final ImageView counterTextView = holder.counterView;
            holder.counterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.isLogin()) {
                        if (CommonUtil.checkNetWork()) {
                            if (product.getWishId() <= 0) {
                                counterTextView.setImageResource(R.drawable.like_h);
//                                holder.saleMoneyText.setText("已收藏");
//                                counterTextView.setTextViewText((product.getFavoriteCount() + 1) + "");
                                new SubmitFavoriteTask(product, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                if (product.getFavoriteCount() > 0) {
//                                    holder.saleMoneyText.setText("省" + product.getOff());
                                    counterTextView.setImageResource(R.drawable.like_n);
//                                    counterTextView.setTextViewText((product.getFavoriteCount() - 1) + "");
                                    new SubmitFavoriteTask(product, 1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
                        } else {
                            GeneralUtil.showToastShort(context, ToastMessage.NET_WORK_NOT_WORK);
                        }
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
//			convertView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					CommonUtil.forwardToDetailPage(context, product);
//				}
//
//			});
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView priceView;
        public ImageView imageView;
        public ImageView counterView;
        private TextView saleMoneyText;
        private TextView beforePrice;
        private RotateTextView saleTopText;
        private FrameLayout saleFrameLayout;
    }

    private class SubmitFavoriteTask extends AsyncTask<Object, Object, Integer> {
        private Item product;
        private int actionType;

        private WishListService wishListService = new WishListService();

        public SubmitFavoriteTask(Item product, int actionType) {
            this.product = product;
            this.actionType = actionType;
        }

        @Override
        protected Integer doInBackground(Object... params) {
            int favoriteCount = product.getFavoriteCount();
            if (actionType == 0) {
                favoriteCount = wishListService.addWish("default", product.getItemId());
            } else {
                favoriteCount = wishListService.deleteWish("default", product.getItemId());
            }
            return favoriteCount;
        }

        @Override
        protected void onPostExecute(Integer favoriteCount) {
            if(favoriteCount != -1){
                product.setFavoriteCount(favoriteCount);
                if (actionType == 0) {
                    product.setWishId(1);
                } else {
                    product.setWishId(0);
                }
            }else{
                Toast.makeText(context, ToastMessage.OPERATION_FAILED, Toast.LENGTH_SHORT).show();
            }
            SearchProductAdapter.this.notifyDataSetChanged();
        }
    }
}
