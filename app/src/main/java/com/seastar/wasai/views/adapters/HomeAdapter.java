package com.seastar.wasai.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.seastar.wasai.Entity.Activity;
import com.seastar.wasai.Entity.FocusPosition;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.Image;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.extendedcomponent.DrawView;
import com.seastar.wasai.views.extendedcomponent.GuideItemFavouriteCounterView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.login.LoginActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杨腾
 * @ClassName: GuideListAdapter
 * @Description: 导购列表适配器
 * @date 2015年4月17日 下午6:22:00
 */
public class HomeAdapter extends BaseAdapter {
    public static final int ITEM_ACTIVITY = 0;
    public static final int ITEM_GUIDE = 1;
    private LayoutInflater mInflater;
    private DisplayImageOptions imageDisplayOptions;
    private DisplayImageOptions logoImageDisplayOptions;
    private List<Guide> guideList;
    private List<Activity> activityList;
    private Context context;
    private WindowManager wm;
    private int screenWidth;

    public HomeAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).showImageForEmptyUri(R.drawable.guide_big_holder).showImageOnLoading(R.drawable.guide_big_holder).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        logoImageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
    }

    public void setGuideList(List<Guide> guideList) {
        this.guideList = guideList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @Override
    public int getCount() {
        int activitySize = activityList != null ? activityList.size() : 0;
        int guideSize = guideList != null ? guideList.size() : 0;
        return activitySize + guideSize;
    }

    @Override
    public Object getItem(int position) {
        int activitySize = activityList != null ? activityList.size() : 0;
        int guideSize = guideList != null ? guideList.size() : 0;

        if (position <= activitySize - 1) {
            return activityList.get(position);
        } else if (position > (activitySize - 1) && position <= (activitySize + guideSize - 1)) {
            return guideList.get(position - activitySize);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        int activitySize = activityList != null ? activityList.size() : 0;
        int guideSize = guideList != null ? guideList.size() : 0;

        if (position <= activitySize - 1) {
            return activityList.get(position).getActivityId();
        } else if (position > (activitySize - 1) && position <= (activitySize + guideSize - 1)) {
            return guideList.get(position - activitySize).getGuideId();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int activitySize = activityList != null ? activityList.size() : 0;
        int guideSize = guideList != null ? guideList.size() : 0;
        int type = -1;
        if (position <= activitySize - 1) {
            type = ITEM_ACTIVITY;
        } else if (position > (activitySize - 1) && position <= (activitySize + guideSize - 1)) {
            type = ITEM_GUIDE;
        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ActivityViewHolder activityViewHolder = null;
        GuideViewHolder guideViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case ITEM_ACTIVITY:
                    activityViewHolder = new ActivityViewHolder();
                    convertView = mInflater.inflate(R.layout.home_activity_item, parent, false);
                    activityViewHolder.imageView = (ImageView) convertView.findViewById(R.id.activity_item_image);
                    convertView.setTag(activityViewHolder);
                    break;
                case ITEM_GUIDE:
                    guideViewHolder = new GuideViewHolder();
                    Guide guide = (Guide) getItem(position);
                    convertView = mInflater.inflate(R.layout.home_guide_item, parent, false);
                    guideViewHolder.titleView = (TextView) convertView.findViewById(R.id.guide_item_title);
                    guideViewHolder.titleImageView = (ImageView) convertView.findViewById(R.id.guide_item_title_img);
                    guideViewHolder.imageView = (ImageView) convertView.findViewById(R.id.guide_item_image);
                    guideViewHolder.imageView.setTag(guide.getPic(Image.SIZE_LARGE));
                    guideViewHolder.counterView = (GuideItemFavouriteCounterView) convertView
                            .findViewById(R.id.guide_favourite);
                    guideViewHolder.focusImageLayout = (FrameLayout) convertView.findViewById(R.id.guide_item_focus_layout);
                    convertView.setTag(guideViewHolder);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case ITEM_ACTIVITY:
                    activityViewHolder = (ActivityViewHolder) convertView.getTag();
                    break;
                case ITEM_GUIDE:
                    guideViewHolder = (GuideViewHolder) convertView.getTag();
                    break;
                default:
                    break;
            }
        }

        switch (type) {
            case ITEM_ACTIVITY:
                putActivityList(position, activityViewHolder);
                break;
            case ITEM_GUIDE:
                putGuideList(position, guideViewHolder);
                break;
            default:
                break;
        }

        return convertView;
    }

    private void putActivityList(int position, ActivityViewHolder activityViewHolder) {
        if (activityList != null && activityList.size() > 0) {
            final Activity activity = (Activity) getItem(position);
            ImageLoader.getInstance().displayImage(activity.getPic(Image.SIZE_LARGE), activityViewHolder.imageView,
                    imageDisplayOptions);
        }
    }

    private void putGuideList(int position, final GuideViewHolder guideViewHolder) {
        if (guideList != null && guideList.size() > 0) {
            final Guide guide = (Guide) getItem(position);
            guideViewHolder.focusImageLayout.removeAllViews();
            ImageLoader.getInstance().displayImage(guide.getPic(Image.SIZE_LARGE), guideViewHolder.imageView, imageDisplayOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (guide.getPosition() != null && guide.getPosition().size() > 0) {
                        drawFocusImage(guideViewHolder, guide,loadedImage);
                    }
                }
            });

            guideViewHolder.titleView.setText(guide.getTitle());
            ImageLoader.getInstance().displayImage(guide.getPic(Image.SIZE_LOGO), guideViewHolder.titleImageView,
                    logoImageDisplayOptions);
            if (guide.getFavoriteId() > 0) {
                guideViewHolder.counterView.setImageResource(R.drawable.like_sel);
            } else {
                guideViewHolder.counterView.setImageResource(R.drawable.like_nor);
            }
            guideViewHolder.counterView.setTextViewText(String.valueOf(guide.getFavoriteCount()));
            final GuideItemFavouriteCounterView counterView = guideViewHolder.counterView;
            guideViewHolder.counterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.isLogin()) {
                        if (CommonUtil.checkNetWork()) {
                            if (guide.getFavoriteId() > 0) {
                                counterView.setImageResource(R.drawable.like_nor);
                                counterView.setTextViewText(guide.getFavoriteCount() > 0?(guide.getFavoriteCount() - 1 + ""):"0");
                                postFavorite(guide, 0);
                            } else {
                                counterView.setImageResource(R.drawable.like_sel);
                                counterView.setTextViewText(guide.getFavoriteCount() + 1 + "");
                                postFavorite(guide, 1);
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
        }
    }

    /**
     * @param @param guideViewHolder
     * @param @param guide
     * @return void
     * @throws
     * @Title: drawFocusImage
     * @Description: 画卖点
     */
    private void drawFocusImage(GuideViewHolder guideViewHolder, final Guide guide,final Bitmap loadedImage) {
        if(loadedImage != null){
            int width = guideViewHolder.imageView.getWidth();
            int height = guideViewHolder.imageView.getHeight();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.focus_1);
            for (FocusPosition focusPosition : guide.getPosition()) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);

                if (StringUtil.isNotEmpty(focusPosition.getCenter())) {
                    String[] centerPosition = focusPosition.getCenter().split(",");
                    if (centerPosition.length == 2) {
                        int left = Integer.parseInt(centerPosition[0].trim());
                        int top = Integer.parseInt(centerPosition[1].trim());
                        int right = 0;
                        int bottom = 0;

                        int imageWidth = 750;
                        int imageHeight = 750;

                        int actualLeft = width * left / imageWidth;
                        int actualTop = height * top / imageHeight;

                        int actualTopMargin = actualTop - bitmap.getWidth() / 2;
                        int actualLeftMargin = actualLeft - bitmap.getHeight() / 2;

                        lp.setMargins(actualLeftMargin, actualTopMargin, right, bottom);
                        ImageView focusImage = new ImageView(context);
                        focusImage.setLayoutParams(lp);
                        AnimationDrawable ad = (AnimationDrawable) context.getResources().getDrawable(
                                R.drawable.focus_anim);
                        focusImage.setImageDrawable(ad);

                        final DrawView view = new DrawView(context, width, height, focusPosition);
                        view.invalidate();

                        guideViewHolder.focusImageLayout.addView(view);
                        guideViewHolder.focusImageLayout.addView(focusImage);
                        ad.start();
                    }
                }
            }
        }
    }

    public final class ActivityViewHolder {
        public ImageView imageView;
    }

    public final class GuideViewHolder {
        public TextView titleView;
        public ImageView titleImageView;
        public ImageView imageView;
        public GuideItemFavouriteCounterView counterView;
        public FrameLayout focusImageLayout;
    }

    private void postFavorite(final Guide guide, final int actionType) {
        String url = InterfaceConstant.FAVORITE;
        int method = Request.Method.POST;
        if (actionType == 0) {
            url = InterfaceConstant.FAVORITE + "/" + guide.getGuideId();
            method = Request.Method.DELETE;
        }
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                Guide tempGuide = gson.fromJson(dataJsonStr, Guide.class);
                guide.setFavoriteId(actionType == 0 ? 0l : 1l);
                guide.setFavoriteCount(tempGuide.getFavoriteCount());
                HomeAdapter.this.notifyDataSetChanged();
                Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(method, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("guide_id", guide.getGuideId() + "");
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }
}
