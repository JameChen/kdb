package com.nahuo.kdb.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.nahuo.kdb.R;
import com.nahuo.kdb.common.ListUtils;
import com.nahuo.kdb.common.MenuID;
import com.nahuo.kdb.model.MenuBean;

import java.util.List;

/**
 * Created by jame on 2018/11/22.
 */

public class MainAdapter extends BaseQuickAdapter<MenuBean.NavBarBean, BaseViewHolder> {
    Context context;

    public MainAdapter(Context context) {
        super(R.layout.menu_item_lv2, null);
        this.context = context;
    }
    public void setMyData(List<MenuBean.NavBarBean> data){
        this.setNewData(data);
    }
    public void setBlueToothData(String connect){
        if (!ListUtils.isEmpty(getData())){
            for (MenuBean.NavBarBean navBarBean:getData()) {
                if (navBarBean.getID().equals(MenuID.ID_Bluetooth)){
                    navBarBean.setName(connect);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }
    @Override
    protected void convert(BaseViewHolder helper, MenuBean.NavBarBean item) {
        if (item!=null) {
            helper.setText(R.id.tv_left_text, item.getName());
            helper.setText(R.id.tv_right_text, item.getRemark());
        }
    }
}
