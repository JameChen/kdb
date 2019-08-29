package com.nahuo.kdb;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nahuo.kdb.activity.InventoryActivity;
import com.nahuo.kdb.activity.ScanRecordActivity;
import com.nahuo.kdb.activity.SpotGoodsActivity;
import com.nahuo.kdb.adapter.CommonListAdapter;
import com.nahuo.kdb.api.ApiHelper;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.eventbus.BusEvent;
import com.nahuo.kdb.eventbus.EventBusId;
import com.nahuo.kdb.model.ProductModel;
import com.nahuo.kdb.model.ScanQrcodeModel;
import com.nahuo.kdb.model.ShopItemListModel;
import com.nahuo.kdb.zxing.activity.CaptureActivity;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.nahuo.kdb.CommonListActivity.ListType.入库;
import static com.nahuo.kdb.CommonListActivity.ListType.库存;
import static com.nahuo.kdb.CommonListActivity.ListType.销售;

public class CommonListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnLoadMoreListener, PullToRefreshListView.OnRefreshListener {
    private Context mContext = this;
    private static final String TAG = "CommonListActivity";
    private CommonListActivity vThis = this;
    private LoadingDialog mloadingDialog;
    private int mPageIndex = 1;
    private int mPageSize = 10;
    private CommonListAdapter adapter;
    private List<ShopItemListModel> itemList = null;
    private static final int REQUEST_OPEN_CAMERA = 1;
    private static final int REQUEST_OPEN_CAMERA_KU = 2;
    private ScanQrcodeModel qrcodeItemData;
    private PullToRefreshListView mRefreshListView;
    private String searchKey;//搜索的关键字
    private EventBus mEventBus = EventBus.getDefault();
    private TextView tv_all, tv_batch_warehousing, tv_calcel_exhibition, tv_exhibition, detail_view_dian_btn, tv_inventory;
    private View detail_view;
    private TextView detail_view_txt, kc_detail_txt;
    private RelativeLayout rl_batch_warehousing;
    private LoadingDialog mDialog;
    private List<ShopItemListModel> mData = new ArrayList<>();
    private ImageView titlebar_btnScan;

    public enum ListType {
        入库,
        销售,
        库存,
    }

    private ListType mType;

    private Dialog writeListDialog;
    private String itemcode = "";

    @Override
    public void onLoadMore() {
        bindItemData(false);
    }

