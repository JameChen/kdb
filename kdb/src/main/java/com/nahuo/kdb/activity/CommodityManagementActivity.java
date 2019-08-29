package com.nahuo.kdb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.CommodityManagementAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.controls.DropDownView;
import com.nahuo.kdb.dialog.SortTimeDialog;
import com.nahuo.kdb.model.CdMaBean;
import com.nahuo.kdb.model.SelectBean;
import com.nahuo.kdb.zxing.activity.CaptureActivity;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommodityManagementActivity extends AppCompatActivity implements View.OnClickListener, DropDownView.OnItemClickListener {
    public static String TAG = CommodityManagementActivity.class.getSimpleName();
    private CommodityManagementActivity vThis = this;
    public static String ERROR_PREFIX = "error:";
    private int mPageIndex = 1;
    private int mPageSize = 20;
    private DropDownView tv_sort_time;
    private LoadDataTask loadDataTask;
    private LoadingDialog mloadingDialog;
    private List<SelectBean> timeList = new ArrayList<>();
    public final static int Type_Time = 1;
    private String beginTime = "", endTime = "";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommodityManagementAdapter adapter;
    private String itemCode = "", keyword = "";
    private TextView tv_summary;
    private List<CdMaBean.ItemsBean> data = new ArrayList<>();
    private ImageView iv_right_01, iv_right_02;
    private static final int REQUEST_OPEN_CAMERA = 1;
    private EditText et_search;
    private boolean isShowSearch = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataTask != null)
            loadDataTask.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_management);
        BWApplication.addActivity(this);
        isShowSearch = true;
        mloadingDialog = new LoadingDialog(vThis);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setVisibility(View.GONE);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = et_search.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        keyword = text;
                        onMyRefresh();
                    } else {
                        ViewHub.showShortToast(getApplicationContext(), "请输入关键字搜索");
                    }
                }
                return false;
            }
        });
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        iv_right_01 = (ImageView) findViewById(R.id.iv_right_01);
        iv_right_02 = (ImageView) findViewById(R.id.iv_right_02);
        iv_right_01.setImageResource(R.drawable.find);
        iv_right_02.setImageResource(R.drawable.sweep_code_white);
        iv_right_01.setVisibility(View.VISIBLE);
        iv_right_02.setVisibility(View.VISIBLE);
        iv_right_01.setOnClickListener(this);
        iv_right_02.setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitleCenter);
        TextView btnLeft = (TextView) findViewById(R.id.tvTLeft);
