package com.seastar.wasai.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.seastar.wasai.Entity.Activity;
import com.seastar.wasai.Entity.Focus;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.IconMenu;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.TypeConstant;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.PreferencesWrapper;
import com.seastar.wasai.views.Handler.FocusHandler;
import com.seastar.wasai.views.adapters.FocusAdapter;
import com.seastar.wasai.views.adapters.HomeAdapter;
import com.seastar.wasai.views.adapters.HomeMenuAdapter;
import com.seastar.wasai.views.adapters.SuperRebateAdapter;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;
import com.seastar.wasai.views.extendedcomponent.SpacesItemDecoration;
import com.seastar.wasai.views.login.LoginActivity;
import com.seastar.wasai.views.search.SearchMainActivity;
import com.seastar.wasai.views.waitingdlg.WaitingDlg;
import com.seastar.wasai.views.wallet.WalletMainActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
public class HomeActivity extends FragmentActivity {
    private static final String TAG = "HomeActivity";
    private HomeAdapter homeAdapter;
    private List<Guide> guideList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private Long guideLastId = 0l;
    private PullToRefreshListView mPullRefreshListView;
    private SimpleMessageView errorView;
    private ListView actualListView = null;
    private long lastClickTime;
    private DisplayImageOptions imageDisplayOptions;

    private View headerView;
    private FocusHandler focusHandler;
    private ViewPager focusViewPager;
    //放圆点的View的list
    private List<ImageView> dotList = new ArrayList<>();
    private ViewGroup dotsGroup;
    private List<View> focusItemViews = new ArrayList<>();
    private FocusAdapter focusAdapter;

    private List<IconMenu> iconMenus = new ArrayList<>();
    private GridView menuGroup;
    private HomeMenuAdapter homeMenuAdapter;

    private View superRabateLayout;
    private TextView superReabateMoreView;
    private RecyclerView superRebateGridView;
    private SuperRebateAdapter superRebateAdapter;
    private List<Item> superRebateItems = new ArrayList<>();
    private Handler timeHandler = new Handler();
    private WaitingDlg waitingDlg;
    private PreferencesWrapper mPreferencesWrapper;


    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.isLogin()) {
            if (guideList.size() > 0) {
                getFavoriteData(guideList);
            }
        } else {
            for (Guide guide : guideList) {
                guide.setFavoriteId(0);
            }
            homeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initListData
     * @Description: 初始化列表
     */
    private void initListData() {
        getGuideList(true);
        getFocusList();
        getIconMenuList();
        getSuperReabateList();
        actualListView.setSelection(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPreferencesWrapper = new PreferencesWrapper(this);
        imageDisplayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(800)).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        findViewById(R.id.search_edittext).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Intent searchIntent = new Intent(HomeActivity.this, SearchMainActivity.class);
                    mPreferencesWrapper.setIntValueAndCommit("to_search_activity",0);
                    startActivity(searchIntent);
                    lastClickTime = currentTime;
                }
            }
        });
        waitingDlg = new WaitingDlg(this);

        initMessageView();
        initHeaderView();
        initListView();
        initListData();

        timeHandler.postDelayed(superRebateRunnable, 1000);

