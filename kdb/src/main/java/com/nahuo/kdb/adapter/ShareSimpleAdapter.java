package com.nahuo.kdb.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.model.ImageItemModel;

/**
 * Created by ALAN on 2017/5/17 0017.
 */
public class ShareSimpleAdapter extends BaseRecyclerAdapter<ImageItemModel> implements View.OnClickListener{

    private Listener mListener;

    private Context mContext;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ShareSimpleAdapter() {

    }

    public interface Listener {
        void onItemClick(int position);
    }

    @Override
    protected int getItemViewLayoutId() {
        return R.layout.share_popupwindow_item;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.ViewHolder holder, final int position) {
        View itemView = holder.itemView;
        mContext = itemView.getContext();
        ImageView imageView=(ImageView) itemView.findViewById(R.id.share_item_image);
        TextView text=(TextView) itemView.findViewById(R.id.share_item_tv);
        ImageItemModel item=mData.get(position);
        imageView.setImageResource(item.getResId());
        text.setText(item.getText());
        itemView.setTag(position);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mListener.onItemClick(position);
    }
}
