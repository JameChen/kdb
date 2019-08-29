package com.nahuo.kdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nahuo.kdb.activity.SearchSaleLogActivity;
import com.nahuo.kdb.adapter.SaleListAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.controls.DropDownView;
import com.nahuo.kdb.dialog.SortTimeDialog;
import com.nahuo.kdb.model.OrderModel;
import com.nahuo.kdb.model.SaleBean;
import com.nahuo.kdb.model.SelectBean;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;
import com.nahuo.library.helper.FunctionHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleLogActivity extends AppCompatActivity implements OnClickListener,DropDownView.OnItemClickListener {

    private static final String TAG = SaleLogActivity.class.getSimpleName();
    private SaleLogActivity vThis = this;
    private PullToRefreshListView pullRefreshListView;
    private LoadDataTask loadDataTask;
    private LoadingDialog mloadingDialog;
    private TextView tvEmptyMessage, tv_summary;
    private SaleListAdapter adapter;
    private List<OrderModel.OrderListBean> itemList = null;
    private View emptyView;
    private int mPageIndex = 1;
    private int mPageSize = 20;
    private String Summary = "";
    private int mStatus = -1;
    private String beginTime = "", endTime = "";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int  pay_type,sellerUserID;
    private View rl_layout;
    private DropDownView tv_sort_time, tv_sort_order,tv_sort_seller, tv_sort_pay;
    private List<SelectBean> SellerUsers=new ArrayList<>();
    private List<SelectBean> payList=new ArrayList<>();
    private List<SelectBean> orderList=new ArrayList<>();
    private List<SelectBean> timeList=new ArrayList<>();
    public final static int Type_Saler=1;
    public final static int Type_Pay=2;
    public final static int Type_Methor=3;
    public final static int Type_Time=4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_sale_list);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//                R.layout.layout_titlebar_default);// 更换自定义标题栏布局
        BWApplication.addActivity(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataTask != null)
            loadDataTask.cancel(true);
    }

    private void initView() {
        emptyView = findViewById(R.id.empty_view);
        rl_layout=findViewById(R.id.rl_layout);
        // 标题栏
        TextView tvTitle = (TextView) findViewById(R.id.tvTitleCenter);
        TextView btnLeft = (TextView) findViewById(R.id.tvTLeft);
//        ImageView titlebar_icon_right = (ImageView) findViewById(R.id.titlebar_icon_right);
//        titlebar_icon_right.setImageResource(R.drawable.pn_message_left_white);
//        titlebar_icon_right.setOnClickListener(this);
//        titlebar_icon_right.setVisibility(View.GONE);
        tvTitle.setText("销售单");
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        tv_sort_time=(DropDownView) findViewById(R.id.tv_sort_time);
        tv_sort_time.setNUSELETED_SHOW_NAME("选择时间");
       // tv_sort_time.setOnClickListener(this);
        tv_sort_time.setOnItemClickListener(this);
        tv_sort_order=(DropDownView) findViewById(R.id.tv_sort_order);
        tv_sort_seller=(DropDownView) findViewById(R.id.tv_sort_seller);
        tv_sort_pay=(DropDownView) findViewById(R.id.tv_sort_pay);
        tv_sort_order.setType(Type_Methor);
        tv_sort_seller.setType(Type_Saler);
        tv_sort_pay.setType(Type_Pay);
        tv_sort_time.setType(Type_Time);
        tv_sort_order.setNUSELETED_SHOW_NAME("全部订单");
        tv_sort_seller.setNUSELETED_SHOW_NAME("销售员");
        tv_sort_pay.setNUSELETED_SHOW_NAME("支付方式");
        tv_sort_seller.setOnItemClickListener(this);
        payList.clear();
//        SelectBean s0=new SelectBean();
//        s0.setID(0);
//        s0.setName("支付方式");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date tomorrow = FunctionHelper.GetDateTime(1);
        Date now = FunctionHelper.GetDateTime(0);
        Date yesterday = FunctionHelper.GetDateTime(-1);
        Date weekday = FunctionHelper.GetDateTime(-7);
        Date month = FunctionHelper.GetDateTime(-30);
        SelectBean sst1=new SelectBean();
        sst1.setID(1);
        sst1.setName("今天");
        sst1.setStart_time(df.format(now));
        sst1.setEnd_time(df.format(tomorrow));
        SelectBean sst2=new SelectBean();
        sst2.setID(2);
        sst2.setName("昨天");
        sst2.setStart_time(df.format(yesterday));
        sst2.setEnd_time(df.format(now));
        SelectBean sst3=new SelectBean();
        sst3.setID(3);
        sst3.setName("过去一周");
        sst3.setStart_time(df.format(weekday));
        sst3.setEnd_time(df.format(now));
        SelectBean sst4=new SelectBean();
        sst4.setID(4);
        sst4.setName("过去一月");
        sst4.setStart_time(df.format(month));
        sst4.setEnd_time(df.format(now));
        SelectBean sst5=new SelectBean();
        sst5.setID(-1);
        sst5.setName("任意时间");
        //payList.add(s0);
        timeList.add(sst1);
        timeList.add(sst2);
        timeList.add(sst3);
        timeList.add(sst4);
        timeList.add(sst5);
        tv_sort_time.setupDataList(timeList);
        SelectBean ss1=new SelectBean();
        ss1.setID(1);
        ss1.setName("待支付");
        SelectBean ss2=new SelectBean();
        ss2.setID(5);
        ss2.setName("已完成");
        SelectBean ss3=new SelectBean();
        ss3.setID(10);
        ss3.setName("已取消");
        //payList.add(s0);
        orderList.add(ss1);
        orderList.add(ss2);
        orderList.add(ss3);
        tv_sort_order.setupDataList(orderList);
        tv_sort_order.setOnItemClickListener(this);
        SelectBean s1=new SelectBean();
        s1.setID(1);
        s1.setName("现金");
        SelectBean s2=new SelectBean();
        s2.setID(2);
        s2.setName("微信");
        SelectBean s3=new SelectBean();
        s3.setID(3);
        s3.setName("支付宝");
        //payList.add(s0);
        payList.add(s1);
        payList.add(s2);
        payList.add(s3);

        tv_sort_pay.setupDataList(payList);
        tv_sort_pay.setOnItemClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.bg_red),getResources().getColor(R.color.red));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        // 初始化适配器
        tvEmptyMessage = (TextView) emptyView
                .findViewById(R.id.layout_empty_tvMessage);
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        mloadingDialog = new LoadingDialog(vThis);
        pullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview_items);
