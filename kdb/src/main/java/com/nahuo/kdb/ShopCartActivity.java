package com.nahuo.kdb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.activity.SallerActivity;
import com.nahuo.kdb.adapter.ShopcartAdapter;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.model.CreateProduceBean;
import com.nahuo.kdb.model.ProdectBean;
import com.nahuo.kdb.model.ShopCartModel;
import com.nahuo.kdb.zxing.activity.CaptureActivity;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.nahuo.kdb.ShopCartActivity.Step.UPDATE_ITEM;


public class ShopCartActivity extends BaseActivity2 implements OnClickListener, PullToRefreshListView.OnRefreshListener {

    protected static final String TAG = "ShopCartActivity";

    private ShopCartActivity vThis = this;
    private CheckBox mCbSelectAll;
    private TextView txt2, txtSummary;
    private ShopcartAdapter mAdapter;
    private PullToRefreshListView mRefreshListView;
    private View mBottomView;
    private View nBottomLine;
    private ImageView mIvScroll2Top;
    private LoadingDialog mloadingDialog;
    private ShopCartModel updateShopCartModel;
    private ArrayList<ShopCartModel> datas;
    private View empty_view, layout_saller;
    private TextView tv_saller_name, tv_member;
    private List<ProdectBean.SellerUsersBean> sList;
    private static final int REQUEST_OPEN_CAMERA = 1;
    private static final int REQUEST_OPEN_CAMERA_MEMBER = 2;

    public enum Step {
        LOAD_DATA, UPDATE_ITEM, DELETE_ITEMS, Create_Order_Preview, ScanShopCart
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcart);
        BWApplication.addActivity(this);
        setTitle(R.string.shopping_cart);
        setRightIcon01(R.drawable.sweep_code_white);
        setRightIcon02(R.drawable.list_search_white);
        setRight01ClickListener(this);
        setRight02ClickListener(this);
        initViews();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {

        new Task(Step.LOAD_DATA).execute();
    }

    private void initViews() {
        mloadingDialog = new LoadingDialog(vThis);
        findViewById(R.id.rl_member).setOnClickListener(this);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_shopcart);
        mRefreshListView.setCanLoadMore(false);
        mRefreshListView.setCanRefresh(true);
        mRefreshListView.setMoveToFirstItemAfterRefresh(true);
        mRefreshListView.setOnRefreshListener(this);
        empty_view = findViewById(R.id.empty_view);
        layout_saller = findViewById(R.id.layout_saller);
        layout_saller.setOnClickListener(this);
        tv_saller_name = (TextView) findViewById(R.id.tv_saller_name);
        tv_member = (TextView) findViewById(R.id.tv_member);
        findViewById(R.id.btn_reload).setOnClickListener(this);
        mCbSelectAll = (CheckBox) findViewById(android.R.id.checkbox);
        mCbSelectAll.setOnClickListener(this);
        txt2 = (TextView) findViewById(android.R.id.text2);
        txtSummary = (TextView) findViewById(android.R.id.summary);
        mBottomView = findViewById(android.R.id.inputArea);
        nBottomLine = findViewById(R.id.line);
        mBottomView.setVisibility(View.INVISIBLE);
        mIvScroll2Top = (ImageView) findViewById(R.id.iv_scroll_top);

        mRefreshListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mIvScroll2Top.setVisibility(firstVisibleItem > 0 ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void init() {
        mAdapter = new ShopcartAdapter(this);
        mAdapter.setTotalPriceChangedListener(new ShopcartAdapter.TotalPriceChangedListener() {
            @Override
            public void selectItemChanged() {
                resetCount();
            }

            @Override
            public void totalItemCountChanged(ShopCartModel data) {
               // updateShopCartModel = data;
                new Task(UPDATE_ITEM,data).execute();
            }
        });
        mRefreshListView.setAdapter(mAdapter);
    }

    private void resetCount() {
        int count = 0;
        double amount = 0;
        for (ShopCartModel m : mAdapter.datas) {
            if (m.isSelect()) {
                count += m.getQty();
                amount += m.getQty() * m.getPrice();
            }
        }
        txt2.setText("共计:¥" + Utils.moneyFormat(amount));
        txtSummary.setText("共" + count + "件");
    }

    private void resetCount(ShopCartModel shopCartModel) {
        int count = 0;
        double amount = 0;
        if (mAdapter != null) {
            if (!ListUtils.isEmpty(mAdapter.datas)) {

                for (ShopCartModel m : mAdapter.datas) {
                    if (shopCartModel!=null){
                        if (shopCartModel.getItemID()==m.getItemID())
                            m.setQty(shopCartModel.getQty());
                    }
                    if (m.isSelect()) {
                        count += m.getQty();
                        amount += m.getQty() * m.getPrice();
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
        txt2.setText("共计:¥" + Utils.moneyFormat(amount));
        txtSummary.setText("共" + count + "件");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SallerActivity.RequestCode_01) {
                if (data != null) {
                    ProdectBean.SellerUsersBean sellerUsersBean = (ProdectBean.SellerUsersBean) data.getSerializableExtra(SallerActivity.Extra_Bean);
                    if (sellerUsersBean != null) {
                        if (tv_saller_name != null)
                            tv_saller_name.setText(sellerUsersBean.getName());
                    }
                }
            } else if (requestCode == REQUEST_OPEN_CAMERA) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                new Task(Step.ScanShopCart, scanResult).execute();
            } else if (requestCode == REQUEST_OPEN_CAMERA_MEMBER) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (tv_member != null)
                    tv_member.setText(scanResult);
            }
        }
    }

    String ShoppingIDS;
    private double ss_money;
    private double zk;
    private double payment_money;
    private double product_money;
    private int product_count;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_saller:
                if (!ListUtils.isEmpty(sList)) {
                    Intent sintent = new Intent(this, SallerActivity.class);
                    sintent.putExtra(SallerActivity.Extra_List, (Serializable) sList);
                    startActivityForResult(sintent, SallerActivity.RequestCode_01);
                } else {
                    ViewHub.showLongToast(vThis, "列表为空");
                }
                break;
            case R.id.iv_right_01:
                //扫描
                Intent openCameraIntent = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);
                break;
            case R.id.rl_member
                    :
                Intent openCameraIntent2 = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent2, REQUEST_OPEN_CAMERA_MEMBER);
                break;
            case R.id.iv_right_02:
                Intent intent2 = new Intent(vThis, CommonListActivity.class);
                intent2.putExtra("type", 1);
                startActivity(intent2);
                break;
            case R.id.btn_reload:
                new Task(Step.LOAD_DATA).execute();
                break;
            case android.R.id.button1:// 结算
            {

                List<ShopCartModel> selecteds = new ArrayList<>();
                for (ShopCartModel m : mAdapter.datas) {
                    if (m.isSelect()) {
                        selecteds.add(m);
                    }
                }
                if (selecteds.isEmpty()) {
                    ViewHub.showShortToast(getApplicationContext(), getString(R.string.select_nothing));
                    return;
                }
                double t = 0;
                int c = 0;
                String shopcartIDS = "";
                for (ShopCartModel m : selecteds) {
                    shopcartIDS += "," + m.getID();
                    t += m.getQty() * m.getPrice();
                    c += m.getQty();
                }
                if (shopcartIDS.length() > 0) {
                    ShoppingIDS = shopcartIDS.substring(1);
                }
                zk = 0;
                product_count = c;
                payment_money = t;
                product_money = t;
                ss_money = product_money;
                new Task(Step.Create_Order_Preview).execute();
                //  Intent intent = new Intent(vThis, PrintOrderActivity.class);
//                intent.putExtra("datas", (Serializable) selecteds);
//                startActivity(intent);
                break;
            }
            case android.R.id.button2:// 删除
            {
                ViewHub.showOkDialog(this, "提示", "您确定要删除所选商品吗？", getString(android.R.string.ok),
                        getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Task(Step.DELETE_ITEMS).execute();
                            }
                        });
                break;
            }
            case android.R.id.checkbox:
                for (ShopCartModel m : mAdapter.datas) {
                    m.setSelect(mCbSelectAll.isChecked());
                }
                mAdapter.notifyDataSetChanged();
                resetCount();
                break;
            case R.id.iv_scroll_top:
                mRefreshListView.setSelection(0);
                break;
            default:
                break;
        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;
        private String qrStr;
        private ShopCartModel shopCartModel;

        public Task(Step step) {
            mStep = step;
        }

        public Task(Step step, ShopCartModel shopCartModel) {
            mStep = step;
            this.shopCartModel = shopCartModel;
        }

        public Task(Step step, String qrStr) {
            mStep = step;
            this.qrStr = qrStr;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case LOAD_DATA:
                    mloadingDialog.start("加载数据中");
                    break;
                case UPDATE_ITEM:
                    mloadingDialog.start("入库中...");
                    break;
                case DELETE_ITEMS:
                    mloadingDialog.start("删除中...");
                    break;
                case Create_Order_Preview:
                    mloadingDialog.start("创建订单中...");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case ScanShopCart:
                        KdbAPI.addFromCode(vThis, qrStr);
                        return "OK";
                    case Create_Order_Preview:
                        return KdbAPI.createOrderPreview(vThis, ShoppingIDS, 0 + "", 0 + "");
                    case LOAD_DATA:// 加载商品信息
                        ProdectBean bean = KdbAPI.getShopCart(vThis);
                        return bean;
                    case UPDATE_ITEM:
                        KdbAPI.getInstance().updateShopCart(vThis, shopCartModel);
                        return "OK";
                    case DELETE_ITEMS:
                        String selecteds = "";
                        for (ShopCartModel m : mAdapter.datas) {
                            if (m.isSelect()) {
                                selecteds += "," + m.getID();
                            }
                        }
                        if (selecteds.length() > 0) {
                            selecteds = selecteds.substring(1);
                        }
                        if (selecteds.length() <= 0) {
                            ViewHub.showShortToast(getApplicationContext(), getString(R.string.select_nothing));
                        }

                        String delStr = selecteds;
                        KdbAPI.getInstance().deleteShopCart(vThis, delStr);
                        return "OK";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mloadingDialog.isShowing()) {
                mloadingDialog.stop();
            }
            mRefreshListView.onRefreshComplete();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(vThis, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case ScanShopCart:
                        onRefresh();
                        break;
                    case Create_Order_Preview:
                        if (result instanceof CreateProduceBean) {
                            CreateProduceBean bean = (CreateProduceBean) result;
                            if (bean != null) {
                                Intent intent = new Intent(vThis, PrintOrderActivity.class);
                                intent.putExtra(PrintOrderActivity.Extra_ShoppingIDS, ShoppingIDS);
                                intent.putExtra(PrintOrderActivity.Extra_Bean, bean);
                                startActivity(intent);
                            } else {
                                ViewHub.showLongToast(vThis, "获取数据为空");
                            }
                        }
                        break;
                    case LOAD_DATA:
                        if (result instanceof ProdectBean) {
                            ProdectBean bean = (ProdectBean) result;
                            if (bean != null) {
                                datas = bean.getProducts();
                                sList = bean.getSellerUsers();
                                judeSaller(sList);
                            }
                            if (datas == null) {
                                mAdapter.datas.clear();
                            } else {
                                for (ShopCartModel m : datas) {
                                    m.setSelect(true);
                                }
                                mAdapter.setData(datas);
                                mAdapter.notifyDataSetChanged();

                                mBottomView.setVisibility(datas.size() > 0 ? View.VISIBLE : View.INVISIBLE);
                                nBottomLine.setVisibility(datas.size() > 0 ? View.VISIBLE : View.INVISIBLE);
                                mCbSelectAll.setChecked(true);
                                resetCount();
                            }
                            setEmpty_view();
                        }
                        break;
                    case UPDATE_ITEM:
                        ViewHub.showLongToast(vThis, "修改成功");
                        resetCount(shopCartModel);
                        break;
                    case DELETE_ITEMS:
                        new Task(Step.LOAD_DATA).execute();
                        break;
                }
            }
        }
    }

    private void judeSaller(List<ProdectBean.SellerUsersBean> list) {
        if (!ListUtils.isEmpty(list)) {
            layout_saller.setVisibility(View.VISIBLE);
            int SellerUserId = SpManager.getSellerUsersId(this);
            int firstId = 0;
            String first_name = "";
            boolean hasSellerId = false;
            for (int i = 0; i < list.size(); i++) {
                ProdectBean.SellerUsersBean bean = list.get(i);
                if (bean != null) {
                    if (i == 0) {
                        firstId = bean.getID();
                        first_name = bean.getName();
                    }
                    if (SellerUserId == bean.getID()) {
                        hasSellerId = true;
                        break;
                    }
                }
            }
            if (hasSellerId) {
                if (tv_saller_name != null)
                    tv_saller_name.setText(SpManager.getSellerName(vThis));
            } else {
                SpManager.setSellerUsersId(vThis, firstId);
                SpManager.setSellerName(vThis, first_name);
                if (tv_saller_name != null)
                    tv_saller_name.setText(first_name);
            }
        } else {
            layout_saller.setVisibility(View.GONE);
        }

    }

    private void setEmpty_view() {
        if (mAdapter != null) {
            if (ListUtils.isEmpty(mAdapter.datas)) {
                empty_view.setVisibility(View.VISIBLE);
            } else {
                empty_view.setVisibility(View.GONE);
            }
        }
    }

}
