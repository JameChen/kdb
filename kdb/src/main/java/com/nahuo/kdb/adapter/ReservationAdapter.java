package com.nahuo.kdb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.ReservationBean;
import com.nahuo.kdb.common.Const;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.nahuo.kdb.R.id.tv_appointment_conact;
import static com.nahuo.kdb.R.id.tv_appointment_order_time;
import static com.nahuo.kdb.R.id.tv_appointment_tel;
import static com.nahuo.kdb.R.id.tv_appointment_time;

/**
 * Created by jame on 2017/5/16.
 */

public class ReservationAdapter extends BaseAdapter {
    public Context mContext;
    public List<ReservationBean> mList;

    public ReservationAdapter(Context context, List<ReservationBean> mList) {
        this.mContext = context;
        this.mList = mList;

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        try {
            if (mList.size() > 0) {
                if (view == null) {
                    view = LayoutInflater.from(mContext).inflate(
                            R.layout.item_appointment, parent, false);
                    holder = new ViewHolder();
                    holder.tv_appointment_number = (TextView) view.findViewById(R.id.tv_appointment_number);
                    holder.tv_appointment_conact = (TextView) view.findViewById(tv_appointment_conact);
                    holder.tv_appointment_order_time = (TextView) view.findViewById(tv_appointment_order_time);
                    holder.tv_appointment_time = (TextView) view.findViewById(tv_appointment_time);
                    holder.tv_appointment_itemname = (TextView) view.findViewById(R.id.tv_appointment_itemname);
                    holder.tv_color_size = (TextView) view.findViewById(R.id.tv_color_size);
                    holder.tv_appointment_price = (TextView) view.findViewById(R.id.tv_appointment_price);
                    holder.tv_appointment_tel = (TextView) view.findViewById(tv_appointment_tel);
                    holder.iv_commodity = (ImageView) view.findViewById(R.id.iv_commodity);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                ReservationBean bean = mList.get(position);
                holder.tv_appointment_number.setText("预约号：" + bean.getCode());
                holder.tv_appointment_conact.setText("联系人：" + bean.getLinkMan());
                holder.tv_appointment_order_time.setText("下单时间:" + bean.getCreateTime());
                holder.tv_appointment_time.setText("预约时间:" + bean.getBookTime());
                holder.tv_appointment_itemname.setText("" + bean.getItemName());
                holder.tv_color_size.setText(bean.getColor() + "/" + bean.getSize());
                holder.tv_appointment_price.setText("¥" + bean.getItemPrice());
                holder.tv_appointment_tel.setText("电话：" + bean.getLinkMobile());
                String url = ImageUrlExtends.getImageUrl(bean.getItemCover(), Const.LIST_ITEM_SIZE);
                Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(holder.iv_commodity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public class ViewHolder {
        TextView tv_appointment_number, tv_appointment_conact, tv_appointment_order_time, tv_appointment_time,
                tv_appointment_itemname, tv_color_size, tv_appointment_price, tv_appointment_tel;
        ImageView iv_commodity;
    }
}
