package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.ProdectBean;

/**
 * Created by jame on 2018/11/23.
 */

public class SallerAdapter extends BaseQuickAdapter<ProdectBean.SellerUsersBean, BaseViewHolder> {
    public SallerAdapter(Context context) {
        super(R.layout.item_saller, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProdectBean.SellerUsersBean item) {
        if (item != null) {
            helper.setText(R.id.name, item.getName());
            if (item.isCheck) {
                helper.setGone(R.id.iv_cover, true);
            } else {
                helper.setGone(R.id.iv_cover, false);
            }
        }
    }
}
