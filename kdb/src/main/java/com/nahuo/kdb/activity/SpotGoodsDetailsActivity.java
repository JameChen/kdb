package com.nahuo.kdb.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.PackageDetailAdapter;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.dialog.CommDialog;
import com.nahuo.kdb.model.PackageDetailBean;
import com.nahuo.kdb.model.ScanQrcodeModel;
import com.nahuo.kdb.zxing.activity.CaptureActivity;
import com.nahuo.library.controls.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nahuo.kdb.R.id.iv_right_01;

public class SpotGoodsDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private SpotGoodsDetailsActivity Vthis = this;
    private LoadingDialog mloadingDialog;
    private RecyclerView recyclerView;
    private int packageID;
    public static String EXTRA_PACKAGEID = "EXTRA_PACKAGEID";
    private TextView tv_count, tv_warehousing;
    private PackageDetailAdapter adapter;
    private List<PackageDetailBean.ItemsBean> data = new ArrayList<>();
    private int total;
    private static final int REQUEST_OPEN_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_goods_details);
        BWApplication.addActivity(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvTitleCenter)).setText("发货单点货");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PackageDetailAdapter(Vthis);
        adapter.setEditChangQty(new PackageDetailAdapter.EditChangQty() {
            @Override
            public void OnEditQty() {
                changeQty();
            }
        });
        recyclerView.setAdapter(adapter);
        ImageView iv_right_01 = (ImageView) findViewById(R.id.iv_right_01);
        iv_right_01.setImageResource(R.drawable.sweep_code_white);
        iv_right_01.setVisibility(View.VISIBLE);
        iv_right_01.setOnClickListener(this);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_warehousing = (TextView) findViewById(R.id.tv_warehousing);
        tv_warehousing.setOnClickListener(this);
        if (getIntent() != null)
            packageID = getIntent().getIntExtra(this.EXTRA_PACKAGEID, 0);
        new Task(Step.GetPackageList).execute();
    }

    private void changeQty() {
        if (!ListUtils.isEmpty(data)) {
            int qty = 0;
            for (PackageDetailBean.ItemsBean itemsBean : data) {
                if (!ListUtils.isEmpty(itemsBean.getProducts())) {
                    for (PackageDetailBean.ItemsBean.ProductsBean productsBean : itemsBean.getProducts()) {
                        qty += productsBean.getArrivalQty();
                    }
                }
            }
            if (tv_count != null) {
                tv_count.setText("发货数量" + total + "件，实收数量" + qty + "件");
            }
        } else {
            if (tv_count != null) {
                tv_count.setText("发货数量" + total + "件，实收数量件");
            }
        }
    }

    public enum Step {
        GetPackageList, Warehousing
    }

    public class Task extends AsyncTask<String, Integer, Object> {
        private Step step;
        private List<PackageDetailBean.ItemsBean> list;

        Task(Step step) {
            this.step = step;
        }

        Task(Step step, List<PackageDetailBean.ItemsBean> list) {
            this.step = step;
            this.list = list;
        }


        @Override
        protected Object doInBackground(String... params) {
            switch (step) {
                case Warehousing:
                    try {
                        strSaveJson = "";
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject1 = new JSONObject();
                        JSONArray jsonArray1 = new JSONArray();
                        for (PackageDetailBean.ItemsBean itemsBean : list) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SourceID", itemsBean.getSourceID());
                            if (!ListUtils.isEmpty(itemsBean.getProducts())) {
                                for (PackageDetailBean.ItemsBean.ProductsBean productsBean : itemsBean.getProducts()) {
                                    JSONObject jsonObjectx = new JSONObject();
                                    jsonObjectx.put("Color", productsBean.getColor());
                                    jsonObjectx.put("Size", productsBean.getSize());
                                    jsonObjectx.put("Qty", productsBean.getArrivalQty());
                                    if (productsBean.getArrivalQty() > 0)
                                        jsonArray1.put(jsonObjectx);

                                }
                                jsonObject.put("Products", jsonArray1);
                            }
                            jsonArray.put(jsonObject);
                        }
                        jsonObject1.put("items", jsonArray);
                        jsonObject1.put("PackageID", packageID);
                        strSaveJson = jsonObject1.toString();
                        KdbAPI.SaveInStock(Vthis, strSaveJson);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "error:" + e.getMessage();
                    }
                    break;
                case GetPackageList:
                    try {
                        return KdbAPI.getPackageDetail(Vthis, packageID);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "error:" + e.getMessage();
                    }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog = new LoadingDialog(Vthis);
            switch (step) {
                case GetPackageList:
                    if (mloadingDialog != null)
                        mloadingDialog.start("加载数据....");
                    break;
                case Warehousing:
                    if (mloadingDialog != null)
                        mloadingDialog.start("入库中....");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (mloadingDialog.isShowing()) {
                mloadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(Vthis, ((String) result).replace("error:", ""));
                return;
            }
            switch (step) {
                case Warehousing:
                    ViewHub.showOkDialog(Vthis, "提示", "入库完成", "知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    break;
                case GetPackageList:
                    if (result instanceof PackageDetailBean) {
                        PackageDetailBean bean = (PackageDetailBean) result;
                        data.clear();
                        if (bean != null) {
                            total = bean.getTotalShipQty();
                            if (tv_count != null) {
                                tv_count.setText("发货数量" + total + "件，实收数量0件");
                            }
                            if (!ListUtils.isEmpty(bean.getItems())) {
//                                for (int i=0;i< 200;i++) {
//                                    PackageDetailBean.ItemsBean bean1=new PackageDetailBean.ItemsBean();
//                                    PackageDetailBean.ItemsBean.ProductsBean productsBean=new PackageDetailBean.ItemsBean.ProductsBean();
//                                    productsBean.setSourceID(i);
//                                    productsBean.setShipQty(5);
//                                    PackageDetailBean.ItemsBean.ProductsBean productsBean2=new PackageDetailBean.ItemsBean.ProductsBean();
//                                    productsBean2.setSourceID(i);
//                                    productsBean2.setShipQty(5);
//                                    PackageDetailBean.ItemsBean.ProductsBean productsBean3=new PackageDetailBean.ItemsBean.ProductsBean();
//                                    productsBean3.setSourceID(i);
//                                    productsBean3.setShipQty(5);
//                                    List<PackageDetailBean.ItemsBean.ProductsBean> list=new ArrayList<>();
//                                    list.add(productsBean);
//                                    bean1.setProducts(list);
//                                    bean1.setSourceID(i);
//                                    data.add(bean1);
//                                }
                                data.addAll(bean.getItems());
                                for (PackageDetailBean.ItemsBean itemsBean : data) {
                                    if (!ListUtils.isEmpty(itemsBean.getProducts())) {
                                        for (PackageDetailBean.ItemsBean.ProductsBean productsBean : itemsBean.getProducts()) {
                                            productsBean.setSourceID(itemsBean.getSourceID());
                                        }
                                    }
                                }
                            }
                        }
                        adapter.setNewData(data);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                try {
                    new ScanQrCodeTask(scanResult).execute();
                } catch (Exception e) {
                    ViewHub.showShortToast(Vthis, "扫码解析异常");
                }

            }
        }
    }

    private ScanQrcodeModel qrcodeItemData;

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
                    RCQrcodeItemLoaded();
                } else {
                    ViewHub.showLongToast(Vthis, result.toString());
                }
            }
            mloadingDialog.stop();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                qrcodeItemData = KdbAPI.getScanRecord(Vthis, qrStr);
                return "OK";
            } catch (Exception ex) {
                // Log.e(TAG, "获取qrcode异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

    }

    List<String> color_size = new ArrayList<>();

    private void RCQrcodeItemLoaded() {
        if (qrcodeItemData != null) {
            qrcodeItemData.setColor_Size(qrcodeItemData.getItemID() + "/" + qrcodeItemData.getColor() + "/" + qrcodeItemData.getSize());
            if (qrcodeItemData.getMaxQty() <= 0) {
                ViewHub.showLongToast(Vthis, qrcodeItemData.getName() + "颜色：" + qrcodeItemData.getColor() +
                        "尺寸：" + qrcodeItemData.getSize() +
                        "可入库数为0");
                return;
            }
            if (!ListUtils.isEmpty(data)) {
                for (PackageDetailBean.ItemsBean bean : data) {
                    if (!ListUtils.isEmpty(bean.getProducts())) {
                        for (PackageDetailBean.ItemsBean.ProductsBean productsBean : bean.getProducts()) {
                            color_size.add(productsBean.getColor_Size());
                            if (qrcodeItemData.getColor_Size().equals(productsBean.getColor_Size())) {
                                if (productsBean.getArrivalQty() + 1 > productsBean.getShipQty()) {
                                    ViewHub.showShortToast(Vthis, "实到数不能大于可发货数");
                                    return;
                                } else {
                                    productsBean.setArrivalQty(productsBean.getArrivalQty() + 1);
                                }
//                                if (bean.getDhQty() == bean.getMaxQty()) {
//                                    ViewHub.showShortToast(vThis, "到货数不能大于可入库数");
//                                    return;
//                                } else {
//                                    int qty = bean.getDhQty();
//                                    bean.setDhQty(qty + 1);
//                                }
                            }
                        }
                    }

                }
//                if (!color_size.contains(qrcodeItemData.getColor_Size())) {
//                    mList.add(qrcodeItemData);
//                }
            }
//            if (mList == null || mList.size() == 0) {
//                mList.add(qrcodeItemData);
//            }
            if (adapter != null)
                adapter.notifyDataSetChanged();
            changeQty();
        }


    }

    String strSaveJson;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_warehousing:
                if (ListUtils.isEmpty(data)) {
                    ViewHub.showLongToast(Vthis, "没有货物入库");
                } else {
                    boolean isHasOut = false;
                    boolean isHasQty = false;
                    try {
                        for (PackageDetailBean.ItemsBean itemsBean : data) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("SourceID", itemsBean.getSourceID());
                            if (!ListUtils.isEmpty(itemsBean.getProducts())) {
                                for (PackageDetailBean.ItemsBean.ProductsBean productsBean : itemsBean.getProducts()) {
                                    if (productsBean.getArrivalQty() > 0) {
                                        isHasQty = true;
                                    }
                                    if (productsBean.getArrivalQty() != productsBean.getShipQty()) {
                                        isHasOut = true;
                                    }
                                }
                            }
                        }
                        if (!isHasQty) {
                            ViewHub.showLongToast(Vthis, "请输入实到数量");
                            return;
                        }
                        if (isHasOut) {
                            CommDialog.getInstance(Vthis).setcTitle("提示").setLeftStr("放弃").setRightStr("是的")
                                    .setMessage("发货数量和到货数量不一致，确认强制入库").setPositive(new CommDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int ok_cancel, CommDialog.DialogType type) {
                                    if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                                        new Task(Step.Warehousing, data).execute();
                                    }
                                }
                            }).showDialog();
                        } else {
                            new Task(Step.Warehousing, data).execute();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.tvTLeft:
                finish();
                break;
            case iv_right_01:
                Intent openCameraIntent = new Intent(Vthis, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);
                break;
        }
    }
}