//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int screenWidth = display.getWidth();
//        int screenHeight = display.getHeight();
//
//        Log.e(TAG,screenWidth + "------------" + screenHeight);
    }

    private void initListView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setMode(Mode.BOTH);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(HomeActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                initListData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(HomeActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getGuideList(false);
            }
        });
        actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.addHeaderView(headerView);
        registerForContextMenu(actualListView);
        homeAdapter = new HomeAdapter(this);
        actualListView.setAdapter(homeAdapter);
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    Guide guide = guideList.get(position - activityList.size() - 2);
                    CommonUtil.forwardToGuideDetail(HomeActivity.this, guide);
                    lastClickTime = currentTime;
                }
            }
        });
    }

    private void initMessageView() {
        errorView = (SimpleMessageView) findViewById(R.id.container_error);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                initListData();
            }
        });
    }

    /**
     * 获取导购列表
     */
    private void getGuideList(final boolean isRefresh) {
        if (isRefresh) {
            guideLastId = 0l;
        }
        String url = InterfaceConstant.GUIDE_LIST + "/0/" + guideLastId + "/" + 2 + "/" + 20;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        if (isRefresh) {
                            guideList.clear();
                        }
                        guideList.addAll(resultList);
                        homeAdapter.setGuideList(guideList);
                        guideLastId = resultList.get(resultList.size() - 1).getGuideId();
                        if (MyApplication.isLogin()) {
                            getFavoriteData(resultList);
                        }
                        homeAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取导购数据成功：" + dataJsonStr);
                } else {
                    if (!isRefresh) {
                        Toast.makeText(HomeActivity.this, ToastMessage.NOT_FOUND_GUIDE_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        guideList.clear();
                        homeAdapter.notifyDataSetChanged();
                    }
                }
                mPullRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, isRefresh ? errorView : null, mPullRefreshListView);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取用户是否喜欢
     *
     * @param guideList
     */
    private void getFavoriteData(final List<Guide> guideList) {
        StringBuffer guideIds = new StringBuffer();
        for (int i = 0; i < guideList.size(); i++) {
            Guide guide = guideList.get(i);
            if (i < guideList.size() - 1) {
                guideIds.append(guide.getGuideId() + "_");
            } else {
                guideIds.append(guide.getGuideId());
            }
        }
        String url = InterfaceConstant.FAVORITE_GUIDE + "/" + guideIds;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Guide> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Guide>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        for (Guide guide : guideList) {
                            for (Guide resultGuide : resultList) {
                                if (guide.getGuideId() == resultGuide.getGuideId()) {
                                    guide.setFavoriteId(resultGuide.getFavoriteId());
                                    guide.setFavoriteCount(resultGuide.getFavoriteCount());
                                }
                            }
                        }
                        homeAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取喜欢数据成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取跑马灯
     */
    private void getFocusList() {
        String url = InterfaceConstant.GET_FOCUS_LIST;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Focus> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Focus>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {

                        focusItemViews.clear();
                        dotList.clear();
                        dotsGroup.removeAllViews();

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 15, 0);
                        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                        for (final Focus focus : resultList) {
                            View itemView = inflater.inflate(R.layout.home_focus_item, null);
                            ImageView itemImageView = (ImageView) itemView.findViewById(R.id.focus_item_image);
                            ImageLoader.getInstance().displayImage(focus.getImgUrl(), itemImageView, imageDisplayOptions);
                            focusItemViews.add(itemImageView);
                            itemImageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    long currentTime = new Date().getTime();
                                    if (currentTime - lastClickTime > 1000) {
                                        if (focus.getIsNeedLogin() != 0 && !MyApplication.isLogin()) {
                                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            CommonUtil.forwardCommon(HomeActivity.this, focus.getType(), focus.getLink(), focus.getTitle());
                                        }
                                        lastClickTime = currentTime;
                                    }
                                }
                            });
                            ImageView dotImage = new ImageView(HomeActivity.this);
                            dotImage.setLayoutParams(lp);
                            dotImage.setImageResource(R.drawable.point_normal);
                            dotList.add(dotImage);
                            dotsGroup.addView(dotImage);
                        }
                        focusAdapter.notifyDataSetChanged();
                        dotList.get(0).setImageResource(R.drawable.point_select);
                        focusViewPager.setCurrentItem(100 * resultList.size(), false);//默认在中间，使用户看不到边界
                    }
                    Log.d(TAG, "获取跑马灯数据成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    /**
     * 获取icon menu
     */
    private void getIconMenuList() {
        String url = InterfaceConstant.GET_ICON_MENU_LIST;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<IconMenu> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<IconMenu>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        iconMenus.clear();
                        IconMenu menuSuperRebate = new IconMenu();
                        menuSuperRebate.setIsNeedLogin(0);
                        menuSuperRebate.setTitle("天天特价");
                        menuSuperRebate.setImgUrl(R.drawable.chaofan + "");
                        menuSuperRebate.setType(9997);
                        iconMenus.add(menuSuperRebate);
                        for (IconMenu menu : resultList) {
                            iconMenus.add(menu);
                        }
                        IconMenu menuWallet = new IconMenu();
                        menuWallet.setIsNeedLogin(1);
                        menuWallet.setTitle("我的钱包");
                        menuWallet.setImgUrl(R.drawable.qianbao + "");
                        menuWallet.setType(9998);
                        iconMenus.add(menuWallet);
                        homeMenuAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取首页icon menu数据成功：" + dataJsonStr);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void initHeaderView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        headerView = inflater.inflate(R.layout.home_header, null);

        initFocusViews();
        initIconMenus();
        initSuperRebateView();
    }

    /**
     * 初始化跑马灯
     */
    private void initFocusViews() {
        focusViewPager = (ViewPager) headerView.findViewById(R.id.focus_view_pager);
        focusHandler = new FocusHandler(new WeakReference<>(this), focusViewPager);
        dotsGroup = (ViewGroup) headerView.findViewById(R.id.dots_group);
        focusAdapter = new FocusAdapter(focusItemViews);
        focusViewPager.setAdapter(focusAdapter);
        focusViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                focusHandler.sendMessage(Message.obtain(focusHandler, FocusHandler.MSG_PAGE_CHANGED, arg0, 0));
                for (ImageView imageView : dotList) {
                    imageView.setImageResource(R.drawable.point_normal);
                }
                dotList.get(arg0 % dotList.size()).setImageResource(R.drawable.point_select);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        focusHandler.sendEmptyMessage(FocusHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        focusHandler.sendEmptyMessageDelayed(FocusHandler.MSG_UPDATE_IMAGE, FocusHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initIconMenus() {
        menuGroup = (GridView) headerView.findViewById(R.id.menu_grid_view);
        homeMenuAdapter = new HomeMenuAdapter(this, iconMenus);
        menuGroup.setAdapter(homeMenuAdapter);
        menuGroup.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IconMenu im = iconMenus.get(position);
                if (im != null) {
                    if (im.getIsNeedLogin() != 0 && !MyApplication.isLogin()) {
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        if (im.getType() == TypeConstant.MY_WALLET) {
                            Intent intent = new Intent(HomeActivity.this, WalletMainActivity.class);
                            startActivity(intent);
                        } else if (im.getType() == TypeConstant.SUPER_REBATE) {
                            getSuperReabateCategory();
                        } else {
                            CommonUtil.forwardCommon(HomeActivity.this, im.getType(), im.getLink(), im.getTitle());
                        }
                    }
                }
            }
        });
    }

    private void initSuperRebateView() {
        superReabateMoreView = (TextView) headerView.findViewById(R.id.super_reabate_more);
        superReabateMoreView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getSuperReabateCategory();
            }
        });
        superRabateLayout = headerView.findViewById(R.id.super_rabate_layout);
        superRebateGridView = (RecyclerView) headerView.findViewById(R.id.super_rebate_grid);
        GridLayoutManager mgr = new GridLayoutManager(this, 2);
        superRebateGridView.setLayoutManager(mgr);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.home_super_rebate_sapce);
        superRebateGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        superRebateAdapter = new SuperRebateAdapter(this, superRebateItems);
        superRebateGridView.setAdapter(superRebateAdapter);
    }

    Runnable superRebateRunnable = new Runnable() {
        @Override
        public void run() {
            for (Item item : superRebateItems) {
                if (item.isSuperItem()) {
                    if (item.getRemainTime() > 0) {
                        item.setRemainTime(item.getRemainTime() - 1000);
                    }
//                    else {
//                        getSuperReabateList();
//                        break;
//                    }
                }
            }
            superRebateAdapter.notifyDataSetChanged();
            timeHandler.postDelayed(this, 1000);
        }
    };


    private void getSuperReabateList() {
        String url = InterfaceConstant.SUPER_ITEM_LIST + "/2";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Item> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Item>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        superRebateItems.clear();
                        superRabateLayout.setVisibility(View.VISIBLE);
                        superRebateItems.addAll(resultList);
                        superRebateAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "获取超级返商品列表成功 ： " + dataJsonStr);
                } else {
                    superRabateLayout.setVisibility(View.GONE);
                }
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }

    private void getSuperReabateCategory() {
        waitingDlg.showWaitingDlg(true);
        String url = InterfaceConstant.SUPER_ITEM_LIST_CAT;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    Map<String, String> resultMap = gson.fromJson(dataJsonStr,
                            new TypeToken<Map<String, String>>() {
                            }.getType());
                    if (resultMap != null && resultMap.keySet().size() > 0) {
                        String categoryId = resultMap.get("category_id");
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setClass(HomeActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", TypeConstant.SUPER_REBATE);
                        bundle.putInt("categoryId", Integer.parseInt(categoryId));
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else {
                        superRabateLayout.setVisibility(View.GONE);
                    }
                }
                waitingDlg.showWaitingDlg(false);
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }
}
