package com.nahuo.kdb.adapter;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nahuo.library.controls.RecyclingImageView;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.kdb.R;
import com.squareup.picasso.Picasso;

public class PicGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mData;
    private int mNumColumns;
    private static int[] IMG_SIZES;
    private int mGridViewWidth;
    private int IMG_SIZE;
    private AbsListView.LayoutParams mLayoutParams;

    public void setData(String[] urls) {
        this.mData = urls;
        if(mNumColumns == 0){
            mNumColumns = getCount() < 4 ? getCount() : 4;
            setNumColumns(mNumColumns == 0 ? 1 : mNumColumns);
        }
    }
    public void setNumColumns(int numColumns){
        mNumColumns = numColumns;
        int idx = mNumColumns - 1;
        idx = idx < 0 ? 0 : idx;
        idx = idx >= IMG_SIZES.length ? IMG_SIZES.length - 1 : idx;
        IMG_SIZE = IMG_SIZES[idx];
    }

    public PicGridViewAdapter(Context context) {
        this.mContext = context;
        Resources res = mContext.getResources();
        IMG_SIZES = new int[] { res.getInteger(R.integer.grid_pic_width_big),
                res.getInteger(R.integer.grid_pic_width_mid),
                res.getInteger(R.integer.grid_pic_width_small) };
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.length;
    }

    @Override
    public Object getItem(int position) {
        return null == mData ? null : mData[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            mLayoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    mGridViewWidth / mNumColumns);
            imageView = new RecyclingImageView(mContext);
            imageView.setLayoutParams(mLayoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        String tmpUrl = mData[position];

        if (tmpUrl==null || tmpUrl.length()==0)
        {
            imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,1));
            return imageView;
        }
        File f = new File(tmpUrl);
        if (f.exists()) {
            Picasso.with(mContext).load(f).placeholder(R.drawable.empty_photo).into(imageView);
        } else {
            String url = ImageUrlExtends.getImageUrl(tmpUrl, IMG_SIZE);
            if(!TextUtils.isEmpty(url)){
                Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(imageView);
            }
            
        }

        return imageView;
    }

    public int getGridViewWidth() {
        return mGridViewWidth;
    }

    public void setGridViewWidth(int gridViewWidth) {
        mGridViewWidth = gridViewWidth;
    }

}
