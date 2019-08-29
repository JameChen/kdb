package com.nahuo.kdb.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.CommodityManagementDetailAdapter;
import com.nahuo.kdb.api.OtherAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.CdmDetailBean;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommodityManagementDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = CommodityManagementDetailActivity.class.getSimpleName();
    private CommodityManagementDetailActivity vThis = this;
    public static String ERROR_PREFIX = "error:";
    private LoadingDialog mloadingDialog;
    private RecyclerView recyclerView;
    private TextView tvTitleCenter, tv_content, tv_summary;
    private ImageView iv_icon;
    public static final String EXTRA_SOURCEID = "EXTRA_SOURCEID";
    private int sourceId;
    private CommodityManagementDetailAdapter adapter;
    private  LoadDataTask loadDataTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_management_detail);
        BWApplication.addActivity(this);
        mloadingDialog = new LoadingDialog(vThis);
        tvTitleCenter = (TextView) findViewById(R.id.tvTitleCenter);
        tvTitleCenter.setText("商品详情");
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CommodityManagementDetailAdapter(vThis);
        recyclerView.setLayoutManager(new LinearLayoutManager(vThis));
        recyclerView.setAdapter(adapter);
        if (getIntent() != null) {
            sourceId = getIntent().getIntExtra(vThis.EXTRA_SOURCEID, 0);
        }
       loadDataTask= new LoadDataTask(sourceId);loadDataTask.execute();
    }

    CdmDetailBean bean;
    List<CdmDetailBean.ProductsBean> data = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataTask!=null)
            loadDataTask.cancel(true);
    }

    public class LoadDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;
        private int sourceId;

        public LoadDataTask(int sourceId) {
            this.sourceId = sourceId;
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
            } else if (object instanceof CdmDetailBean) {
                bean = (CdmDetailBean) object;
                if (data != null)
                    data.clear();
                if (bean != null) {
                    if (tv_content != null)
                        tv_content.setText(bean.getName() + "\n款号："
                                + bean.getSku() + "\n首批入库：" + bean.getFirstInstockTime());
                    String url = ImageUrlExtends.getImageUrl(bean.getCover());
                    if (iv_icon != null) {
                        if (TextUtils.isEmpty(url)) {
                            iv_icon.setImageResource(R.drawable.empty_photo);
                        } else {
                            Picasso.with(vThis).load(url).placeholder(R.drawable.empty_photo).into(iv_icon);
                        }
                    }
                    if (tv_summary != null)
                        tv_summary.setText("库存量" + bean.getTotalStockQty() + "   售罄率" + FunctionHelper.DoubleFormat(bean.getTotalStockOutRate()) + "%");
                    if (!ListUtils.isEmpty(bean.getProducts())) {
                        data.addAll(bean.getProducts());
                    }
                }
                if (adapter!=null)
                adapter.setNewData(data);

            }

        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                CdmDetailBean saleBean = OtherAPI.getItemStockStDetail(vThis, sourceId);
                return saleBean;
            } catch (Exception ex) {
                Log.e(TAG, "获取详情发生异常");
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
