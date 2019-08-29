package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.AchieveBean;
import com.nahuo.library.helper.FunctionHelper;

/**
 * Created by jame on 2018/11/30.
 */

public class AchieveAdapter extends BaseQuickAdapter<AchieveBean.SellerListBean,BaseViewHolder> {
    private  Context context;
    public AchieveAdapter(Context context) {
        super(R.layout.item_achieve, null);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AchieveBean.SellerListBean item) {
        if (item!=null){
            helper.setText(R.id.tv_01,item.getTop()+"");
            helper.setText(R.id.tv_02,item.getSellerUserName()+"");
            helper.setText(R.id.tv_04,item.getProductQty()+"");
            helper.setText(R.id.tv_05, FunctionHelper.DoubleFormat(item.getJointRate())+"%");
            helper.setText(R.id.tv_06,FunctionHelper.DoubleFormat(item.getPerCustomerTransaction())+"");
            helper.setText(R.id.tv_03,item.getOrderCount()+"");
            helper.setText(R.id.tv_07,FunctionHelper.DoubleFormat(item.getSalesAmount())+"");
        }
    }
}
