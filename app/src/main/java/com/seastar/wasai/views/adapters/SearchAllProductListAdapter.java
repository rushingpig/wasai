package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.service.FavoriteService;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.views.extendedcomponent.GuideActionCompactCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import org.w3c.dom.Text;

import java.util.List;

public class SearchAllProductListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Item> productList;
    private DisplayImageOptions imageDisplayOptions;
    private Context context;
    private double customerRate;

    public SearchAllProductListAdapter(Context context, List<Item> productList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.productList = productList;
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
    }

    public void setCustomerRate(double rate) {
        this.customerRate = rate;
    }

    @Override
    public int getCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            Item product = (Item) getItem(position);
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_search_tb_product, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.product_img);
            holder.titleView = (TextView) convertView.findViewById(R.id.product_title);
            holder.imageView.setTag(product.getPic(Image.SIZE_MIDDLE));
            holder.priceView = (TextView) convertView.findViewById(R.id.product_price);
            holder.rebateView = (TextView) convertView.findViewById(R.id.product_rebate);
            holder.platformImageView = (ImageView) convertView.findViewById(R.id.product_platform_img);
            holder.locationView = (TextView) convertView.findViewById(R.id.product_location);
            holder.saleCountView = (TextView) convertView.findViewById(R.id.product_sale_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        putProductList(position, convertView, holder);
        return convertView;
    }

    private void putProductList(int position, View convertView, ViewHolder holder) {
        if (productList != null && productList.size() > 0) {
            final Item product = (Item) getItem(position);
            if (!holder.imageView.getTag().equals(product.getPic(Image.SIZE_MIDDLE))) {
                holder.imageView.setImageBitmap(null);
            }
            ImageLoader.getInstance().displayImage(product.getPicUrlSet(), holder.imageView,
                    imageDisplayOptions);
            holder.titleView.setText(product.getItemName());
            holder.priceView.setText(product.getPrice());
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            String actrualRate = df.format(Double.parseDouble(product.getRate()) * customerRate / 100);

            holder.rebateView.setText("返" + actrualRate + "%");
            holder.locationView.setText(product.getLocation());
            holder.saleCountView.setText("月销：" + product.getSaleCount() + " 笔");

            if ("tm".equals(product.getPlatform())) {
                holder.platformImageView.setVisibility(View.VISIBLE);
            } else {
                holder.platformImageView.setVisibility(View.GONE);
            }
        }
    }

    public final class ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView priceView;
        public TextView rebateView;
        public ImageView platformImageView;
        public TextView locationView;
        public TextView saleCountView;
    }
}
