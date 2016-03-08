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
import com.seastar.wasai.Entity.Favorite;
import com.seastar.wasai.Entity.Guide;
import com.seastar.wasai.Entity.InterfaceConstant;
import com.seastar.wasai.Entity.MyGsonRequest;
import com.seastar.wasai.Entity.ToastMessage;
import com.seastar.wasai.R;
import com.seastar.wasai.utils.CommonUtil;
import com.seastar.wasai.utils.MyReqSucessListener;
import com.seastar.wasai.views.adapters.FavoriteGuideListAdapter;
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
 * @ClassName: UserGuideListFragment
 * @Description: 用户收藏的导购
 * @date 2015年4月16日 下午6:08:52
 */
public class UserGuideListFragment extends ListFragment {

    private View contextView = null;
    private FavoriteGuideListAdapter guideListAdapter;
    private List<Favorite> guideList = new ArrayList<Favorite>();

    private Long lastId = 0l;

    private PullToRefreshListView mPullRefreshListView;
    private SimpleMessageView errorView;
    private EmptyMessageView emptyView;
    private long lastClickTime;

    @Override
    public void onResume() {
        guideList.clear();
        lastId = 0l;
        if (MyApplication.isLogin()) {
            getGuideList(true);
        } else {
            errorView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            guideListAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contextView = inflater.inflate(R.layout.fragment_favorite_guide_list, null);
        errorView = (SimpleMessageView) contextView.findViewById(R.id.container_error);
        errorView.setOnClick(new ICallBack() {
            @Override
            public void onClick() {
                errorView.setVisibility(View.INVISIBLE);
                getGuideList(true);
            }
        });
        emptyView = (EmptyMessageView) contextView.findViewById(R.id.container_empty);
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        guideListAdapter = new FavoriteGuideListAdapter(contextView.getContext());
        mPullRefreshListView = (PullToRefreshListView) contextView.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setMode(Mode.DISABLED);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (MyApplication.isLogin()) {
                    getGuideList(false);
                }

            }
        });
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        registerForContextMenu(actualListView);
        actualListView.setAdapter(guideListAdapter);
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                long currentTime = new Date().getTime();
                if (currentTime - lastClickTime > 1000 && (arg2 - 1) < guideList.size()) {
                    final Favorite favorite = guideList.get(arg2 - 1);
                    Guide guide = new Guide();
                    guide.setGuideId(favorite.getGuideId());
                    guide.setType(favorite.getType());
                    CommonUtil.forwardToGuideDetail(UserGuideListFragment.this.getActivity(), guide);
                }
                lastClickTime = currentTime;
            }
        });

        mPullRefreshListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Favorite favorite = guideList.get(position - 1);
                showDialog(favorite.getGuideId());
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获取导购列表
     */
    private void getGuideList(final boolean isRefresh) {
        String url = InterfaceConstant.FAVORITE + "/" + lastId + "/" + Integer.MAX_VALUE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                if (dataJsonStr != null) {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                    List<Favorite> resultList = gson.fromJson(dataJsonStr,
                            new TypeToken<List<Favorite>>() {
                            }.getType());
                    if (resultList != null && resultList.size() > 0) {
                        guideList.addAll(resultList);
                        guideListAdapter.setmData(guideList);
                        lastId = resultList.get(resultList.size() - 1).getGuideId();
                        emptyView.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "获取导购数据成功：" + dataJsonStr);
                } else {
                    if (!isRefresh && guideList.size() > 0) {
                        Toast.makeText(contextView.getContext(), ToastMessage.NOT_FOUND_GUIDE_LIST, Toast.LENGTH_SHORT).show();
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                guideListAdapter.notifyDataSetChanged();
                mPullRefreshListView.onRefreshComplete();
            }
        };
        MyGsonRequest request = new MyGsonRequest(url, null, isRefresh ? errorView : null);
        MyApplication.addToRequestQueue(request.getRequest(sucessListener));
    }


    private void delFavorite(final long guideId) {
        String url = InterfaceConstant.FAVORITE + "/" + guideId;
        int method = Request.Method.DELETE;
        Response.Listener<JSONObject> sucessListener = new MyReqSucessListener() {
            @Override
            public void doResponse(String dataJsonStr) {
                int index = -1;
                for (int i = 0; i < guideList.size(); i++) {
                    if (guideList.get(i).getGuideId() == guideId) {
                        index = i;
                        break;
                    }
                }
                if (index > -1 && index < guideList.size()) {
                    guideList.remove(index);
                }
                guideListAdapter.notifyDataSetChanged();
                Toast.makeText(UserGuideListFragment.this.getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "提交用户喜欢成功：" + dataJsonStr);
            }
        };
        MyGsonRequest request = new MyGsonRequest(method, url, null, null);
        Map<String, String> requestBody = new HashMap<String, String>();
        MyApplication.addToRequestQueue(request.getRequestWithBody(sucessListener, requestBody));
    }

    /**
     * 删除的对话框
     *
     * @param guideId
     */
    private void showDialog(final long guideId) {
        new AlertDialog.Builder(UserGuideListFragment.this.getActivity()).setTitle(null).setItems(new String[]{"狠心删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        delFavorite(guideId);
                        break;
                }
            }
        }).show();
    }
}
