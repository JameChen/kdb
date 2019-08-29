package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.PackageBean;

/**
 * Created by jame on 2018/11/27.
 */

public class SpotGoodsAdpater extends BaseQuickAdapter<PackageBean.PackageListBean,BaseViewHolder> {
    private  Context context;
    public SpotGoodsAdpater(Context context) {
        super(R.layout.item_spot_goods, null);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PackageBean.PackageListBean item) {
        if (item!=null){
            helper.setText(R.id.tv_01,"单号："+item.getExpressCode());
            helper.setText(R.id.tv_02,"物流公司："+item.getExpressName());
            helper.setText(R.id.tv_03,"发货地点："+item.getSender());
            helper.setText(R.id.tv_04,"日期："+item.getCreateTime());
            helper.setText(R.id.tv_05,"总计："+item.getShipQty()+"件");
            helper.setText(R.id.tv_05,"可入库数："+item.getCanInstockQty()+"件");
        }
    }
}
