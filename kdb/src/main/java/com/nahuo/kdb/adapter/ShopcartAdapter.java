package com.nahuo.kdb.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.common.Utils;
import com.nahuo.kdb.controls.NumPlusMinusDialog;
import com.nahuo.kdb.controls.NumPlusMinusDialog.NumPlusMinusDialogListener;
import com.nahuo.kdb.model.ShopCartModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopcartAdapter extends BaseAdapter {

    private static final String TAG = "ShopcartAdapter";

    private TotalPriceChangedListener mTotalPriceChangedListener;
    public List<ShopCartModel> datas = new ArrayList<>();
    private Context mContext;

    // 构造函数
    public ShopcartAdapter(Context Context) {
        mContext = Context;
    }

    public void setData(List<ShopCartModel> datas) {
        this.datas = datas;
    }

    public List<ShopCartModel> getdatas() {
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ShopCartModel getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_shopcart_child_item, null);
            holder.checkBox = (CheckBox) convertView.findViewById(android.R.id.checkbox);
            holder.icon = (ImageView) convertView.findViewById(android.R.id.icon);
            holder.tvTitle = (TextView) convertView.findViewById(android.R.id.title);
            holder.tvColorSize = (TextView) convertView.findViewById(android.R.id.text1);
            holder.tvPrice = (TextView) convertView.findViewById(android.R.id.text2);
            holder.etCount = (EditText) convertView.findViewById(android.R.id.edit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ShopCartModel data = getItem(position);

        holder.tvTitle.setText(data.getName());
        holder.tvColorSize.setText(data.getColor() + "/" + data.getSize());
        holder.etCount.setText(data.getQty() + "");
        String imageUrl = data.getCover();
        imageUrl = ImageUrlExtends.getImageUrl(imageUrl, 8);
        if (TextUtils.isEmpty(imageUrl)) {
            holder.icon.setImageResource(R.drawable.empty_photo);
        } else {
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.empty_photo).into(holder.icon);
        }
        holder.tvPrice.setText("¥" + Utils.moneyFormat(data.getPrice()));
        holder.checkBox.setChecked(data.isSelect());
        holder.checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                data.setSelect(cb.isChecked());
                notifyDataSetChanged();
                if (mTotalPriceChangedListener != null) {
                    mTotalPriceChangedListener.selectItemChanged();
                }
            }
        });
        holder.etCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NumPlusMinusDialog dialog = NumPlusMinusDialog.newInstance(data.getQty());
                dialog.setNumPlusMinusDialogListener(new NumPlusMinusDialogListener() {
                    @Override
                    public void numChanged(int num) {
                        ShopCartModel shopCartModel =new ShopCartModel();
                        shopCartModel.setItemID(data.getItemID());
                        shopCartModel.setColor(data.getColor());
                        shopCartModel.setSize(data.getSize());
                        shopCartModel.setQty(num);
                      //  data.setQty(num);
                        notifyDataSetChanged();
                        if (mTotalPriceChangedListener != null) {
                            mTotalPriceChangedListener.totalItemCountChanged(shopCartModel);
                        }
                    }
                });
                dialog.show(((Activity) mContext).getFragmentManager(), "numDialog");
            }
        });
        return convertView;
    }


    private final static class ViewHolder {
        public CheckBox checkBox;
        public ImageView icon;
        public TextView tvTitle;
        public TextView tvColorSize;
        public EditText etCount;
        public TextView tvPrice;
    }

    public interface TotalPriceChangedListener {
        void selectItemChanged();

        void totalItemCountChanged(ShopCartModel data);
    }

    public void setTotalPriceChangedListener(TotalPriceChangedListener listener) {
        this.mTotalPriceChangedListener = listener;
    }
}
