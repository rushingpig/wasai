package com.seastar.wasai.views.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.Item;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.Entity.Wish;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.WishListAdapter;
import com.seastar.wasai.views.extendedcomponent.EmptyMessageView;
import com.seastar.wasai.views.extendedcomponent.MyApplication;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView;
import com.seastar.wasai.views.extendedcomponent.SimpleMessageView.ICallBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杨腾
 * @ClassName: UserItemListFragment
 * @Description: 用户收藏的商品
 * @date 2015年4月16日 下午6:09:08
 */
public class UserItemListFragment extends ListFragment {
    private View contextView = null;
    private List<Wish> mListItems = new ArrayList<Wish>();
    private PullToRefreshListView mPullRefreshListView;
    private WishListAdapter mAdapter;
    private long lastId = 0l;
    private SimpleMessageView errorView;
    private EmptyMessageView emptyView;
    private long lastClickTime;


    @Override
    public void onResume() {
        mListItems.clear();
        lastId = 0l;
        if (MyApplication.isLogin()) {
            getWishes(true);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.INVISIBLE);
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.fragment_wish_item_list, null);
        errorView = (SimpleMessageView) contextView.findViewById(R.id.container_error);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getWishes(true);
            }
        });
        emptyView = (EmptyMessageView) contextView.findViewById(R.id.container_empty);
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPullRefreshListView = (PullToRefreshListView) this.getActivity().findViewById(R.id.wish_pull_refresh_list);
        mPullRefreshListView.setMode(Mode.DISABLED);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (MyApplication.isLogin()) {
                    getWishes(false);
                }
            }
        });
        mAdapter = new WishListAdapter(contextView.getContext(), mListItems);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        registerForContextMenu(actualListView);
        actualListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000 && (arg2 - 1) < mListItems.size()) {
                    Wish wish = mListItems.get(arg2 - 1);
                    Item product = new Item();
                    product.setItemId(wish.getItemId());
                    product.setPlatform(wish.getPlatform());
                    product.setOpid(wish.getOpid());
                    CommonUtil.forwardToDetailPage(getActivity(), product);
                }
                lastClickTime = currentTime;
            }
        });
        mPullRefreshListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Wish wish = mListItems.get(position - 1);
                showDialog(wish.getItemId());
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getWishes(final boolean isRefresh) {
        String url = InterfaceConstant.WISH + "/default/" + lastId + "/" + Integer.MAX_VALUE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Wish> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Wish>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        lastId = resultList.get(resultList.size() - 1).getItemId();
                        mListItems.addAll(resultList);
                        emptyView.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "获取商品数据成功：" + dataJsonStr);
                } else {
                    if (!isRefresh && mListItems.size() > 0) {
                        Toast.makeText(contextView.getContext(), ToastMessage.NOT_FOUND_PRODUCT_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mPullRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, isRefresh ? errorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    /**
     * 删除的对话框
     *
     * @param guideId
     */
    private void showDialog(final long guideId) {
        new AlertDialog.Builder(UserItemListFragment.this.getActivity()).setTitle(null).setItems(new String[]{"狠心删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        delWish(guideId);
                        break;
                }
            }
        }).show();
    }

    private void delWish(final long itemId) {
        String url = InterfaceConstant.WISH + "/default/" + itemId;
        int method = Request.Method.DELETE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                int index = -1;
                for (int i = 0; i < mListItems.size(); i++) {
                    if (mListItems.get(i).getItemId() == itemId) {
                        index = i;
                        break;
                    }
                }
                if (index > -1 && index < mListItems.size()) {
                    mListItems.remove(index);
                }
                mAdapter.notifyDataSetChanged();
                Toast.makeText(UserItemListFragment.this.getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(method, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }
}