//        ImageView titlebar_icon_right = (ImageView) findViewById(R.id.titlebar_icon_right);
//        titlebar_icon_right.setImageResource(R.drawable.pn_message_left_white);
//        titlebar_icon_right.setOnClickListener(this);
//        titlebar_icon_right.setVisibility(View.GONE);
        tvTitle.setText("商品管理");
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        tv_sort_time = (DropDownView) findViewById(R.id.tv_sort_time);
        tv_sort_time.setOnItemClickListener(this);
        tv_sort_time.setNUSELETED_SHOW_NAME("选择时间");
        tv_sort_time.setType(Type_Time);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date now = FunctionHelper.GetDateTime(0);
        Date tomorrow = FunctionHelper.GetDateTime(1);
        Date yesterday = FunctionHelper.GetDateTime(-1);
        Date weekday = FunctionHelper.GetDateTime(-7);
        Date month = FunctionHelper.GetDateTime(-30);
        SelectBean sst1 = new SelectBean();
        sst1.setID(1);
        sst1.setName("今天");
        sst1.setStart_time(df.format(now));
        sst1.setEnd_time(df.format(tomorrow));
        SelectBean sst2 = new SelectBean();
        sst2.setID(2);
        sst2.setName("昨天");
        sst2.setStart_time(df.format(yesterday));
        sst2.setEnd_time(df.format(now));
        SelectBean sst3 = new SelectBean();
        sst3.setID(3);
        sst3.setName("过去一周");
        sst3.setStart_time(df.format(weekday));
        sst3.setEnd_time(df.format(now));
        SelectBean sst4 = new SelectBean();
        sst4.setID(4);
        sst4.setName("过去一月");
        sst4.setStart_time(df.format(month));
        sst4.setEnd_time(df.format(now));
        SelectBean sst5 = new SelectBean();
        sst5.setID(-1);
        sst5.setName("任意时间");
        //payList.add(s0);
        timeList.add(sst1);
        timeList.add(sst2);
        timeList.add(sst3);
        timeList.add(sst4);
        timeList.add(sst5);
        tv_sort_time.setupDataList(timeList);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.bg_red), getResources().getColor(R.color.red));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommodityManagementAdapter(vThis);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMore();
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                List<CdMaBean.ItemsBean> data = adapter.getData();
                CdMaBean.ItemsBean bean = data.get(position);
                if (bean != null) {
                    Intent intent = new Intent(vThis, CommodityManagementDetailActivity.class);
                    intent.putExtra(CommodityManagementDetailActivity.EXTRA_SOURCEID, bean.getSourceID());
                    startActivity(intent);
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onMyRefresh();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    private void loadData() {
        loadDataTask = new LoadDataTask(true);
        loadDataTask.execute((Void) null);
    }

    public void onMyRefresh() {
        adapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        bindItemData(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_OPEN_CAMERA) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    itemCode = scanResult;
                    onMyRefresh();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case R.id.iv_right_01:
                isShowSearch = !isShowSearch;
                if (isShowSearch) {
                    iv_right_01.setImageResource(R.drawable.find);
                    et_search.setVisibility(View.GONE);
                    FunctionHelper.hideSoftInput(vThis);
                } else {
                    iv_right_01.setImageResource(R.drawable.reback);
                    et_search.setVisibility(View.VISIBLE);
                }
                keyword="";
                et_search.setText("");
                break;
            case R.id.iv_right_02:
                Intent openCameraIntent = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);

                break;
        }
    }

    @Override
    public void onItemClick(SelectBean map, int pos, int realPos, int Type) {
        if (Type == Type_Time) {
            if (map == null) {
                vThis.beginTime = "";
                vThis.endTime = "";
            } else {
                if (map.getID() == -1) {
                    SortTimeDialog.getInstance(this).setPositive(new SortTimeDialog.PopDialogListener() {
                        @Override
                        public void onGetSortTimeDialogButtonClick(String beginTime, String endTime) {
                            vThis.beginTime = beginTime;
                            vThis.endTime = endTime;
                            loadData();
                        }
                    }).showDialog();
                    return;
                } else if (map.getID() == 1 || (map.getID() == 2) || (map.getID() == 3) || (map.getID() == 4)) {
                    vThis.beginTime = map.getStart_time();
                    vThis.endTime = map.getEnd_time();
                }
            }
            onMyRefresh();
        }
    }

    public class LoadDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object object) {
            mloadingDialog.stop();
            if (mIsRefresh)
                mSwipeRefreshLayout.setRefreshing(false);
            if (object instanceof String && ((String) object).startsWith(ERROR_PREFIX)) {
                String msg = ((String) object).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(vThis, msg);
                return;
            } else if (object instanceof CdMaBean) {
                CdMaBean bean = (CdMaBean) object;
                if (bean != null) {
                    tv_summary.setVisibility(View.VISIBLE);
                    tv_summary.setText("入库" + bean.getTotalInstockQty() + "件   " + "销售" + bean.getTotalSaleQty()
                            + "件   售罄率" + FunctionHelper.DoubleFormat(bean.getStockOutRate()) + "%");
                    //                SellerUsers.clear();
                    if (mIsRefresh) {
                        if (data != null)
                            data.clear();
                        if (!ListUtils.isEmpty(bean.getItems()))
                            data.addAll(bean.getItems());
                    } else {
                        if (!ListUtils.isEmpty(bean.getItems()))
                            data.addAll(bean.getItems());
                    }
                    adapter.setNewData(data);
                    if (mIsRefresh) {
                        adapter.setEnableLoadMore(true);
                       // mSwipeRefreshLayout.setRefreshing(false);
                        if (ListUtils.isEmpty(bean.getItems())) {
                            adapter.loadMoreEnd(true);
                        }

                    } else {
                        //
                        if (ListUtils.isEmpty(bean.getItems())) {
                            adapter.loadMoreEnd(false);
                        }
                    }
                }
            }

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                CdMaBean saleBean = OtherAPI.getItemStockSt(vThis, mPageIndex, mPageSize, beginTime, endTime, itemCode, keyword);

                return saleBean;
            } catch (Exception ex) {
                Log.e(TAG, "获取商品列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? ERROR_PREFIX : ex.getMessage();
            }
        }
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


    public void onLoadMore() {
        bindItemData(false);
    }
}
