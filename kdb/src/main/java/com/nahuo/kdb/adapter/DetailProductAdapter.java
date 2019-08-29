package com.nahuo.kdb.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nahuo.kdb.CommonListActivity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.ProductModel;

import java.util.List;

public class DetailProductAdapter extends BaseAdapter {

    public Context mContext;
    public List<ProductModel> mList;
    private CommonListActivity.ListType mType;
    private OnChangeQtyListener mListener;

    // 构造函数
    public DetailProductAdapter(Context Context, List<ProductModel> dataList, CommonListActivity.ListType type) {
        mContext = Context;
        mList = dataList;
        mType = type;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ProductModel getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        View view = arg1;
        if (mList.size() > 0) {
            if (view == null) {
                holder = new ViewHolder();
                switch (mType) {
                    case 入库: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor5_work, arg2, false);

                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txt2 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_4);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        view.setTag(holder);
                        break;
                    }
                    case 销售:
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor4_work, arg2, false);

                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        view.setTag(holder);
                        break;
                    case 库存: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor_ku_work, arg2, false);

                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        holder.txtProfitAndloss=(TextView)view.findViewById(R.id.item_detail_sizecolor_item_5);
                        view.setTag(holder);
                        break;
                    }
                }
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ProductModel item = mList.get(arg0);

            holder.color.setText(item.getColor());
            holder.size.setText(item.getSize());
            switch (mType) {
                case 入库: {
                    holder.txt1.setText(item.getBookQty() + "");
                    holder.txt2.setText(item.getCanStockQty() + "");
                    holder.txtQty.setText(item.getDHQty() + "");
                    break;
                }
                case 销售: {
                    holder.txt1.setText(item.getQty() + "");
                    holder.txtQty.setText(item.getBuyQty() + "");
                    break;
                }
                case 库存: {
                    holder.txt1.setText(item.getQty() + "");
                    holder.txtQty.setText(item.getInventoryQty() + "");
                    holder.txtProfitAndloss.setText(item.getProfitLossQty()+"");
                    break;
                }
            }
            if (holder.txtReduce != null) {
                holder.txtReduce.setTag(arg0);
                holder.txtReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ProductModel selectItem = mList.get(selectPosition);
                        qtyWork(true, selectItem);
                    }
                });
                holder.txtAdd.setTag(arg0);
                holder.txtAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ProductModel selectItem = mList.get(selectPosition);
                        qtyWork(false, selectItem);
                    }
                });
            }
        }

        return view;
    }

    private void qtyWork(boolean reduce, ProductModel selectItem) {
        switch (mType) {
            case 入库: {
                if (reduce) {
                    selectItem.setDHQty(selectItem.getDHQty() - 1);
                } else {
                    selectItem.setDHQty(selectItem.getDHQty() + 1);
                }
                break;
            }
            case 销售: {
                if (reduce) {
                    selectItem.setBuyQty(selectItem.getBuyQty() - 1);
                } else {
                    selectItem.setBuyQty(selectItem.getBuyQty() + 1);
                }
                break;
            }
            case 库存: {
                if (reduce) {
                   int qty= selectItem.getInventoryQty() - 1;
                    if (qty<0)
                        qty=0;
                    selectItem.setInventoryQty(qty);
                } else {
                    selectItem.setInventoryQty(selectItem.getInventoryQty() + 1);
                }
                break;
            }
        }
        if (mListener != null) {
            mListener.OnQtyChange();
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public int position;
        //        CheckBox checkBox;
        TextView color;
        TextView size;
        TextView txt1;
        TextView txt2;
        TextView txtReduce;
        TextView txtQty;
        TextView txtAdd;
        TextView txtProfitAndloss;
    }

    public static interface OnChangeQtyListener {
        public void OnQtyChange();
    }

    public void setListener(OnChangeQtyListener listener) {
        mListener = listener;
    }
}
