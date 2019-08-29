package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.InventoryBean;

/**
 * Created by jame on 2018/11/28.
 */

public class InventoryAdapter extends BaseQuickAdapter<InventoryBean.ListBean,BaseViewHolder> {
    private Context context;
    public InventoryAdapter(Context context) {
        super(R.layout.item_inventory, null);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, InventoryBean.ListBean item) {
        if (item!=null){
                helper.setText(R.id.tv_time,item.getStockTime());
        }

    }
}
