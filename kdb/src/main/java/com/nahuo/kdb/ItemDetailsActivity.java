package com.nahuo.kdb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.nahuo.kdb.activity.PrintActivity;
import com.nahuo.kdb.adapter.DetailProductAdapter;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.dialog.EditInventoryDialog;
import com.nahuo.kdb.eventbus.BusEvent;
import com.nahuo.kdb.eventbus.EventBusId;
import com.nahuo.kdb.model.PrintBean;
import com.nahuo.kdb.model.ProductModel;
import com.nahuo.kdb.model.ShopItemListModel;
import com.nahuo.kdb.model.StoageDetailBean;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.library.helper.SDCardHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class ItemDetailsActivity extends Activity implements View.OnClickListener {

    public static String downloadDirPath = SDCardHelper.getSDCardRootDirectory()
            + "/kdb/share";
    private Context mContext = this;
    private LoadingDialog mDialog;
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_QSID = "EXTRA_QSID";
    private DecimalFormat df = new DecimalFormat("#0.00");
    private TextView mTitle, mText1, mText5, mRemarkTv, mText2, mText3, item_pre, mText4, mBtnGreen, item_right, tv_count, tv_ok;
    private EditText mTitle_ET, mText1_ET;
    private View mText1_View;
    private TextView mText1_TEXT;
    private ImageView mCover;
    private ListView mSizeColorList;
    private ArrayList<ProductModel> colorSizeData;
    private int mId, mQsID;

    private ShopItemListModel mShopItem;
    private CommonListActivity.ListType mType;
    private DetailProductAdapter productAdapter;
    private View rl_inventory;
    private StoageDetailBean bean;

    private enum Step {
        LOAD_ITEM_INFO, SUBMIT_RK, ADD_SHOPCART, SUBMIT_KC, EDIT_KC_ITEM_INFO, SaveSkuStock
    }

    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private TextView item_detail_print;
    private int totalQty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        BWApplication.addActivity(this);
        connection();
        initExtras();
        initView();
        loadData();
    }

    private void initExtras() {
        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_ID, -1);
        mQsID = intent.getIntExtra(EXTRA_QSID, -1);

        mType = (CommonListActivity.ListType) getIntent().getSerializableExtra("type");
        mShopItem = (ShopItemListModel) getIntent().getSerializableExtra("shopItem");
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
    }


    private void loadData() {
        // 加载商品信息
        new Task(Step.LOAD_ITEM_INFO).execute();
    }

    private void initView() {
        initTitleBar();
        item_detail_print = (TextView) findViewById(R.id.item_detail_print);
        item_detail_print.setText("打印");
        item_detail_print.setOnClickListener(this);
        item_detail_print.setVisibility(View.GONE);
        rl_inventory = findViewById(R.id.rl_inventory);
        mCover = (ImageView) findViewById(R.id.item_cover);
        mCover.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.item_title);
        item_right = (TextView) findViewById(R.id.item_right);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        mTitle_ET = (EditText) findViewById(R.id.item_title_et);
        mTitle_ET.setFocusable(false);
        mTitle_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SpManager.getEditTitle(mContext)) {
                    return;
                }
                if (mShopItem == null) {
                    return;
                }

                ViewHub.showEditDialog(mContext, "修改标题", mShopItem.getName(), new ViewHub.EditDialogListener() {
                    @Override
                    public void onOkClick(DialogInterface dialog, EditText editText) {
                        mShopItem.setName(editText.getText().toString());
                        ViewHub.setDialogDismissable(dialog, true);
                        new Task(Step.EDIT_KC_ITEM_INFO).execute();

                    }

                    @Override
                    public void onOkClick(EditText editText) {

                    }
                });
            }
        });
        mText1 = (TextView) findViewById(R.id.item_text_1);
        mText5 = (TextView) findViewById(R.id.item_text_5);
        mText5.setVisibility(View.GONE);
        mText1_ET = (EditText) findViewById(R.id.item_text_1_et);
        item_pre = (TextView) findViewById(R.id.item_pre);
        mText1_ET.setFocusable(false);
        mText1_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SpManager.getEditPrice(mContext))
                    return;
                if (mShopItem == null) {
                    return;
                }

                ViewHub.showEditDialog(mContext, "修改零售价", mShopItem.getRetailPrice() + "", new ViewHub.EditDialogListener() {
                    @Override
                    public void onOkClick(DialogInterface dialog, EditText editText) {
                        String t = editText.getText().toString();
                        double price = 0.0;
                        try {
                            price = Double.parseDouble(t);
                        } catch (Exception ex) {

                        }
                        mShopItem.setRetailPrice(price);
                        ViewHub.setDialogDismissable(dialog, true);
                        new Task(Step.EDIT_KC_ITEM_INFO).execute();
                    }

                    @Override
                    public void onOkClick(EditText editText) {

                    }
                });
            }
        });
        mText1_View = (View) findViewById(R.id.item_text_1_et_view);
        mText1_TEXT = (TextView) findViewById(R.id.item_text_1_txt);
        mText2 = (TextView) findViewById(R.id.item_text_2);
        mText3 = (TextView) findViewById(R.id.item_text_3);
        mText4 = (TextView) findViewById(R.id.item_text_4);
        mSizeColorList = (ListView) findViewById(R.id.item_detail_sizecolor_list);
        mBtnGreen = (TextView) findViewById(R.id.item_detail_green_btn);
        mBtnGreen.setOnClickListener(this);

        mDialog = new LoadingDialog(this);
        switch (mType) {
            case 入库:
                mBtnGreen.setVisibility(View.VISIBLE);
                mBtnGreen.setText("确认入库");
                setTitle("入库");
                rl_inventory.setVisibility(View.GONE);
                item_right.setVisibility(View.GONE);
                break;
            case 销售:
                mBtnGreen.setVisibility(View.VISIBLE);
                mBtnGreen.setText("添加到购物车");
                setTitle("销售");
                rl_inventory.setVisibility(View.GONE);
                item_right.setVisibility(View.GONE);
                break;
            case 库存:
//                mBtnGreen.setVisibility(View.VISIBLE);
                rl_inventory.setVisibility(View.VISIBLE);
                mBtnGreen.setVisibility(View.GONE);
//                mBtnGreen.setText("修改");
                setTitle("库存");
                mTitle.setVisibility(View.GONE);
                mTitle_ET.setVisibility(View.GONE);
                mText1.setVisibility(View.GONE);
                mText1_ET.setVisibility(View.GONE);
                mText1_TEXT.setVisibility(View.GONE);
                mText1_View.setVisibility(View.GONE);
                mText2.setVisibility(View.GONE);
                item_right.setVisibility(View.VISIBLE);
                break;
            default:
                setTitle("");
                break;
        }
    }

    private void initTitleBar() {
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        switch (mType) {
            case 入库:
                setTitle("入库");
                break;
            case 销售:
                setTitle("销售");
                break;
            case 库存:
                setTitle("库存");
                break;
            default:
                setTitle("");
                break;
        }
    }

    private void showRight() {
        ImageView btnRight = (ImageView) findViewById(R.id.titlebar_icon_loading);
        btnRight.setImageResource(R.drawable.share);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(this);
    }

    private void setTitle(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(text);
    }

    int SourceID;
    String remark;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_detail_print:
                if (bean != null) {
                    List<PrintBean> data = new ArrayList<>();
                    if (!ListUtils.isEmpty(bean.getColorSize())) {
                        for (ProductModel model : bean.getColorSize()) {
                            if (model.getInventoryQty()>0) {
                                for (int i=0;i<model.getInventoryQty();i++) {
                                    PrintBean printBean = new PrintBean();
                                    printBean.setColor(model.getColor());
                                    printBean.setSize(model.getSize());
                                    printBean.setScan(model.getSKU());
                                    printBean.setCategroy(bean.getCategroy());
                                    printBean.setCode("#" + bean.getCode() + "#");
                                    printBean.setRetailPrice(bean.getRetailPrice());
                                    data.add(printBean);
                                }
                            }
                        }
                        Intent intent = new Intent(mContext,PrintActivity.class);
                        intent.putExtra(PrintActivity.ETRA_PRINT_LIST_BEAN, (Serializable) data);
                        mContext.startActivity(intent);
                    } else {
                        ViewHub.showLongToast(mContext, "商品数据为空，不能打印");
                    }

                } else {
                    ViewHub.showLongToast(mContext, "商品数据为空，不能打印");
                }
                break;
            case R.id.tv_ok:
                if (bean != null) {
                    if (!ListUtils.isEmpty(bean.getColorSize())) {
//                        boolean isHasInventory=false;
//                        for ( ProductModel model:bean.getColorSize()) {
//                            if (model.getInventoryQty()>0) {
//                                isHasInventory = true;
//                                break;
//                            }
//                        }
//                        if (!isHasInventory){
//                            ViewHub.showLongToast(ItemDetailsActivity.this,"请添加盘点数量");
//                        }else
                        {
                            EditInventoryDialog.getInstance(this).setPositive(new EditInventoryDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int ok_cancel, String txt) {
                                    if (ok_cancel == EditInventoryDialog.BUTTON_POSITIVIE) {
                                        if (TextUtils.isEmpty(txt)) {
                                            ViewHub.showLongToast(ItemDetailsActivity.this, "请输入备注");
                                        } else {
                                            remark = txt;
                                            new Task(Step.SaveSkuStock, bean.getColorSize()).execute();
                                        }
                                    }
                                }
                            }).showDialog();
                        }
                    } else {
                        ViewHub.showLongToast(ItemDetailsActivity.this, "沒有商品数量");
                    }
                } else {
                    ViewHub.showLongToast(ItemDetailsActivity.this, "沒有商品");
                }
                break;
            case R.id.item_cover:
                Intent intent = new Intent(mContext, PicGalleryActivity.class);
                intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                ArrayList<String> arr = new ArrayList<>();
                arr.add(mShopItem.getCover());
                intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, arr);
                startActivity(intent);
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.item_detail_green_btn:// 绿色按钮点击
                try {
                    String text = ((TextView) v).getText().toString();
                    if (text == "确认入库") {
                        //获取入库件数
                        int count = 0;
                        for (ProductModel p : colorSizeData) {
                            count += p.getDHQty();
                        }
                        if (count == 0) {
                            ViewHub.showLongToast(mContext, "请选择购买数量");
                        } else {
                            ViewHub.showOkDialog(mContext, "提示", "确定要入库" + count + "件？", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Task(Step.SUBMIT_RK).execute();
                                }
                            });
                        }
                    }
                    if (text == "添加到购物车") {
                        new Task(Step.ADD_SHOPCART).execute();
                    }
                    if (text == "修改") {
                        new Task(Step.SUBMIT_KC).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;
        private ArrayList<ProductModel> data;

        public Task(Step step) {
            mStep = step;
        }

        public Task(Step step, ArrayList<ProductModel> data) {
            mStep = step;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case SaveSkuStock:
                    if (mDialog != null)
                        mDialog.start("保存中...");
                    break;
                case LOAD_ITEM_INFO:
                    if (mDialog != null)
                        mDialog.start("加载数据中");
                    break;
                case SUBMIT_RK:
                    if (mDialog != null)
                        mDialog.start("入库中...");
                    break;
                case ADD_SHOPCART:
                    if (mDialog != null)
                        mDialog.start("添加到购物车中...");
                    break;
                case SUBMIT_KC:
                case EDIT_KC_ITEM_INFO:
                    if (mDialog != null)
                        mDialog.start("修改中...");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case SaveSkuStock:
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SourceID", SourceID);
                            jsonObject.put("Remark", remark);
                            JSONArray jsonArray = new JSONArray();
                            for (ProductModel model : data) {
                                JSONObject cJ = new JSONObject();
                                cJ.put("Color", model.getColor());
                                cJ.put("Size", model.getSize());
                                cJ.put("Qty", model.getInventoryQty());
//                                if (model.getInventoryQty() > 0)
                                jsonArray.put(cJ);
                            }
                            jsonObject.put("Products", jsonArray);
                            KdbAPI.SaveSkuStock(mContext, jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "error:" + e.getMessage();
                        }
                        break;
                    case LOAD_ITEM_INFO:// 加载商品信息
                        Object object = KdbAPI.getInstance().getDetail(mContext, mType, mId, mQsID);
                        if (object instanceof ArrayList) {
                            colorSizeData = (ArrayList<ProductModel>) object;
                        } else if (object instanceof StoageDetailBean) {
                            bean = (StoageDetailBean) object;
                            if (bean != null) {
                                colorSizeData = bean.getColorSize();
                            }
                        }
                        if (!ListUtils.isEmpty(colorSizeData)) {
                            for (ProductModel pm : colorSizeData) {
                                pm.setAgentItemId(mShopItem.getAgentItemId());
                                pm.setInventoryQty(pm.getQty());
                            }
                        }
                        return "OK";
                    case SUBMIT_RK:
                        // KdbAPI.getInstance().submitRK(mContext, productAdapter.mList);
                        KdbAPI.getInstance().SaveSingleInStock(mContext, productAdapter.mList);
                        return "OK";
                    case ADD_SHOPCART:
                        KdbAPI.getInstance().addShopCart(mContext, mShopItem, productAdapter.mList);
                        return "OK";
                    case SUBMIT_KC:
                        KdbAPI.getInstance().submitKC(mContext, mShopItem, productAdapter.mList);
                        return "OK";
                    case EDIT_KC_ITEM_INFO:
                        KdbAPI.getInstance().editItem(mContext, mShopItem);
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
            if (mDialog.isShowing()) {
                mDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case SaveSkuStock:
                        ViewHub.showOkDialog(mContext, "提示", "盘点完成", "知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        break;
                    case LOAD_ITEM_INFO:
                        onItemInfoLoaded();
                        break;
                    case SUBMIT_RK:
                        ViewHub.showLongToast(mContext, "入库成功");
                        finish();
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_入库));
                        break;
                    case ADD_SHOPCART:
                        ViewHub.showOkDialog(mContext, "提示", "已成功添加到购物车", "继续添加", "开单", new ViewHub.OkDialogListener() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog, int which) {
                                finish();
                                EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_销售));
                            }

                            @Override
                            public void onNegativeClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, ShopCartActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                    case SUBMIT_KC:
                        ViewHub.showLongToast(mContext, "保存成功");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_库存));
                        break;
                    case EDIT_KC_ITEM_INFO:
                        mTitle_ET.setText(mShopItem.getName());
                        mText1_ET.setText(mShopItem.getRetailPrice() + "");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_库存));
                        break;
                }

            }
        }

    }

    private void onItemInfoLoaded() {
        String url = ImageUrlExtends.getImageUrl(mShopItem.getCover());
        Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(mCover);
        mTitle.setText(mShopItem.getName());
        boolean isShowPurchase = SpManager.getPurchase(mContext);
        boolean isShowRetail = SpManager.getRetail(mContext);
        View mListHeader = null;
        switch (mType) {
            case 入库:

                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor5_work_header, null);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("订货数");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("可入库数");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_5)).setText("到货数");
                mText5.setVisibility(View.VISIBLE);
                mText5.setText(Html.fromHtml("可入库数：" + mShopItem.getCanStockQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;已入数：" + mShopItem.getInStockQty() + "件"));
                mText1.setText(Html.fromHtml("订货数：" + mShopItem.getBookQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;已发数：" + mShopItem.getShipQty() + "件"));
                mText2.setText(Html.fromHtml("进货价：<font color=\"#FF0000\">￥</font>" + mShopItem.getPrice() + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(mShopItem.getBookQty() * mShopItem.getPrice()))));
                mText3.setText(Html.fromHtml("零售价：<font color=\"#FF0000\">￥</font>" + mShopItem.getRetailPrice()));

                //到货数量默认为发货数量
//                for (ProductModel pm : colorSizeData) {
//                   // pm.setShipQty(pm.getStoreQty());
//                    pm.setDHQty(pm.getShipQty());
//                }
                if (isShowPurchase) {
                    if (mText2 != null)
                        mText2.setVisibility(View.VISIBLE);
                } else {
                    if (mText2 != null)
                        mText2.setVisibility(View.GONE);
                }
                if (isShowRetail) {
                    if (mText3 != null)
                        mText3.setVisibility(View.VISIBLE);
                } else {
                    if (mText3 != null)
                        mText3.setVisibility(View.GONE);
                }

                break;
            case 销售:
                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_work_header, null);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("库存");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("购买数");

                mText1.setText(Html.fromHtml("零售价：<font color=\"#FF0000\">￥</font>" + mShopItem.getRetailPrice()));
                mText2.setText(Html.fromHtml("库存：" + mShopItem.getQty() + "件"));
                mText3.setText("");
                if (isShowRetail) {
                    if (mText1 != null)
                        mText1.setVisibility(View.VISIBLE);

                } else {
                    if (mText1 != null)
                        mText1.setVisibility(View.GONE);
                }
                break;
            case 库存:
                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor_ku_work_header, null);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("库存数");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("盘点数量");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_5)).setText("盈亏");
                if (bean != null) {
                    SourceID = bean.getSourceID();
                    item_right.setText(bean.getName() + "(" + bean.getCode() + ")\n库存数量："
                            + bean.getStockQty() + "\n上次盘点：" + bean.getLastStockTime());
                    tv_count.setText("已盘点 0/" + bean.getStockQty());

                    try {
                        if (!ListUtils.isEmpty(bean.getColorSize())) {
                            totalQty = bean.getColorSize().size();
                        }
                        item_detail_print.setVisibility(View.VISIBLE);
                        if (totalQty > 0 && mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                            item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_red_p));
                            item_detail_print.setEnabled(true);
                        } else {
                            item_detail_print.setEnabled(false);
                            item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_gray_p));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                mTitle_ET.setText(mShopItem.getName());
