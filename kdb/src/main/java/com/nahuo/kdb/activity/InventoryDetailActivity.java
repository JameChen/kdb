package com.nahuo.kdb.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.InventoryDetailAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.InventoryDetailBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.List;

public class InventoryDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = InventoryDetailActivity.class.getSimpleName();
    private InventoryDetailActivity vThis = this;
    private static final String ERROR_PREFIX = "error:";
    private LoadingDialog mloadingDialog;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private InventoryActivity.LoadDataTask loadDataTask;
    public static String Extra_Time = "Extra_Time";
    private String time = "";
    private InventoryDetailAdapter adapter;
    private List<InventoryDetailBean.RemarkListBean> remarkList;
    private TextView tv_01, tv_02, tv_03, tv_04, tv_05;
    private InventoryDetailBean bean;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataTask != null)
            loadDataTask.cancel(true);
    }

    public class LoadDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;
        private String stockTime;

        public LoadDataTask(String stockTime) {
            this.stockTime = stockTime;
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
            if (object instanceof String && ((String) object).startsWith(ERROR_PREFIX)) {
                String msg = ((String) object).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(vThis, msg);
                return;
            } else if (object instanceof InventoryDetailBean) {
                bean = (InventoryDetailBean) object;
                if (bean != null) {
                    remarkList = bean.getRemarkList();
                    if (!ListUtils.isEmpty(bean.getItems()))
                        adapter.setNewData(bean.getItems());
                    initHead();
                }
            }

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                InventoryDetailBean saleBean = OtherAPI.getSkuStockDetail(vThis, stockTime);
                return saleBean;
            } catch (Exception ex) {
                Log.e(TAG, "获取盘点详情发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? ERROR_PREFIX : ex.getMessage();
            }
        }
    }

    private void initHead() {
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.getOptUserName())) {
                if (tv_02 != null)
                    tv_02.setText("经手人：" + bean.getOptUserName());
            } else {
                if (tv_02 != null)
                    tv_02.setText("经手人：无");
            }
            if (tv_01 != null)
                tv_01.setText("盘点时间：" + bean.getCreateTime());
            if (tv_03 != null)
                tv_03.setText("盘亏" + bean.getCheckLostQty() + "件   金额共" + bean.getCheckLostAmount() + "元");
            if (tv_04 != null)
                tv_04.setText("盘盈" + bean.getCheckMakeQty() + "件   金额共" + bean.getCheckMakeAmount() + "元");
        }
        if (!ListUtils.isEmpty(remarkList)) {
            StringBuffer sbTxt = new StringBuffer();
            sbTxt.append("");
            for (int i = 0; i < remarkList.size(); i++) {
                InventoryDetailBean.RemarkListBean r = remarkList.get(i);
                sbTxt.append("经手人："+r.getOptUserName()+"-时间："+r.getCreateTime()+"-备注："+r.getRemark());
                if (i < remarkList.size() - 1) {
                    sbTxt.append("\n");
                }
            }
            if (tv_05 != null)
                tv_05.setText(sbTxt.toString());
        } else {

            if (tv_05 != null)
                tv_05.setText("无");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);
        BWApplication.addActivity(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvTitleCenter)).setText("盘点详情");
//        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.sw);
//        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.bg_red), getResources().getColor(R.color.red));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                onLoadRefresh();
//            }
//        });
//        swipeLayout.setRefreshing(true);
        //   adpater = new InventoryAdapter(this);
        adapter = new InventoryDetailAdapter(this);
        View view = LayoutInflater.from(this).inflate(R.layout.inventory_detail_head, null);
        tv_01 = (TextView) view.findViewById(R.id.tv_01);
        tv_02 = (TextView) view.findViewById(R.id.tv_02);
        tv_03 = (TextView) view.findViewById(R.id.tv_03);
        tv_04 = (TextView) view.findViewById(R.id.tv_04);
        tv_05 = (TextView) view.findViewById(R.id.tv_05);
        recyclerView.setAdapter(adapter);
        adapter.addHeaderView(view);
//        adpater.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                List<InventoryBean.ListBean> data = adapter.getData();
//                InventoryBean.ListBean bean = data.get(position);
//                if (bean!=null){
//
//                }
//            }
//        });
//        adpater.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                onLoadMore();
//            }
//        }, recyclerView);
        View empty = LayoutInflater.from(this).inflate(R.layout.textview_empty, null);
        TextView tv_empty = (TextView) empty.findViewById(R.id.tv_empty);
        tv_empty.setText("沒有盘点详细数据");
        //adpater.setEmptyView(empty);
        ImageView iv_right_01 = (ImageView) findViewById(R.id.iv_right_01);
        iv_right_01.setImageResource(R.drawable.sweep_code);
        iv_right_01.setVisibility(View.GONE);
        iv_right_01.setOnClickListener(this);
        if (getIntent() != null)
            time = getIntent().getStringExtra(vThis.Extra_Time);
        new LoadDataTask(time).execute();
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
