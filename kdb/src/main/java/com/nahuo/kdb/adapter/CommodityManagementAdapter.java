package com.nahuo.kdb.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.CdMaBean;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

/**
 * Created by jame on 2018/11/29.
 */

public class CommodityManagementAdapter extends BaseQuickAdapter<CdMaBean.ItemsBean,BaseViewHolder> {
    private Context context;
    public CommodityManagementAdapter(Context context) {
        super(R.layout.lv_item_commodity_manage, null);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CdMaBean.ItemsBean item) {
        if (item!=null){
          ImageView imageView= helper.getView(R.id.iv_icon);
            String url= ImageUrlExtends.getImageUrl(item.getCover(),13);
            if (TextUtils.isEmpty(url)){
                imageView.setImageResource(R.drawable.empty_photo);
            }else {
                Picasso.with(BWApplication.getInstance()).load(url)
                        .placeholder(R.drawable.empty_photo).into(imageView);
            }
            helper.setText(R.id.tv_warehousing_qty,item.getInStockQty()+"");
            helper.setText(R.id.tv_sale_out,item.getOrderQty()+"");
            helper.setText(R.id.tv_stock,item.getQtyBalance()+"");
            helper.setText(R.id.tv_sell_rate, FunctionHelper.DoubleFormat(item.getStockOutRate())+"%");
            helper.setText(R.id.tv_content,item.getReName()+"\n款号："+
            item.getSku()+"\n首批入库："+item.getFirstInStockTime());
        }

    }
}
