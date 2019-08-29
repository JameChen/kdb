package com.nahuo.kdb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.CommonListAdapterx;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.eventbus.BusEvent;
import com.nahuo.kdb.eventbus.EventBusId;
import com.nahuo.kdb.model.ScanQrcodeModel;
import com.nahuo.kdb.zxing.activity.CaptureActivity;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ScanRecordActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ScanRecordActivity";
    public Context vThis;
    private static final int REQUEST_OPEN_CAMERA = 1;
    private LoadingDialog mloadingDialog;
    private ScanQrcodeModel qrcodeItemData;
    private CommonListAdapterx adapter;
    public List<ScanQrcodeModel> mList = new ArrayList<>();
    private PullToRefreshListView listView;
    private TextView tv_batch_warehousing, tv_count;
    private ImageButton tv_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_record);
        initView();
    }

    public void initView() {
        vThis = this;
        mloadingDialog = new LoadingDialog(vThis);
        tv_batch_warehousing = (TextView) findViewById(R.id.tv_batch_warehousing);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_add = (ImageButton) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
        tv_batch_warehousing.setOnClickListener(this);
        findViewById(R.id.titlebar_btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.titlebar_btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);
            }
        });
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setCanLoadMore(false);
        listView.setCanRefresh(false);
        adapter = new CommonListAdapterx(this, mList, tv_count);
        listView.setAdapter(adapter);

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
                    ViewHub.showShortToast(vThis, "扫码解析异常");
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_batch_warehousing:
                int count = 0;
                for (ScanQrcodeModel p : mList) {
                    count += p.getDhQty();
                }
                if (count == 0) {
                    if (mList.size() == 0) {
                        ViewHub.showLongToast(vThis, "请扫描商品");
                    } else {
                        ViewHub.showLongToast(vThis, "请选择商品数量");
                    }
                } else {
                    ViewHub.showOkDialog(vThis, "提示", "确定要入库" + count + "件？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Task().execute();
                        }
                    });
                }

                break;
            case R.id.tv_add:
                Intent openCameraIntent = new Intent(vThis, CaptureActivity.class);
                startActivityForResult(openCameraIntent, REQUEST_OPEN_CAMERA);
                break;
        }
    }


    private class Task extends AsyncTask<Object, Void, Object> {

        public Task() {

        }

        @Override
        protected void onPreExecute() {

            mloadingDialog.start("入库中...");

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                KdbAPI.getInstance().ScanSingleInStock(vThis, mList);
                return "OK";

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mloadingDialog.isShowing()) {
                mloadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(vThis, ((String) result).replace("error:", ""));
            } else {
                ViewHub.showLongToast(vThis, "入库成功");
                finish();
                EventBus.getDefault().post(BusEvent.getEvent(EventBusId.SEARCH_入库));
            }

        }
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
                    RCQrcodeItemLoaded();
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

    List<String> color_size = new ArrayList<>();

    private void RCQrcodeItemLoaded() {
        if (qrcodeItemData != null) {
            qrcodeItemData.setColor_Size(qrcodeItemData.getItemID() + "/" + qrcodeItemData.getColor() + "/" + qrcodeItemData.getSize());
            if (qrcodeItemData.getMaxQty() <= 0) {
                ViewHub.showLongToast(vThis, qrcodeItemData.getName() + "颜色：" + qrcodeItemData.getColor() +
                        "尺寸：" + qrcodeItemData.getSize() +
                        "可入库数为0");
                return;
            }
            tv_add.setVisibility(View.GONE);
            if (mList != null && mList.size() >= 1) {
                for (ScanQrcodeModel bean : mList) {
                    color_size.add(bean.getColor_Size());
                    if (qrcodeItemData.getColor_Size().equals(bean.getColor_Size())) {
                        if (bean.getDhQty() == bean.getMaxQty()) {
                            ViewHub.showShortToast(vThis, "到货数不能大于可入库数");
                            return;
                        } else {
                            int qty = bean.getDhQty();
                            bean.setDhQty(qty + 1);
                        }
                    }
                }
                if (!color_size.contains(qrcodeItemData.getColor_Size())) {
                    mList.add(qrcodeItemData);
                }
            }
            if (mList == null || mList.size() == 0) {
                mList.add(qrcodeItemData);
            }
            adapter.notifyDataSetChanged();
        }


    }
}