//        pullRefreshListView.setCanLoadMore(true);
//        pullRefreshListView.setCanRefresh(true);
//        pullRefreshListView.setMoveToFirstItemAfterRefresh(true);
//        pullRefreshListView.setOnRefreshListener(this);
//        pullRefreshListView.setOnLoadListener(this);
//        pullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    position = 1;
//                }
//                OrderModel model = adapter.getItem(position - 1);
//                Intent intent = new Intent(vThis, SaleDetailActivity.class);
//                intent.putExtra("orderCode", model.getOrderCode());
//                startActivity(intent);
//            }
//        });

        // 刷新数据
        showEmptyView(false, "");
        emptyView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

//                pullRefreshListView.pull2RefreshManually();
//
//                if (pullRefreshListView != null) {
//                    if (pullRefreshListView.isCanRefresh())
//                        pullRefreshListView.onRefreshComplete();
//
//                    if (pullRefreshListView.isCanLoadMore())
//                        pullRefreshListView.onLoadMoreComplete();
//                }

            }
        });

        initItemAdapter();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SearchSaleLogActivity.REQUEST_CODE && resultCode == SearchSaleLogActivity.Resutlt_Code) {
            if (data != null) {
                mStatus = data.getIntExtra(SearchSaleLogActivity.EXTRA_STATUS, -1);
                beginTime = data.getStringExtra(SearchSaleLogActivity.EXTRA_START_TIME);
                endTime = data.getStringExtra(SearchSaleLogActivity.EXTRA_END_TIME);
                loadDataTask = new LoadDataTask(true);
                loadDataTask.execute((Void) null);
            }
        }
    }

    private void loadData() {
        loadDataTask = new LoadDataTask(true);
        loadDataTask.execute((Void) null);
    }
    private static final String ERROR_PREFIX = "error:";
    // 初始化数据
    private void initItemAdapter() {
        if (itemList == null)
            itemList = new ArrayList<>();

        adapter = new SaleListAdapter(vThis);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                onLoadMore();
            }
        },mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                List<OrderModel.OrderListBean> data= adapter.getData();
                OrderModel.OrderListBean bean= data.get(position);
                if (bean!=null)
                {
                    Intent intent = new Intent(vThis, SaleDetailActivity.class);
                intent.putExtra("orderCode", bean.getCode());
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
        //pullRefreshListView.setAdapter(adapter);

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case R.id.titlebar_icon_right:
                Intent intent = new Intent(this, SearchSaleLogActivity.class);
                startActivityForResult(intent, SearchSaleLogActivity.REQUEST_CODE);
                break;

        }
    }

    public void onMyRefresh() {
        adapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        bindItemData(true);
        if (itemList.size() == 0) {
            showEmptyView(false, "您还没有销售记录");
        } else {

        }

    }

    @Override
    public void onItemClick(SelectBean map, int pos, int realPos, int Type) {
        switch (Type) {
            case Type_Time:
                if (map==null){
                   // pay_type=0;
                    vThis.beginTime="";
                    vThis.endTime="";
                }else {
                   if (map.getID()==-1){
                       SortTimeDialog.getInstance(this).setPositive(new SortTimeDialog.PopDialogListener() {
                           @Override
                           public void onGetSortTimeDialogButtonClick(String beginTime, String endTime) {
                               vThis.beginTime=beginTime;
                               vThis.endTime=endTime;
                               loadData();
                           }
                       }).showDialog();
                       return;
                   }else if (map.getID()==1|| (map.getID()==2)|| (map.getID()==3)|| (map.getID()==4)){
                       vThis.beginTime=map.getStart_time();
                       vThis.endTime=map.getEnd_time();
                   }
                }

                break;
            case Type_Pay:
                if (map==null){
                    pay_type=0;
                }else {
                    pay_type=map.getID();
                }
                break;
            case Type_Methor:
                if (map==null){
                    mStatus=-1;
                }else {
                    mStatus=map.getID();
                }
                break;
            case Type_Saler:
                if (map==null){
                    sellerUserID=0;
                }else {
                    sellerUserID=map.getID();
                }
                break;
        }
        onMyRefresh();
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
            }
           SaleBean  saleBean= (SaleBean) object;
            if (saleBean!=null) {
                SellerUsers.clear();
                if (!ListUtils.isEmpty(saleBean.getSellerUsers()))
                SellerUsers.addAll(saleBean.getSellerUsers());
                if (tv_sort_seller!=null)
                tv_sort_seller.setupDataList(SellerUsers);
                Summary = saleBean.getSummary();
                OrderModel result = saleBean.getOrders();
                if (result != null) {
                    if (mIsRefresh) {
                        if (itemList != null)
                            itemList.clear();
                        if (!ListUtils.isEmpty(result.getOrderList()))
                            itemList.addAll(result.getOrderList());
                    } else {
                        if (!ListUtils.isEmpty(result.getOrderList()))
                            itemList.addAll(result.getOrderList());
                    }
                    adapter.setMyData(itemList);
                    if (itemList.size() > 0) {
                        vThis.showEmptyView(false, "");
                    } else {
                        vThis.showEmptyView(true, "您还没有销售数据");
                    }

                    if (mIsRefresh) {
                        if (TextUtils.isEmpty(Summary)) {
                            tv_summary.setVisibility(View.GONE);
                        } else {
                            tv_summary.setVisibility(View.VISIBLE);
                            tv_summary.setText(Summary);
                        }
                        adapter.setEnableLoadMore(true);
                        if (ListUtils.isEmpty(result.getOrderList())) {
                            adapter.loadMoreEnd(true);
                        }

                    } else {
                        //
                        if (ListUtils.isEmpty(result.getOrderList())) {
                            adapter.loadMoreEnd(false);
                        }
                    }
                    //adapter.loadMoreComplete();
                }
            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                SaleBean saleBean = OtherAPI.getSaleLog(vThis, mPageIndex, mPageSize, mStatus, beginTime, endTime,pay_type,sellerUserID);

                return saleBean;
            } catch (Exception ex) {
                Log.e(TAG, "获取销售列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? ERROR_PREFIX: ex.getMessage();
            }
        }
    }

    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            showEmptyView(false, "");
            mPageIndex = 1;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void) null);
        } else {
            mPageIndex++;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void) null);
        }

    }

    /**
     * 显示空数据视图
     */
    private void showEmptyView(boolean show, String msg) {
       // pullRefreshListView.setVisibility(show ? View.GONE : View.VISIBLE);
        rl_layout.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (TextUtils.isEmpty(msg)) {
            tvEmptyMessage.setText(getString(R.string.layout_empty_message));
        } else {
            tvEmptyMessage.setText(msg);
        }
    }

    public void onLoadMore() {
        bindItemData(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }
}
