package com.nahuo.kdb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.InventoryAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.InventoryBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = InventoryActivity.class.getSimpleName();
    private InventoryActivity vThis = this;
    private static final String ERROR_PREFIX = "error:";
    private LoadingDialog mloadingDialog;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private InventoryAdapter adpater;
    private int mPageIndex = 1;
    private int mPageSize = 20;
    LoadDataTask loadDataTask;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataTask != null)
            loadDataTask.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        BWApplication.addActivity(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvTitleCenter)).setText("盘点列表");
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.sw);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.bg_red), getResources().getColor(R.color.red));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadRefresh();
            }
        });
        swipeLayout.setRefreshing(true);
        adpater = new InventoryAdapter(this);
        recyclerView.setAdapter(adpater);
        adpater.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<InventoryBean.ListBean> data = adapter.getData();
                InventoryBean.ListBean bean = data.get(position);
                if (bean != null) {
                    startActivity(new Intent(vThis, InventoryDetailActivity.class)
                            .putExtra(InventoryDetailActivity.Extra_Time, bean.getStockTime()));
                }
            }
        });
        adpater.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMore();
            }
        }, recyclerView);
        View empty = LayoutInflater.from(this).inflate(R.layout.textview_empty, null);
        TextView tv_empty = (TextView) empty.findViewById(R.id.tv_empty);
        tv_empty.setText("沒有盘点数据");
        adpater.setEmptyView(empty);
        ImageView iv_right_01 = (ImageView) findViewById(R.id.iv_right_01);
        iv_right_01.setImageResource(R.drawable.sweep_code);
        iv_right_01.setVisibility(View.GONE);
        iv_right_01.setOnClickListener(this);
        new LoadDataTask(true).execute();
    }

    private void onLoadMore() {
        bindItemData(false);
    }

    private void onLoadRefresh() {
        adpater.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        bindItemData(true);
    }

    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            mPageIndex = 1;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void) null);
        } else {
            mPageIndex++;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void) null);
        }

    }
    List<InventoryBean.ListBean> itemList = new ArrayList<>();
    public class LoadDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog = new LoadingDialog(vThis);
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object object) {
            mloadingDialog.stop();
            if (mIsRefresh)
                swipeLayout.setRefreshing(false);
            if (object instanceof String && ((String) object).startsWith(ERROR_PREFIX)) {
                String msg = ((String) object).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(vThis, msg);
                return;
            }
            InventoryBean inventoryBean = (InventoryBean) object;

            if (inventoryBean != null) {
                List<InventoryBean.ListBean> data = inventoryBean.getList();
                if (mIsRefresh) {
                    if(itemList!=null)
                        itemList.clear();
                    if (!ListUtils.isEmpty(data))
                        itemList.addAll(data);
                } else {
                    if (!ListUtils.isEmpty(data))
                        itemList.addAll(data);
                }
                adpater.setNewData(itemList);
                adpater.notifyDataSetChanged();
                if (mIsRefresh) {
                    adpater.setEnableLoadMore(true);
                  //  swipeLayout.setRefreshing(false);
                    if (ListUtils.isEmpty(data)) {
                        adpater.loadMoreEnd(true);
                    }

                } else {
                    //
                    if (ListUtils.isEmpty(data)) {
                        adpater.loadMoreEnd(false);
                    }
                }
            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                InventoryBean saleBean = OtherAPI.getSkuStockList(vThis, mPageIndex, mPageSize);
                return saleBean;
            } catch (Exception ex) {
                Log.e(TAG, "获取盘点列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? ERROR_PREFIX : ex.getMessage();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
        }
    }
}
