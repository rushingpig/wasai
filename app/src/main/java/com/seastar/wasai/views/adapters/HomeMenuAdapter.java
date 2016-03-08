package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.seastar.wasai.Entity.IconMenu;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.R;

import java.util.List;

public class HomeMenuAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<IconMenu> menuList;
    private DisplayImageOptions imageDisplayOptions;
    private Context context;

    public HomeMenuAdapter(Context context,List<IconMenu> menuList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(800)).build();
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList != null ? menuList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menuList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.home_menu_view, parent, false);
            holder.titleView = (TextView) convertView.findViewById(R.id.menu_label);
            holder.imageView = (ImageView) convertView.findViewById(R.id.menu_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (menuList != null && menuList.size() > 0) {
            final IconMenu menu = (IconMenu) getItem(position);
            if(menu.getType() == TypeConstant.MY_WALLET || menu.getType() == TypeConstant.SUPER_REBATE){
                holder.imageView.setImageResource(Integer.parseInt(menu.getImgUrl()));
            }else{
                ImageLoader.getInstance().displayImage(menu.getImgUrl(), holder.imageView,
                        imageDisplayOptions);
            }
            holder.titleView.setText(menu.getTitle());
            String[] colors = new String[]{"0","0","0"};
            if(menu.getColor() != null){
                colors = menu.getColor().split(",");
            }
            holder.titleView.setTextColor(Color.rgb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2])));
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView titleView;
        public ImageView imageView;
    }
}
