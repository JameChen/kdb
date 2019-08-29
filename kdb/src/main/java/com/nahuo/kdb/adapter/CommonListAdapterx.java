package com.nahuo.kdb.adapter;


import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.PicGalleryActivity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.model.ScanQrcodeModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommonListAdapterx extends BaseAdapter {

    public Context mContext;
    public List<ScanQrcodeModel> mList;
    public TextView tv_count;

    // 构造函数
    public CommonListAdapterx(Context Context, List<ScanQrcodeModel> dataList, TextView tv_count) {
        this.mContext = Context;
        this.mList = dataList;
        this.tv_count = tv_count;
    }

    List<Boolean> if_checks = new ArrayList<>();


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ScanQrcodeModel getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void showQtyCount() {
        int qty = 0;
        for (ScanQrcodeModel bean : mList) {
            qty += bean.getDhQty();
        }
        tv_count.setText("到货数量：" + qty);
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        View view = arg1;
        if (mList.size() > 0) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_common_listx, arg2, false);
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.item_title);
                holder.cover = (ImageView) view.findViewById(R.id.item_cover);
                holder.text2 = (TextView) view.findViewById(R.id.item_text_2);
                holder.text3 = (TextView) view.findViewById(R.id.item_text_3);
                holder.dh_view = view.findViewById(R.id.dh_view);
                holder.txtReduce = (TextView) view
                        .findViewById(R.id.dh_view_qty_reduce);
                holder.txtReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ScanQrcodeModel selectItem = mList.get(selectPosition);
                        qtyWork(true, selectItem);
                    }
                });
                holder.txtQty = (TextView) view
                        .findViewById(R.id.dh_view_qty);
                holder.txtAdd = (TextView) view
                        .findViewById(R.id.dh_view_qty_add);
                holder.txtAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ScanQrcodeModel selectItem = mList.get(selectPosition);
                        qtyWork(false, selectItem);
                    }
                });
                holder.txtDetailTxt = (TextView) view
                        .findViewById(R.id.dh_view_detail_text);
                holder.tv_ishide = (TextView) view.findViewById(R.id.tv_ishide);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final ScanQrcodeModel item = mList.get(arg0);

            final String url = ImageUrlExtends.getImageUrl(item.getCover(), 10);
            Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(holder.cover);
            holder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PicGalleryActivity.class);
                    intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(item.getCover());
                    intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, arr);
                    mContext.startActivity(intent);
                }
            });
            holder.title.setText(item.getName());
            holder.txtReduce.setTag(arg0);
            holder.txtAdd.setTag(arg0);
            holder.text2.setText(Html.fromHtml("颜色：" + item.getColor() + "&nbsp;&nbsp;&nbsp;&nbsp;尺码：" + item.getSize()));
            boolean isShowPurchase= SpManager.getPurchase(mContext);
            boolean isShowRetail= SpManager.getRetail(mContext);
            if (isShowPurchase&&isShowRetail) {
                holder.text3.setText(Html.fromHtml("进货价：<font color=\"#FF0000\">¥</font>" + item.getAgentPrice() + "&nbsp;&nbsp;&nbsp;&nbsp;零售价：<font color=\"#FF0000\">¥</font>" + item.getOrgPrice()));
            }else if (isShowPurchase&&!isShowRetail){
                holder.text3.setText(Html.fromHtml("进货价：<font color=\"#FF0000\">¥</font>" + item.getAgentPrice() ));
            }else if (!isShowPurchase&&isShowRetail){
                holder.text3.setText(Html.fromHtml("零售价：<font color=\"#FF0000\">¥</font>" + item.getOrgPrice()));
            }
                holder.txtDetailTxt.setText("(可入库数:" + item.getMaxQty() + ")");
            holder.txtQty.setText(item.getDhQty() + "");
            showQtyCount();
        }

        return view;
    }

    private void qtyWork(boolean reduce, ScanQrcodeModel selectItem) {

        if (reduce) {
            int qty = selectItem.getDhQty() - 1;
            if (qty <= 0) {
                qty = 0;
            }
            selectItem.setDhQty(qty);
        } else {
            int addQty = selectItem.getDhQty() + 1;
            if (addQty > selectItem.getMaxQty()) {
                ViewHub.showShortToast(mContext, "到货数不能大于可入库数");
                return;
            }
            selectItem.setDhQty(addQty);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public int position;
        ImageView cover;
        TextView title;
        TextView text1;
        TextView text2;
        TextView text3;
        View dh_view;
        TextView txtReduce;
        TextView txtQty;
        TextView txtAdd, tv_ishide;
        TextView txtDetailTxt;
    }

}
