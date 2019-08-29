package com.nahuo.kdb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nahuo.kdb.model.ShopItemListModel;

public class BaseSearchAdapter extends MyBaseAdapter<ShopItemListModel>{
    protected IBuyClickListener   mIBuyClickListener;
    public void setIBuyClickListener(IBuyClickListener iBuyClickListener) {
        mIBuyClickListener = iBuyClickListener;
    }
    public BaseSearchAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
    public interface IBuyClickListener {
        public void buyOnClickListener(ShopItemListModel model);
    }
}
