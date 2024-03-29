package com.nahuo.kdb.adapter;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.nahuo.kdb.ShowPicFragment;

/**
 * @description 切换图片
 * @created 2014-10-17 上午10:48:54
 * @author ZZB
 */
public class SlidePicPagerAdapter extends FragmentStatePagerAdapter{

    private ArrayList<String> mUrls;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;
    private boolean mPicZoomable;
    private ScaleType mPicScaleType;
    private OnViewTapListener mOnViewTabListener;
    
    public SlidePicPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(ArrayList<String> urls){
        mUrls = urls;
    }
    public ArrayList<String> getData(){
        return mUrls;
    }
    @Override
    public Fragment getItem(int position) {
        ShowPicFragment f = ShowPicFragment.newInstance(mUrls.get(position), mPicZoomable);
        f.setOnClickListener(mOnClickListener);
        f.setOnLongClickListener(mOnLongClickListener);
        f.setOnViewTapListener(mOnViewTabListener);
        f.setScaleType(mPicScaleType);
        return f;
    }

    @Override
    public int getCount() {
        return mUrls == null ? 0 : mUrls.size();
    }

    public void setOnItemClickLitener(View.OnClickListener listener){
        mOnClickListener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }
    public void setOnViewTabListener(OnViewTapListener listener){
        mOnViewTabListener = listener;
    }
    /**设置图片可否缩放*/
    public void setPicZoomable(boolean picZoomable) {
        mPicZoomable = picZoomable;
    }

    /**
     * @description 设置图片的缩放类型
     * @created 2014-10-31 上午10:51:07
     * @author ZZB
     */
    public void setPicScaleType(ScaleType picScaleType) {
        mPicScaleType = picScaleType;
    }
}
