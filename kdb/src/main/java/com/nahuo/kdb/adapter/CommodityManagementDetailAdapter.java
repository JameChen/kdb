package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.CdmDetailBean;
import com.nahuo.library.helper.FunctionHelper;

/**
 * Created by jame on 2018/11/30.
 */

public class CommodityManagementDetailAdapter extends BaseQuickAdapter<CdmDetailBean.ProductsBean,BaseViewHolder> {
    private Context context;
    public CommodityManagementDetailAdapter(Context context) {
        super(R.layout.item_commodity_management_detail, null);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CdmDetailBean.ProductsBean item) {
        if (item!=null){
            helper.setText(R.id.tv_color,item.getColor());
            helper.setText(R.id.tv_size,item.getSize());
            helper.setText(R.id.tv_in_stock_qty,item.getInStockQty()+"");
            helper.setText(R.id.tv_stock_qty,item.getStockQty()+"");
            helper.setText(R.id.tv_sale_qty,item.getSaleQty()+"");
            helper.setText(R.id.tv_stock_out_rate, FunctionHelper.DoubleFormat(item.getStockOutRate())+"%");
        }
    }
}
