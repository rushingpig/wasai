package com.seastar.wasai.views.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.AiTaobaoProduct;
import com.seastar.wasai.Entity.AiTaobaoResponse;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.utils.StringUtil;
import com.seastar.wasai.views.adapters.SearchAllProductListAdapter;
import com.seastar.wasai.views.extendedcomponent.LoadMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jamie on 2015/6/9.
 */
public class AllProductListFragment extends ListFragment implements View.OnClickListener, AbsListView.OnScrollListener {
    private PullToRefreshListView mPullToRefreshListView = null;
    private SearchAllProductListAdapter mAdapter = null;
    private List<Item> productList = new ArrayList<>();
    private String currKeyword;
    private long lastClickTime;
    private SimpleMessageView errorView;
    private LoadMessageView loadMessageView;
    private TextView emptyView;
    Activity activity;

    private int pageNo = 1;
    private String orderBy = "";
    private TextView orderHotView;
    private TextView orderSaleCountView;
    private TextView orderPriceView;
    private TextView orderCreditView;
    private ImageView orderPriceIconView;
    private ImageView actionTopView;
    private LinearLayout searchEmptyLL;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_all_product, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        currKeyword = SearchResultActivity.mKeyword;
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
        errorView = (SimpleMessageView) view.findViewById(R.id.container_error);
        loadMessageView = (LoadMessageView) view.findViewById(R.id.container_load);
        emptyView = (TextView) view.findViewById(R.id.empty_textview);
        mAdapter = new SearchAllProductListAdapter(getActivity(), productList);
        mPullToRefreshListView.setAdapter(mAdapter);
        mPullToRefreshListView.setOnScrollListener(this);

        orderHotView = (TextView) view.findViewById(R.id.order_hot);
        orderHotView.setOnClickListener(this);
        orderSaleCountView = (TextView) view.findViewById(R.id.order_sale_count);
        orderSaleCountView.setVisibility(View.GONE);
        orderSaleCountView.setOnClickListener(this);
        orderPriceView = (TextView) view.findViewById(R.id.order_price);
        orderPriceView.setOnClickListener(this);
        orderCreditView = (TextView) view.findViewById(R.id.order_credit);
        orderCreditView.setOnClickListener(this);
        orderPriceIconView = (ImageView) view.findViewById(R.id.order_price_ic);
        actionTopView = (ImageView) view.findViewById(R.id.action_top);
        actionTopView.setOnClickListener(this);

