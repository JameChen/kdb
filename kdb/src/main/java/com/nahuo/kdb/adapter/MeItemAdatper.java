package com.nahuo.kdb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.MeItemModel;

import java.util.List;

/**
 * Created by 诚 on 2015/9/21.
 */
public class MeItemAdatper extends BaseAdapter {

    public Context mContext;
    public List<MeItemModel> mList;

    public  MeItemAdatper(Context Context, List<MeItemModel> List){
        mContext=Context;
        mList=List;
    }

    private OnMeItemListener mListener;

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
   public void showUnRead(String name,int count){
        if (!ListUtils.isEmpty(mList))
        for (MeItemModel model: mList) {
            if (name.equals(model.getName())){
                model.setCount_unread(count);
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_me, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.ItemText);
            holder.image = (ImageView) view.findViewById(R.id.ItemImage);
            holder.read=(TextView)view.findViewById(R.id.unread_msg_number) ;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final MeItemModel model = mList.get(position);



        if (model != null) {
            holder.title.setText(model.getName());
            holder.image.setBackgroundResource(model.getSourceId());
            if (model.getCount_unread()>0){
                holder.read.setText(model.getCount_unread()+"");
                holder.read.setVisibility(View.VISIBLE);
            }else {
                holder.read.setVisibility(View.INVISIBLE);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnMeItemClick(model);
                }
            });
        }
        //view.setBackgroundColor(Color.WHITE); //设置背景颜色

        return view;
    }


    public class ViewHolder {

        TextView read;
        TextView title;
        ImageView image;

    }

    public static interface OnMeItemListener {
        public void OnMeItemClick(MeItemModel item);
    }

    public void setStyleListener(OnMeItemListener listener) {
        mListener = listener;
    }

}
