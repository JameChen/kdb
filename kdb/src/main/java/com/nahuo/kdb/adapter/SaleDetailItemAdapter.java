package com.nahuo.kdb.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.kdb.PicGalleryActivity;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.model.SaleDetailBean;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SaleDetailItemAdapter extends BaseAdapter {

    public List<SaleDetailBean.ProductsBean> models ;
    private Context mContext;
    public SaleDetailItemAdapter(Context context){
        mContext=context;
    };
    @Override
    public int getCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public SaleDetailBean.ProductsBean getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null ; 
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lvitem_sale_detail_product, null) ;
            holder = new ViewHolder() ; 
            convertView.setTag(holder) ;
            holder.icon = (ImageView)convertView.findViewById(R.id.img_order_detail_icon) ; 
            holder.name = (TextView)convertView.findViewById(R.id.txt_order_detail_name) ; 
            holder.info = (TextView)convertView.findViewById(R.id.txt_order_detail_info) ; 
            holder.money = (TextView)convertView.findViewById(R.id.txt_order_detail_money) ;
            holder.txt_order_detail_ku=(TextView) convertView.findViewById(R.id.txt_order_detail_ku);
            holder.line=convertView.findViewById(R.id.line);
        }
        else{
            holder = (ViewHolder)convertView.getTag()  ;
        }
        if (!ListUtils.isEmpty(models)){
            if (position<models.size()-1){
                holder.line.setVisibility(View.VISIBLE);
            }else {
                holder.line.setVisibility(View.GONE);
            }
        }
        SaleDetailBean.ProductsBean model = getItem(position) ;
        String imageUrl = model.getCover() ;
        imageUrl = ImageUrlExtends.getImageUrl(imageUrl, 8);
        if (TextUtils.isEmpty(imageUrl)){
            holder.icon.setImageResource(R.drawable.empty_photo);
        }else {
            Picasso.with(parent.getContext()).load(imageUrl).placeholder(R.drawable.empty_photo).into(holder.icon);
        }
        holder.txt_order_detail_ku.setText("款号："+model.getSku());
        holder.name.setText(model.getName()) ;
        holder.info.setText(model.getColor()+"/"+model.getSize()+"/"+model.getQty()+"件") ;
        holder.money.setText(parent.getResources().getString(R.string.rmb_x , model.getPrice()));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleDetailBean.ProductsBean model = getItem(position) ;
                Intent intent = new Intent(mContext, PicGalleryActivity.class);
                intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                ArrayList<String> arr = new ArrayList<>();
                String imageUrl = model.getCover() ;
                arr.add(imageUrl);
                intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, arr);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        ImageView icon ; 
        TextView name , info , money,txt_order_detail_ku;
        View line;
    }
}