    @Override
    public void onRefresh() {
        bindItemData(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common_list);
        mEventBus.registerSticky(this);
        mDialog = new LoadingDialog(this);
        int type = getIntent().getIntExtra("type", 1);
        switch (type) {
            case 1:
                mType = 销售;
                ((TextView) findViewById(R.id.tv_title)).setText("销售");
                break;
            case 2:
                mType = 入库;
                ((TextView) findViewById(R.id.tv_title)).setText("入库");
                break;
            case 3:
                mType = 库存;
                ((TextView) findViewById(R.id.tv_title)).setText("库存");
                break;
            default:
                break;
        }
        mloadingDialog = new LoadingDialog(vThis);
        initView();
        bindItemData(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
        adapter = null;
        if (itemList != null) {
            itemList.clear();
            itemList = null;
        }
        if (writeListDialog != null) {
            if (writeListDialog.isShowing())
                writeListDialog.dismiss();
            writeListDialog = null;
        }
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.COMMON_LIST_RELOAD:
            case EventBusId.SEARCH_入库:
                bindItemData(true);
                break;
            case EventBusId.SEARCH_销售:
            case EventBusId.SEARCH_库存:
                String keyword = "";
                if (event.data != null) {
                    keyword = event.data.toString();
                }
                if (mType == 销售 || mType == 库存) {
                    searchKey = keyword;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_BACK_入库:
                String keyword1 = "";
                if (event.data != null) {
                    keyword1 = event.data.toString();
                }
                if (mType == ListType.入库) {
                    searchKey = keyword1;
                    bindItemData(true);
                }
                break;

        }
    }

    private void initView() {
        detail_view_dian_btn = (TextView) findViewById(R.id.detail_view_dian_btn);
        tv_inventory = (TextView) findViewById(R.id.tv_inventory);
        tv_inventory.setOnClickListener(this);
        detail_view_dian_btn.setOnClickListener(this);
        rl_batch_warehousing = (RelativeLayout) findViewById(R.id.rl_batch_warehousing);
        titlebar_btnScan = (ImageView) findViewById(R.id.titlebar_btnScan);
        titlebar_btnScan.setOnClickListener(this);
        tv_all = (TextView) findViewById(R.id.tv_all);
        tv_batch_warehousing = (TextView) findViewById(R.id.tv_batch_warehousing);
        tv_calcel_exhibition = (TextView) findViewById(R.id.tv_calcel_exhibition);
        tv_exhibition = (TextView) findViewById(R.id.tv_exhibition);
        tv_calcel_exhibition.setOnClickListener(this);
        tv_exhibition.setOnClickListener(this);
        tv_all.setOnClickListener(this);
        tv_batch_warehousing.setOnClickListener(this);
        ImageView backBtn = (ImageView) findViewById(R.id.titlebar_btnLeft);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        findViewById(R.id.titlebar_btnAdd).setOnClickListener(this);
        findViewById(R.id.titlebar_btnSearch).setOnClickListener(this);
        findViewById(R.id.titlebar_btnShopCart).setOnClickListener(this);
        switch (mType) {
            case 入库:
                titlebar_btnScan.setVisibility(View.GONE);
                findViewById(R.id.titlebar_btnAdd).setVisibility(View.VISIBLE);
                findViewById(R.id.sale_detail_view).setVisibility(View.GONE);
                tv_inventory.setVisibility(View.GONE);
                rl_batch_warehousing.setVisibility(View.VISIBLE);
                tv_batch_warehousing.setVisibility(View.VISIBLE);
                detail_view_dian_btn.setVisibility(View.VISIBLE);
                tv_exhibition.setVisibility(View.GONE);
                tv_calcel_exhibition.setVisibility(View.GONE);
                findViewById(R.id.titlebar_btnSearch).setVisibility(View.VISIBLE);
                break;
            case 销售:
                titlebar_btnScan.setVisibility(View.GONE);
                tv_inventory.setVisibility(View.GONE);
                detail_view_dian_btn.setVisibility(View.GONE);
                findViewById(R.id.titlebar_btnAdd).setVisibility(View.VISIBLE);
                findViewById(R.id.titlebar_btnSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.titlebar_btnShopCart).setVisibility(View.VISIBLE);
                rl_batch_warehousing.setVisibility(View.GONE);
                findViewById(R.id.sale_detail_view).setVisibility(View.VISIBLE);
                findViewById(R.id.sale_detail_view_btn).setOnClickListener(this);
                break;
            case 库存:
                titlebar_btnScan.setVisibility(View.VISIBLE);
                tv_inventory.setVisibility(View.VISIBLE);
                detail_view_dian_btn.setVisibility(View.GONE);
                findViewById(R.id.titlebar_btnSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.sale_detail_view).setVisibility(View.GONE);
                rl_batch_warehousing.setVisibility(View.GONE);
                tv_batch_warehousing.setVisibility(View.GONE);
                tv_exhibition.setVisibility(View.VISIBLE);
                tv_calcel_exhibition.setVisibility(View.VISIBLE);
                break;
        }

        detail_view = findViewById(R.id.detail_view);
        detail_view_txt = (TextView) findViewById(R.id.detail_view_txt);
        kc_detail_txt = (TextView) findViewById(R.id.kc_detail_txt);
        findViewById(R.id.detail_view_btn).setOnClickListener(this);

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.id_group_listview);
        initItemAdapter();
        mRefreshListView.setCanLoadMore(true);
        mRefreshListView.setCanRefresh(true);
        mRefreshListView.setMoveToFirstItemAfterRefresh(true);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setOnLoadListener(this);
        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    position = 1;
                }
                ShopItemListModel model = adapter.getItem(position - 1);
                Intent intent = new Intent(vThis, ItemDetailsActivity.class);
                intent.putExtra(ItemDetailsActivity.EXTRA_ID, model.getAgentItemId());
                intent.putExtra(ItemDetailsActivity.EXTRA_QSID, model.getQsId());
                intent.putExtra("type", mType);
                intent.putExtra("shopItem", model);
                startActivity(intent);
            }
        });
        mRefreshListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    position = 1;
                }
                final ShopItemListModel model = adapter.getItem(position - 1);

                ViewHub.showOkDialog(vThis, "提示", "确定删除这条入库记录?", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DelRKTask(model).execute();
                    }
                });

                return true;
            }
        });
    }

    private static enum Step {
        AllSaveInStock, SetItemShow, SetItemHide
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case AllSaveInStock:
                    mDialog.start("入库中...");
                    break;
                case SetItemShow:
                    mDialog.start("展示中...");
                    break;
                case SetItemHide:
                    mDialog.start("取消展示中...");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {

                    case AllSaveInStock:
                        KdbAPI.getInstance().SaveInStock(mContext, mData);
                        return "OK";
                    case SetItemShow:
                        //展示
                        KdbAPI.getInstance().SetItemHide(mContext, mData, false);
                        break;
                    case SetItemHide:
                        KdbAPI.getInstance().SetItemHide(mContext, mData, true);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mDialog.isShowing()) {
                mDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {

                    case AllSaveInStock:
                        ViewHub.showLongToast(mContext, "入库成功");
                        bindItemData(true);
                        //  EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_入库));
                        break;
                    case SetItemShow:
                        ViewHub.showLongToast(mContext, "展示成功");
                        bindItemData(true);
                        break;
                    case SetItemHide:
                        ViewHub.showLongToast(mContext, "取消展示");
                        bindItemData(true);
                        break;
                }

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnScan:
                Intent openCameraIntent1 = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent1, REQUEST_OPEN_CAMERA_KU);
                break;
            case R.id.tv_inventory:
                startActivity(new Intent(vThis, InventoryActivity.class));
                break;
            case R.id.detail_view_dian_btn:
                startActivity(new Intent(vThis, SpotGoodsActivity.class));
                break;
            case R.id.tv_all:
                //全选
                adapter.setAllCheck();
                break;
            case R.id.tv_batch_warehousing:
                //批量入库
                mData.clear();
                final List<ShopItemListModel> data = adapter.getWareCheck();
                mData.addAll(data);
                if (data == null || data.size() == 0) {
                    Toast.makeText(vThis, "请勾选商品", Toast.LENGTH_SHORT).show();
                } else {
//                    int count = 0;
//                    for (ShopItemListModel bean : data) {
//                        count += bean.getBookQty();
//                    }
                    ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                            "确定勾选的" + data.size() + "款要入库吗", "取消", "确定", new LightPopDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int which) {
                                    new Task(Step.AllSaveInStock).execute();
                                }
                            });

                }
                break;
            case R.id.tv_exhibition:
                //展示
                mData.clear();
                final List<ShopItemListModel> data1 = adapter.getWareCheck();
                mData.addAll(data1);
                if (data1 == null || data1.size() == 0) {
                    Toast.makeText(vThis, "请勾选商品", Toast.LENGTH_SHORT).show();
                } else {
//                    int count = 0;
//                    for (ShopItemListModel bean : data) {
//                        count += bean.getBookQty();
//                    }
                    ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                            "确定勾选的" + data1.size() + "款要展示吗", "取消", "确定", new LightPopDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int which) {
                                    new Task(Step.SetItemShow).execute();
                                }
                            });
                }
                break;
            case R.id.tv_calcel_exhibition:
                //取消展示
                mData.clear();
                final List<ShopItemListModel> data2 = adapter.getWareCheck();
                mData.addAll(data2);
                if (data2 == null || data2.size() == 0) {
                    Toast.makeText(vThis, "请勾选商品", Toast.LENGTH_SHORT).show();
                } else {
//                    int count = 0;
//                    for (ShopItemListModel bean : data) {
//                        count += bean.getBookQty();
//                    }
                    ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                            "确定勾选的" + data2.size() + "款要取消展示吗", "取消", "确定", new LightPopDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int which) {
                                    new Task(Step.SetItemHide).execute();
                                }
                            });
                }
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_btnAdd:
                if (mType == 入库) {
                    startActivity(new Intent(vThis, ScanRecordActivity.class));
                } else {
                    Intent openCameraIntent = new Intent(vThis, CaptureActivity.class);
                    startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);
                }
                break;
            case R.id.titlebar_btnSearch:
                CommonSearchActivity.launch(this, mType);
                break;
            case R.id.titlebar_btnShopCart:
                Intent intent = new Intent(vThis, ShopCartActivity.class);
                startActivity(intent);
                break;
            case R.id.detail_view_btn:
                double dhCount = 0;
                for (ShopItemListModel m : adapter.mList) {
                    dhCount += m.getDhQty();
                }
                ViewHub.showOkDialog(vThis, "提示", "确定要入库" + dhCount + "件？", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new SubmitRKTask().execute();
                    }
                });
                break;
            case R.id.sale_detail_view_btn:
                Intent intent1 = new Intent(vThis, SaleLogActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_OPEN_CAMERA) {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString("result");
                    try {
                        if (mType == 销售) {
                            new SaleScanQrCodeTask(scanResult).execute();
                        } else {
                            new ScanQrCodeTask(scanResult).execute();

                        }
                    } catch (Exception e) {
                        ViewHub.showShortToast(vThis, "扫码解析异常");
                    }

                }
            } else if (requestCode == REQUEST_OPEN_CAMERA_KU) {
                try {
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            itemcode = bundle.getString("result");
                            onRefresh();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    // 初始化数据
    private void initItemAdapter() {
        if (itemList == null)
            itemList = new ArrayList<ShopItemListModel>();

        adapter = new CommonListAdapter(vThis, itemList, mType);
        mRefreshListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            if (isFinishing()) {
                return;
            }
            mloadingDialog.stop();
            mRefreshListView.onRefreshComplete();
            if (result == null || result.equals(null)) {
                if (mIsRefresh) {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                } else {

                }
                ViewHub.showShortToast(vThis, "亲！没有数据了");
                return;
            }
            if (result instanceof String) {
                ViewHub.showLongToast(vThis, (String) result);

                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }
            } else {
                @SuppressWarnings("unchecked")
                List<ShopItemListModel> list = (List<ShopItemListModel>) result;
                if (mIsRefresh) {
                    itemList.clear();
                    itemList = list;
                } else {
                    itemList.addAll(list);
                }
                adapter.mList = itemList;
                adapter.notifyDataSetChanged();

                if (mType == 库存) {
                    int count = 0;
                    double money = 0;
                    for (ShopItemListModel m : itemList) {
                        count += m.getQty();
                        money += m.getQty() * m.getOriPrice();
                    }
                    kc_detail_txt.setVisibility(View.GONE);
                    kc_detail_txt.setText("库存数量:" + count + "件,库存金额:¥" + Utils.moneyFormat(money));
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.mList = itemList;
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Object doInBackground(Boolean... params) {
            try {
                List<ShopItemListModel> result = KdbAPI.getList(vThis, mType, searchKey, mPageIndex, mPageSize, itemcode);
                searchKey = "";
                if (result == null)
                    return null;
                return result;
            } catch (Exception ex) {
                Log.e(TAG, "获取列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    /**
     * /** 绑定款式列表
     */
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

    public PullToRefreshListView getRefreshListView() {
        return mRefreshListView;
    }

    public class ScanQrCodeTask extends AsyncTask<String, Void, Object> {
        private String qrStr;

        public ScanQrCodeTask(String s) {
            qrStr = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String) {
                if (result.toString().equals("OK")) {
                    ScanQrcodeItemLoaded();
                } else {
                    ViewHub.showLongToast(vThis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                qrcodeItemData = KdbAPI.getScanRecord(vThis, qrStr);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取qrcode异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    public class SaleScanQrCodeTask extends AsyncTask<String, Void, Object> {
        private String qrStr;

        public SaleScanQrCodeTask(String s) {
            qrStr = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String) {
                if (result.toString().equals("OK")) {
                    ViewHub.showLongToast(vThis, "已添加到购物车");
                } else {
                    ViewHub.showLongToast(vThis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                KdbAPI.addFromCode(vThis, qrStr);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "addFromCode异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    private void ScanQrcodeItemLoaded() {
        switch (mType) {
            case 入库:
                RCQrcodeItemLoaded();
                break;
//            case 销售:
//                XSQrcodeItemLoaded();
//                break;
            case 库存:
                findViewById(R.id.titlebar_btnSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.sale_detail_view).setVisibility(View.GONE);
                break;
        }
    }

    private void XSQrcodeItemLoaded() {
        if (qrcodeItemData != null) {
            new AddShopCartTask().execute();
        }
    }

    private void RCQrcodeItemLoaded() {
        if (qrcodeItemData != null) {
            int index = 0;
            boolean changed = false;
            for (ShopItemListModel m : adapter.mList) {
                if (m.getAgentItemId() == qrcodeItemData.getItemID() || m.getItemId() == qrcodeItemData.getItemID()) {
                    if (m.getColor().length() > 0 && m.getSize().length() > 0) {
                        //有颜色尺码,如果不相同则新增一个ShopItemListModel
                        if (m.getColor() == qrcodeItemData.getColor() &&
                                m.getSize() == qrcodeItemData.getSize()) {
                            if (m.getDhQty() >= m.getShipQty()) {
                                ViewHub.showLongToast(vThis, "收货数大于已发数");
                            }
                            m.setDhQty(m.getDhQty() + 1);
                            changed = true;
                        } else {
                            ShopItemListModel mNew = new ShopItemListModel();
                            mNew.setDhQty(1);
                            mNew.setSize(qrcodeItemData.getSize());
                            mNew.setAgentPrice(m.getAgentPrice());
                            mNew.setAgentItemId(m.getAgentItemId());
                            mNew.setBookQty(m.getBookQty());
                            mNew.setColor(qrcodeItemData.getColor());
                            mNew.setCover(m.getCover());
                            mNew.setName(m.getName());
                            mNew.setOriPrice(m.getOriPrice());
                            mNew.setPrice(m.getPrice());
                            mNew.setQsId(m.getQsId());
                            mNew.setQsName(m.getQsName());
                            mNew.setQty(m.getQty());
                            mNew.setRetailPrice(m.getRetailPrice());
                            mNew.setShipQty(m.getShipQty());
                            adapter.mList.add(index, mNew);
                            changed = true;
                        }
                    } else {
                        if (m.getDhQty() >= m.getShipQty()) {
                            ViewHub.showLongToast(vThis, "收货数大于已发数");
                        }
                        m.setDhQty(m.getDhQty() + 1);
                        m.setColor(qrcodeItemData.getColor());
                        m.setSize(qrcodeItemData.getSize());
                        changed = true;
                    }
                    break;
                }
                index++;
            }
            if (!changed) {
//                ShopItemListModel mNew = new ShopItemListModel();
//                mNew.setDhQty(1);
//                mNew.setSize(qrcodeItemData.getSize());
//                mNew.setAgentPrice(qrcodeItemData.getAgentPrice());
//                mNew.setAgentItemId(qrcodeItemData.getItemID());
//                mNew.setBookQty(qrcodeItemData.get());
//                mNew.setColor(qrcodeItemData.getColor());
//                mNew.setCover(qrcodeItemData.getCover());
//                mNew.setName(qrcodeItemData.getName());
//                mNew.setOriPrice(qrcodeItemData.getOriPrice());
//                mNew.setPrice(qrcodeItemData.getPrice());
//                mNew.setQsId(qrcodeItemData.getQsId());
//                mNew.setQsName(qrcodeItemData.getQsName());
//                mNew.setQty(qrcodeItemData.getQty());
//                mNew.setRetailPrice(qrcodeItemData.getRetailPrice());
//                mNew.setShipQty(qrcodeItemData.getShipQty());
//                adapter.mList.add(index, mNew);
            }
            adapter.notifyDataSetChanged();
            showSaveBtn();
        }
    }

    private void showSaveBtn() {
        double dhCount = 0;
        for (ShopItemListModel m : adapter.mList) {
            dhCount += m.getDhQty();
        }

        if (dhCount > 0) {
            detail_view.setVisibility(View.VISIBLE);
            detail_view_txt.setText("到货数量: " + dhCount);
        } else {
            detail_view.setVisibility(View.GONE);
        }
    }

    public class SubmitRKTask extends AsyncTask<String, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("入库中");
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String) {
                if (result.toString().equals("OK")) {
                    ViewHub.showLongToast(vThis, "保存成功");
                    ScanQrcodeItemLoaded();
                } else {
                    ViewHub.showLongToast(vThis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                List<ProductModel> pl = new ArrayList<>();
                for (ShopItemListModel m : adapter.mList) {
                    ProductModel pm = new ProductModel();
                    pm.setSize(m.getSize());
                    pm.setColor(m.getColor());
                    pm.setDHQty(m.getDhQty());
                    pm.setAgentItemId(m.getAgentItemId());
                    pl.add(pm);
                }
                KdbAPI.getInstance().submitRK(vThis, pl);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "保存入库异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    public class DelRKTask extends AsyncTask<String, Void, Object> {
        private ShopItemListModel delData;

        public DelRKTask(ShopItemListModel data) {
            delData = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("删除中");
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String) {
                if (result.toString().equals("OK")) {
                    ViewHub.showLongToast(vThis, "删除成功");
                    onRefresh();
                } else {
                    ViewHub.showLongToast(vThis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                KdbAPI.delRC(vThis, delData.getAgentItemId() + "");
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "删除入库异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    public class AddShopCartTask extends AsyncTask<String, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("添加到购物车中");
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String) {
                if (result.toString().equals("OK")) {
                    ViewHub.showLongToast(vThis, "已添加到购物车");
                } else {
                    ViewHub.showLongToast(vThis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                KdbAPI.addQrcodeItem2ShopCard(vThis, qrcodeItemData);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "添加到购物车异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }
}
