package com.nahuo.kdb.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.BWApplication;
import com.nahuo.kdb.R;
import com.nahuo.kdb.model.MenuBean;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jame on 2018/11/22.
 */

public class MenuAdapter extends BaseQuickAdapter<MenuBean.MenusBean,BaseViewHolder> {
    List<MenuBean.MenusBean> data;
    Context context;
    public MenuAdapter(Context context, @Nullable List<MenuBean.MenusBean> data) {
        super(R.layout.menu_item_lv1, data);
        this.data=data;
        this.context=context;
    }
    public void  addMyData(List<MenuBean.MenusBean> data){
        this.setNewData(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean.MenusBean item) {
        if (item!=null) {
            helper.setText(R.id.tv_name, item.getName());
           ImageView imageView= helper.getView(R.id.iv_icon);
            String url=ImageUrlExtends.getImageUrl(item.getImgUrl());
            if (TextUtils.isEmpty(url))
                imageView.setImageResource(R.drawable.empty_photo);
                else
            Picasso.with(BWApplication.getInstance()).load(url)
                    .placeholder(R.drawable.empty_photo).into(imageView);
        }

    }
}
