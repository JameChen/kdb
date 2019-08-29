package com.nahuo.kdb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.SpotGoodsAdpater;
import com.nahuo.kdb.api.KdbAPI;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.PackageBean;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import static com.nahuo.kdb.R.id.iv_right_01;

public class SpotGoodsActivity extends AppCompatActivity implements View.OnClickListener{
    private SpotGoodsActivity Vthis=this;
    private LoadingDialog mloadingDialog;
    private RecyclerView recyclerView;
    private SpotGoodsAdpater adpater;
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_goods);
        BWApplication.addActivity(this);
        findViewById(R.id.tvTLeft).setOnClickListener(this);
        ((TextView)findViewById(R.id.tvTitleCenter)).setText("发货单");
        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.sw);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.bg_red),getResources().getColor(R.color.red));
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Task(Step.GetPackageList).execute();
            }
        });
        swipeLayout.setRefreshing(true);
        adpater=new SpotGoodsAdpater(this);
        recyclerView.setAdapter(adpater);
        adpater.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               List<PackageBean.PackageListBean> data=adapter.getData();
                PackageBean.PackageListBean packageListBean=data.get(position);
                if (packageListBean!=null){
                    Intent intent=new Intent(Vthis,SpotGoodsDetailsActivity.class);
                    intent.putExtra(SpotGoodsDetailsActivity.EXTRA_PACKAGEID,packageListBean.getPackageID());
                    startActivity(intent);
                }
            }
        });
        View empty= LayoutInflater.from(this).inflate(R.layout.textview_empty,null);
        TextView tv_empty= (TextView) empty.findViewById(R.id.tv_empty);
        tv_empty.setText("沒有发货数据");
        adpater.setEmptyView(empty);
        ImageView iv_right_01= (ImageView) findViewById(R.id.iv_right_01);
        iv_right_01.setImageResource(R.drawable.sweep_code);
        iv_right_01.setVisibility(View.GONE);
        iv_right_01.setOnClickListener(this);

        new Task(Step.GetPackageList).execute();
    }
    public enum  Step {
        GetPackageList
    }
    public class  Task   extends AsyncTask<String,Integer,Object> {
        private Step step;
        Task(Step step){
            this.step=step;
        }
        @Override
        protected Object doInBackground(String... params) {
            switch ( step){
                case  GetPackageList:
                    try {
                      return   KdbAPI.getPackageList(Vthis);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return  "error:"+e.getMessage();
                    }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog=new LoadingDialog(Vthis);
            switch ( step){
                case  GetPackageList:
                    mloadingDialog.start("加载数据....");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (mloadingDialog.isShowing()) {
                mloadingDialog.stop();
            }
            swipeLayout.setRefreshing(false);
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(Vthis, ((String) result).replace("error:", ""));
            }
            switch ( step){
                case  GetPackageList:
                        if (result instanceof PackageBean){
                            PackageBean bean= (PackageBean) result;
                            List<PackageBean.PackageListBean> data=new ArrayList<>();
                            if (bean!=null){
                                if (!ListUtils.isEmpty(bean.getPackageList())){
                                  data.addAll(bean.getPackageList());
                                }
                            }
                            adpater.setNewData(data);
                            adpater.notifyDataSetChanged();
                        }
                    break;
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTLeft:
                finish();
                break;
            case iv_right_01:

                break;
        }
    }
}
