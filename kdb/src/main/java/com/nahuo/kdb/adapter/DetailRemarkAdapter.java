package com.nahuo.kdb.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.CommonListActivity;
import com.nahuo.kdb.model.ItemRemarkModel;
import com.nahuo.kdb.model.ItemRemarkModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailRemarkAdapter extends BaseAdapter {

    public Context mContext;
    public List<ItemRemarkModel> mList;

    // 构造函数
    public DetailRemarkAdapter(Context Context, List<ItemRemarkModel> dataList) {
        mContext = Context;
        mList = dataList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ItemRemarkModel getItem(int arg0) {
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
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_detail_remark_list, arg2, false);
                holder = new ViewHolder();

                holder.name = (TextView) view
                        .findViewById(R.id.item_name);
                holder.time = (TextView) view
                        .findViewById(R.id.item_time);
                holder.content = (TextView) view
                        .findViewById(R.id.item_content);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ItemRemarkModel item = mList.get(arg0);

            holder.name.setText(item.getUserName());
            holder.time.setText(item.getRecordTime());
            holder.content.setText(item.getContent());
        }

        return view;
    }

    public class ViewHolder {
        public int position;
        TextView name;
        TextView time;
        TextView content;
    }
}
