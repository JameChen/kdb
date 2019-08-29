package com.nahuo.kdb.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.CommonListActivity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.SpManager;
import com.nahuo.kdb.model.ShopItemListModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonListAdapter extends BaseAdapter {

    public Context mContext;
    public List<ShopItemListModel> mList;
    private CommonListActivity.ListType mType;
    private CommonListActivity mActivity;

    // 构造函数
    public CommonListAdapter(Context Context, List<ShopItemListModel> dataList, CommonListActivity.ListType type) {
        mContext = Context;
        mList = dataList;
        mType = type;
        this.mActivity = (CommonListActivity) Context;//强转成对应的activity,便于获取数据
    }

    List<Boolean> if_checks = new ArrayList<>();

    public void setAllCheck() {
        if_checks.clear();
        if (mList != null) {
            for (ShopItemListModel bean : mList) {
                if_checks.add(bean.is_check());
            }
            if (if_checks.contains(false)) {
                for (ShopItemListModel bean : mList) {
                    bean.setIs_check(true);
                }
            } else {
                for (ShopItemListModel bean : mList) {
                    bean.setIs_check(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    List<ShopItemListModel> slist = new ArrayList<>();

    public List<ShopItemListModel> getWareCheck() {
        slist.clear();
        if (mList != null) {
            for (ShopItemListModel bean : mList) {
                if (bean.is_check) {
                    slist.add(bean);
                }
            }
        }
        return slist;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ShopItemListModel getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        View view = arg1;
        if (mList.size() > 0) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_common_list, arg2, false);
                holder = new ViewHolder();
                holder.item_check = (ImageView) view.findViewById(R.id.item_check);
                holder.title = (TextView) view.findViewById(R.id.item_title);
                holder.cover = (ImageView) view.findViewById(R.id.item_cover);
                holder.text1 = (TextView) view.findViewById(R.id.item_text_1);
                holder.text2 = (TextView) view.findViewById(R.id.item_text_2);
                holder.text4 = (TextView) view.findViewById(R.id.item_text_4);
                holder.text3 = (TextView) view.findViewById(R.id.item_text_3);
                holder.dh_view = view.findViewById(R.id.dh_view);
                holder.txtReduce = (TextView) view
                        .findViewById(R.id.dh_view_qty_reduce);
                holder.txtReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ShopItemListModel selectItem = mList.get(selectPosition);
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
                        ShopItemListModel selectItem = mList.get(selectPosition);
                        qtyWork(false, selectItem);
                    }
                });
                holder.txtDetailTxt = (TextView) view
                        .findViewById(R.id.dh_view_detail_text);
                holder.tv_ishide = (TextView) view.findViewById(R.id.tv_ishide);
                holder.check = view.findViewById(R.id.check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final ShopItemListModel item = mList.get(arg0);
            boolean isShowPurchase = SpManager.getPurchase(mContext);
            boolean isShowRetail = SpManager.getRetail(mContext);

            String url = ImageUrlExtends.getImageUrl(item.getCover(), 10);
            Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(holder.cover);
            holder.title.setText(item.getName());
            holder.txtReduce.setTag(arg0);
            holder.txtAdd.setTag(arg0);
            if (holder.text4 != null)
                holder.text4.setVisibility(View.GONE);
            switch (mType) {
                case 入库:
                    if (holder.text4!=null)
                        holder.text4.setVisibility(View.VISIBLE);
                    if (isShowPurchase) {
                        if (holder.text2 != null)
                            holder.text2.setVisibility(View.VISIBLE);
                    } else {
                        if (holder.text2 != null)
                            holder.text2.setVisibility(View.GONE);
                    }
                    if (isShowRetail) {
                        if (holder.text3 != null)
                            holder.text3.setVisibility(View.VISIBLE);
                    } else {
                        if (holder.text3 != null)
                            holder.text3.setVisibility(View.GONE);
                    }
                    if (holder.text4!=null)
                        holder.text4.setText(Html.fromHtml("可入库数: " + item.getCanStockQty() + "&nbsp;&nbsp;&nbsp;&nbsp;已入数：" + (new DecimalFormat("##0.00").format(item.getInStockQty()))));
                    holder.text1.setText(Html.fromHtml("订货数:" + item.getBookQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;已发数：" + item.getShipQty() + "件"));
                    holder.text2.setText(Html.fromHtml("进货价:<font color=\"#FF0000\">¥</font>" + item.getPrice() + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getBookQty() * item.getPrice()))));
                    holder.text3.setText(Html.fromHtml("零售价:<font color=\"#FF0000\">¥</font>" + item.getRetailPrice()));
                    holder.item_check.setVisibility(View.VISIBLE);
                    holder.check.setVisibility(View.VISIBLE);
                    if (item.getDhQty() <= 0) {
                        holder.dh_view.setVisibility(View.GONE);
                    } else {
                        holder.dh_view.setVisibility(View.VISIBLE);
                        holder.txtQty.setText(item.getDhQty() + "");
                        holder.txtDetailTxt.setText(item.getColor() + "/" + item.getSize());
                    }
                    if (item.is_check) {
                        holder.item_check.setImageResource(R.drawable.check_circle);
                    } else {
                        holder.item_check.setImageResource(R.drawable.uncheck_circle);
                    }
                    holder.check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < mList.size(); i++) {
                                if (i == arg0) {
                                    if (mList.get(i).is_check) {
                                        mList.get(i).is_check = false;
                                    } else {
                                        mList.get(i).is_check = true;
                                    }

                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case 销售:
                    holder.item_check.setVisibility(View.GONE);
                    holder.check.setVisibility(View.GONE);
                    holder.text1.setText(Html.fromHtml("零售价:<font color=\"#FF0000\">¥</font>" + item.getRetailPrice()));
                    holder.text2.setText(Html.fromHtml("库存:" + item.getQty() + "件"));
                    holder.text3.setVisibility(View.GONE);

                    if (isShowRetail) {
                        if (holder.text1 != null)
                            holder.text1.setVisibility(View.VISIBLE);
                    } else {
                        if (holder.text1 != null)
                            holder.text1.setVisibility(View.GONE);
                    }
                    break;
                case 库存:
                    if (isShowPurchase) {
                        if (holder.text1 != null)
                            holder.text1.setVisibility(View.VISIBLE);
                    } else {
                        if (holder.text1 != null)
                            holder.text1.setVisibility(View.GONE);
                    }
                    if (isShowRetail) {
                        if (holder.text2 != null)
                            holder.text2.setVisibility(View.VISIBLE);
                    } else {
                        if (holder.text2 != null)
                            holder.text2.setVisibility(View.GONE);
                    }
                    holder.item_check.setVisibility(View.GONE);
                    holder.check.setVisibility(View.VISIBLE);
                    holder.text1.setText(Html.fromHtml("进货价:<font color=\"#FF0000\">¥</font>" + item.getAgentPrice() + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getQty() * item.getOriPrice()))));
                    holder.text2.setText(Html.fromHtml("零售价:<font color=\"#FF0000\">¥</font>" + item.getRetailPrice()));
                    holder.text3.setText(Html.fromHtml("库存:" + item.getQty() + "件"));
                    if (item.is_check) {
                        holder.item_check.setImageResource(R.drawable.check_circle);
                    } else {
                        holder.item_check.setImageResource(R.drawable.uncheck_circle);
                    }
                    if (item.IsHide) {
                        holder.tv_ishide.setText("未展示");
                        holder.tv_ishide.setVisibility(View.GONE);
                    } else {
                        holder.tv_ishide.setText("已展示");
                        holder.tv_ishide.setVisibility(View.GONE);
                    }
                    holder.check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < mList.size(); i++) {
                                if (i == arg0) {
                                    if (mList.get(i).is_check) {
                                        mList.get(i).is_check = false;
                                    } else {
                                        mList.get(i).is_check = true;
                                    }

                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        return view;
    }

    private void qtyWork(boolean reduce, ShopItemListModel selectItem) {
        switch (mType) {
            case 入库: {
                if (reduce) {
                    selectItem.setDhQty(selectItem.getDhQty() - 1);
                } else {
                    selectItem.setDhQty(selectItem.getDhQty() + 1);
                }
                break;
            }
            case 销售: {
                break;
            }
            case 库存: {
                break;
            }
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
        TextView text4;
        View dh_view;
        TextView txtReduce;
        TextView txtQty;
        TextView txtAdd, tv_ishide;
        TextView txtDetailTxt;
        ImageView item_check;
        View check;
    }

}
