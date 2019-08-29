package com.nahuo.kdb.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.OrderModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaleListAdapter extends BaseQuickAdapter<OrderModel.OrderListBean,BaseViewHolder> {
    private static final String TAG = "SaleListAdapter";

    private List<OrderModel.OrderListBean> datas = new ArrayList<>();
    private Context mContext;

    public SaleListAdapter(Context context) {
        super(R.layout.lv_item_sale_log, null);
        this.mContext=context;
    }


    public void setMyData(List<OrderModel.OrderListBean> datas) {
        this.datas = datas;
        this.setNewData(datas);
    }


    @Override
    protected void convert(BaseViewHolder helper, OrderModel.OrderListBean item) {
        if (item!=null){
            helper.setText(R.id.tv_code,"销售单号："+item.getCode());
            helper.setText(R.id.tv_saler_name,item.getSellerUserName());
            helper.setText(R.id.tv_count,item.getProductQty()+"");
            helper.setText(R.id.tv_price,item.getPayableAmount()+"");
            helper.setText(R.id.tv_custer_name,"客户名称："+item.getOptUserName());
            helper.setText(R.id.tv_sale_time,"销售时间："+item.getCreateTime());
            helper.setText(R.id.tv_status,item.getStatu());
           RecyclerView rv_list_child= helper.getView(R.id.rv_list_child);
            rv_list_child.setLayoutManager(new GridLayoutManager(mContext,2));
            rv_list_child.setNestedScrollingEnabled(false);
            SaleChildAdpater adpater=new SaleChildAdpater(mContext);
            rv_list_child.setAdapter(adpater);
            if (ListUtils.isEmpty(item.getItems())){
                rv_list_child.setVisibility(View.GONE);
            }else {
                rv_list_child.setVisibility(View.VISIBLE);
                adpater.setNewData(item.getItems());
                adpater.notifyDataSetChanged();
            }
        }

    }
    public  class SaleChildAdpater extends  BaseQuickAdapter<OrderModel.OrderListBean.ItemsBean,BaseViewHolder>{
        Context context;
        public SaleChildAdpater(Context context) {
            super(R.layout.item_sale_child, null);
            this.context=context;
        }

        @Override
        protected void convert(BaseViewHolder helper, OrderModel.OrderListBean.ItemsBean item) {
            if (item!=null){
                helper.setText(R.id.tv_01,"款号："+item.getSku());
                helper.setText(R.id.tv_02,"价格："+item.getRetailPrice());
                helper.setText(R.id.tv_03,"总数："+item.getQty());
               ImageView imageView= helper.getView(R.id.iv_icon);
               String cover= ImageUrlExtends.getImageUrl(item.getCover(),11);
                if (TextUtils.isEmpty(cover))
                {
                    imageView.setImageResource(R.drawable.empty_photo);
                }else {
                    Picasso.with(BWApplication.getInstance()).load(cover)
                            .placeholder(R.drawable.empty_photo).into(imageView);
                }
            }
        }
    }
}


