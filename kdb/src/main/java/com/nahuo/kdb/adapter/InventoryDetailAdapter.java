package com.nahuo.kdb.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.InventoryDetailBean;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

/**
 * Created by jame on 2018/11/29.
 */

public class InventoryDetailAdapter extends BaseQuickAdapter<InventoryDetailBean.ItemsBean, BaseViewHolder> {
    private Context context;

    public InventoryDetailAdapter(Context context) {
        super(R.layout.item_inventory_detail, null);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, InventoryDetailBean.ItemsBean item) {
        if (item != null) {
            ImageView imageView = helper.getView(R.id.iv_icon);
            String path = ImageUrlExtends.getImageUrl(item.getCover(), 13);
            if (TextUtils.isEmpty(path)) {
                imageView.setImageResource(R.drawable.empty_photo);
            } else {
                Picasso.with(BWApplication.getInstance()).load(path)
                        .placeholder(R.drawable.empty_photo).into(imageView);
            }
            helper.setText(R.id.tv_content, item.getName() + "(" + item.getSku() + ")\n库存数量："
                    + item.getTotalStockQty() + "\n零售价：" +
                    item.getRetailPrice());
            RecyclerView rl_color_size = helper.getView(R.id.rl_color_size);
            InventoryDetailColorSize adapater = new InventoryDetailColorSize(context);
            rl_color_size.setLayoutManager(new LinearLayoutManager(context));
            rl_color_size.setAdapter(adapater);
            rl_color_size.setNestedScrollingEnabled(false);
            if (!ListUtils.isEmpty(item.getProducts())) {
                rl_color_size.setVisibility(View.VISIBLE);
                adapater.setNewData(item.getProducts());
            } else {
                rl_color_size.setVisibility(View.GONE);
            }
        }

    }

    public class InventoryDetailColorSize extends BaseQuickAdapter<InventoryDetailBean.ItemsBean.ProductsBean, BaseViewHolder> {
        private Context context;

        public InventoryDetailColorSize(Context context) {
            super(R.layout.item_inventory_detail_color_size, null);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder helper, InventoryDetailBean.ItemsBean.ProductsBean item) {
            if (item != null) {
                helper.setText(R.id.tv_color, item.getColor());
                helper.setText(R.id.tv_size, item.getSize());
                helper.setText(R.id.tv_pre_qty, item.getStockQty()+"");
                helper.setText(R.id.tv_inventory_qty, item.getCheckQty()+"");
                helper.setText(R.id.tv_win_loser, item.getQtyBalance()+"");
            }
        }
    }
}
