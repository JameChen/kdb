package com.nahuo.kdb.activity;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.ReservationBean;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.ReservationAdapter;
import com.nahuo.kdb.api.ApiHelper;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListViewEx;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment implements PullToRefreshListView.OnLoadMoreListener, PullToRefreshListView.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage = -1;
    private PullToRefreshListViewEx mRefreshListView;
    private LoadingDialog mloadingDialog;
    private Activity vThis;
    private List<ReservationBean> itemList = new ArrayList<>();
    private ReservationAdapter adapter;
    private int mPageIndex = 1;
    private int mPageSize = 10;
    private TextView empt_tv;

    public static ReservationFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ReservationFragment pageFragment = new ReservationFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        vThis = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        mloadingDialog = new LoadingDialog(getActivity());
        empt_tv = (TextView) view.findViewById(R.id.tv);
        mRefreshListView = (PullToRefreshListViewEx) view.findViewById(R.id.listview);
        mRefreshListView.setCanLoadMore(true);
        mRefreshListView.setCanRefresh(true);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setEmptyViewText("");
        mRefreshListView.setOnLoadListener(this);
        adapter = new ReservationAdapter(vThis, itemList);
        mRefreshListView.setAdapter(adapter);
        new LoadListDataTask(true).execute();
        return view;
    }

    @Override
    public void onRefresh() {
        bindItemData(true);
    }

    @Override
    public void onLoadMore() {
        bindItemData(false);
    }

    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            mPageIndex = 1;
            LoadListDataTask loadListDataTask = new LoadListDataTask(isRefresh);
            loadListDataTask.execute(false);
        } else {
            mPageIndex++;
            LoadListDataTask loadListDataTask = new LoadListDataTask(isRefresh);
            loadListDataTask.execute(false);
        }

    }

    public class LoadListDataTask extends AsyncTask<Boolean, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadListDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object result) {
            if (getActivity().isFinishing()) {
                return;
            }
            mloadingDialog.stop();
            mRefreshListView.onRefreshComplete();
            if (result instanceof String) {
                ViewHub.showLongToast(vThis, (String) result);

                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }
            } else {
                @SuppressWarnings("unchecked")
                List<ReservationBean> list = (List<ReservationBean>) result;
                if (!ListUtils.isEmpty(list)) {
                    empt_tv.setVisibility(View.GONE);
                    if (mIsRefresh) {
                        itemList.clear();
                        itemList.addAll(list);
                    } else {
                        itemList.addAll(list);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (mIsRefresh) {
                        empt_tv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }


        @Override
        protected Object doInBackground(Boolean... params) {
            try {
                List<ReservationBean> result = KdbAPI.getAppointmentList(vThis, mPage, mPageIndex,
                        mPageSize);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

}