        searchEmptyLL = (LinearLayout) view.findViewById(R.id.search_empty_ll);
    }

    private void setListener() {
        mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getProductList();
            }

        });
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000) {
                    CommonUtil.forwardToDetailPage(AllProductListFragment.this.getActivity(), productList.get(position - 1));
                }
                lastClickTime = currentTime;
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
        getProductList();
    }

    public void refreshProductPager(String mKeyword) {
        this.currKeyword = mKeyword;
        if (StringUtil.isNotEmpty(currKeyword)) {
            loadMessageView.setVisibility(View.VISIBLE);
            pageNo = 1;
            getProductList();
        }
    }

    private void getProductList() {
        String url = InterfaceConstant.INTERFACE_HOST + "/search/atb";
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (pageNo > 10) {
                    loadMessageView.setVisibility(View.INVISIBLE);
                    mPullToRefreshListView.onRefreshComplete();
                    Toast.makeText(AllProductListFragment.this.getActivity(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    AiTaobaoResponse result = gson.fromJson(dataJsonStr, AiTaobaoResponse.class);
                    if (result != null && result.getItems() != null && result.getItems().size() > 0) {
                        if (pageNo == 1) {
                            productList.clear();
                        }
                        mAdapter.setCustomerRate(result.getCustomerRate());
                        for (AiTaobaoProduct product : result.getItems()) {
                            Item item = new Item();
                            item.setItemId(0);
                            item.setItemName(product.getTitle() == null ? "" : product.getTitle());
                            item.setPrice(product.getPromotionPrice() != 0 ? (product.getPromotionPrice() + "") : (product.getPrice() + ""));
                            item.setLocation(product.getItemLocation() == null ? "" : product.getItemLocation());
                            item.setPicUrlSet(product.getPicUrl() == null ? "" : product.getPicUrl());
                            item.setOpid(product.getOpenIid() == null ? "" : product.getOpenIid());
                            item.setPlatform(product.getShopType() == null ? "tb" : (product.getShopType().equals("B") ? "tm" : "tb"));
                            item.setRate(product.getCommissionRate() + "");
                            item.setSaleCount(product.getVolume() + "");
                            productList.add(item);
                        }
                        mAdapter.notifyDataSetChanged();
                        if (pageNo == 1) {
                            mPullToRefreshListView.getRefreshableView().setSelection(0);
                        }
                        searchEmptyLL.setVisibility(View.GONE);
                        pageNo++;
                    } else {
                        if (pageNo == 1) {
                            searchEmptyLL.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(AllProductListFragment.this.getActivity(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (pageNo == 1) {
                        searchEmptyLL.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(AllProductListFragment.this.getActivity(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d(TAG, "商品数据列表加载成功：" + dataJsonStr);
                loadMessageView.setVisibility(View.INVISIBLE);
                mPullToRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(Request.Method.POST, url, loadMessageView, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("keyword", currKeyword);
        paramMap.put("fields", "open_iid,title,nick,pic_url,promotion_price,price,commission,commission_rate,commission_num,commission_volume,seller_credit_score,item_location,volume,shop_type");
        paramMap.put("page_no", pageNo + "");
        paramMap.put("page_size", "40");
        paramMap.put("sort", orderBy);
        requestBody.put("data", new Gson().toJson(paramMap));
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_hot: {
                if (!"".equals(orderBy)) {
                    orderHotView.setTextColor(getResources().getColor(R.color.titlebackground));
                    orderSaleCountView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderPriceView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderCreditView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderPriceIconView.setImageResource(R.drawable.ic_up_down);
                    orderBy = "";
                    pageNo = 1;
                    loadMessageView.setVisibility(View.VISIBLE);
                    getProductList();
                }
                break;
            }
            case R.id.order_sale_count: {
                if (!"commissionNum_desc".equals(orderBy)) {
                    orderHotView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderSaleCountView.setTextColor(getResources().getColor(R.color.titlebackground));
                    orderPriceView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderCreditView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderPriceIconView.setImageResource(R.drawable.ic_up_down);
                    orderBy = "commissionNum_desc";
                    pageNo = 1;
                    loadMessageView.setVisibility(View.VISIBLE);
                    getProductList();
                }
                break;
            }
            case R.id.order_price: {
                orderHotView.setTextColor(getResources().getColor(R.color.text_gray));
                orderSaleCountView.setTextColor(getResources().getColor(R.color.text_gray));
                orderPriceView.setTextColor(getResources().getColor(R.color.titlebackground));
                orderCreditView.setTextColor(getResources().getColor(R.color.text_gray));
                pageNo = 1;
                if ("price_asc".equals(orderBy)) {
                    orderBy = "price_desc";
                    orderPriceIconView.setImageResource(R.drawable.ic_down);
                } else {
                    orderBy = "price_asc";
                    orderPriceIconView.setImageResource(R.drawable.ic_up);
                }
                loadMessageView.setVisibility(View.VISIBLE);
                getProductList();
                break;
            }
            case R.id.order_credit: {
                if (!"credit_desc".equals(orderBy)) {
                    orderHotView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderSaleCountView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderPriceView.setTextColor(getResources().getColor(R.color.text_gray));
                    orderCreditView.setTextColor(getResources().getColor(R.color.titlebackground));
                    orderPriceIconView.setImageResource(R.drawable.ic_up_down);
                    orderBy = "credit_desc";
                    pageNo = 1;
                    loadMessageView.setVisibility(View.VISIBLE);
                    getProductList();
                }
                break;
            }
            case R.id.action_top: {
                mPullToRefreshListView.getRefreshableView().setSelection(0);
                actionTopView.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: {
                if (mPullToRefreshListView.getRefreshableView().getFirstVisiblePosition() == 0) {
                    actionTopView.setVisibility(View.GONE);
                } else {
                    actionTopView.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
