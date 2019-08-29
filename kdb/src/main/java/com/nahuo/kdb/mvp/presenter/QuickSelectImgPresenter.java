package com.nahuo.kdb.mvp.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.nahuo.kdb.common.MediaStoreUtils;
import com.nahuo.kdb.model.MediaStoreImage;
import com.nahuo.kdb.mvp.MvpBasePresenter;
import com.nahuo.kdb.mvp.view.QuickSelectImgView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZB on 2015/7/9 17:22
 */
public class QuickSelectImgPresenter extends MvpBasePresenter<QuickSelectImgView>{

    private Context mAppContext;

    public QuickSelectImgPresenter(Context context){
        mAppContext = context.getApplicationContext();
    }

    public void loadRecentImages(Context context){
        new AsyncTask<Void, Void, List<MediaStoreImage>>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getView().showLoading(true);
            }

            @Override
            protected List<MediaStoreImage> doInBackground(Void... params) {
                List<MediaStoreImage> imgs = new ArrayList<MediaStoreImage>();
                if(isViewAttached()){
                    imgs = MediaStoreUtils.getRecentImages(mAppContext, 20);
                }
                return imgs;
            }

            @Override
            protected void onPostExecute(List<MediaStoreImage> imgs) {
                super.onPostExecute(imgs);
                if(isViewAttached()){
                    getView().onRecentImgsLoaded(imgs);
                    getView().showLoading(false);
                }
            }
        }.execute();

    };

}