//                mText1_TEXT.setText(Html.fromHtml("进货价：<font color=\"#FF0000\">￥</font>" + mShopItem.getOriPrice()));
//                mText1_ET.setText(mShopItem.getRetailPrice() + "");
//                mText2.setText(Html.fromHtml("库存：" + mShopItem.getQty() + "件"));
//                mText3.setText("");
//                if (isShowPurchase){
//                    if (mText1_TEXT!=null)
//                        mText1_TEXT.setVisibility(View.VISIBLE);
//                }else {
//                    if (mText1_TEXT!=null)
//                        mText1_TEXT.setVisibility(View.GONE);
//                }
//                if (isShowRetail){
//                    if (mText1_ET!=null)
//                        mText1_ET.setVisibility(View.VISIBLE);
//                    if (item_pre!=null)
//                        item_pre.setVisibility(View.VISIBLE);
//                }else {
//                    if (mText1_ET!=null)
//                        mText1_ET.setVisibility(View.GONE);
//                    if (item_pre!=null)
//                        item_pre.setVisibility(View.GONE);
//                }
                break;
            default:
                setTitle("");
                break;
        }
        if (mListHeader != null) {
            mSizeColorList.addHeaderView(mListHeader);
        }
        productAdapter = new DetailProductAdapter(mContext, colorSizeData, mType);
        productAdapter.setListener(new DetailProductAdapter.OnChangeQtyListener() {
            @Override
            public void OnQtyChange() {
                if (mType == CommonListActivity.ListType.库存) {
                    if (!ListUtils.isEmpty(colorSizeData)) {
                        int qty = 0;
                        for (ProductModel bean : colorSizeData) {
                            qty += bean.getInventoryQty();
                        }
                        if (tv_count != null) {
                            tv_count.setText("已盘点 " + qty + "/" + bean.getStockQty());
                        }
                    } else {
                        if (tv_count != null)
                            tv_count.setText("已盘点 0/" + bean.getStockQty());
                    }
                }

            }
        });
        mSizeColorList.setAdapter(productAdapter);
    }
}
