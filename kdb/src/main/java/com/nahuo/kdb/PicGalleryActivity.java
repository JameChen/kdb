package com.nahuo.kdb;

import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView.ScaleType;

import com.nahuo.kdb.R;
import com.nahuo.kdb.adapter.SlidePicPagerAdapter;
import com.nahuo.kdb.common.MediaStoreUtils;
import com.nahuo.library.controls.FlowIndicator;
import com.nahuo.library.controls.LightAlertDialog;
import com.nahuo.library.helper.ImageUrlExtends;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

public class PicGalleryActivity extends BaseSlideBackActivity {

    /** 所有的图片地址 */
    public static final String EXTRA_URLS = "EXTRA_URLS";
    /** 当前进来的图片url */
    public static final String EXTRA_CUR_POS = "EXTRA_CUR_POS";
    /**是否转化图片url*/
    public static final String EXTRA_TRANSFER_IMG_URL = "EXTRA_TRANSFER_IMG_URL";
    private Context mContext = this;
    private ViewPager mPager;
    private SlidePicPagerAdapter mPagerAdapter;
    private FlowIndicator mLayoutIndicator;
    private ArrayList<String> mUrls;
    private int mCurPos;
    private DownloadManager mDownloadManager;
    private boolean mTransferImgUrl = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_gallery);
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        initExtras();
        initView();
    }

    private void initExtras() {
        Intent intent = getIntent();
        mUrls = intent.getStringArrayListExtra(EXTRA_URLS);
        mTransferImgUrl = intent.getBooleanExtra(EXTRA_TRANSFER_IMG_URL, true);
        mCurPos = intent.getIntExtra(EXTRA_CUR_POS, -1);
    }

    private void initView() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mLayoutIndicator = (FlowIndicator) findViewById(R.id.layout_indicator);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mLayoutIndicator.setSelectedPos(position);
            }
        });
        mPagerAdapter = new SlidePicPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setPicZoomable(true);
        mPagerAdapter.setPicScaleType(ScaleType.FIT_CENTER);
        mPager.setAdapter(mPagerAdapter);
        
        ArrayList<String> fullUrls = new ArrayList<String>();
        if(mTransferImgUrl){
            for (String url : mUrls) {
                fullUrls.add(ImageUrlExtends.getImageUrl(url, getResources().getInteger(R.integer.grid_pic_width_big)));
            } 
        }else{
            fullUrls.addAll(mUrls);
        }
        
        mPagerAdapter.setData(fullUrls);
        mPagerAdapter.notifyDataSetChanged();
        mLayoutIndicator.setMaxNum(fullUrls.size());
        mPager.setCurrentItem(mCurPos, true);
        mPagerAdapter.setOnViewTabListener(new OnViewTapListener() {
            
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        mPagerAdapter.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                Builder builder = LightAlertDialog.Builder.create(mContext);
                builder.setPositiveButton("保存到手机", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = (String) v.getTag();
                        DownloadManager.Request request = new DownloadManager.Request(Uri
                                .parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                                | DownloadManager.Request.NETWORK_WIFI);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                        // request.setMimeType("image/jpeg");
                        request.setVisibleInDownloadsUi(false);
                        String folderName = "weipu/weipu_save";
                        String fileName = "";
                        try {
                            fileName = url.substring(url.lastIndexOf("/"), url.lastIndexOf("!"));
                        } catch (Exception e) {
                            fileName = System.currentTimeMillis() + ".jpg";
                        }
                        String savePath = Environment.DIRECTORY_DCIM + "/" + folderName + fileName;
                        File tmpFile = new File(Environment.DIRECTORY_DCIM + "/" + folderName);
                        if(!tmpFile.exists()) {
                            tmpFile.mkdirs();
                        }
//                      request.setDestinationInExternalPublicDir(folderName, fileName);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, folderName + fileName);
                        try {
                            long downloadId = mDownloadManager.enqueue(request);
                        } catch (IllegalArgumentException e) {
                            ViewHub.showOkDialog(mContext, "提示", "设备没开启下载功能，无法下载图片", "OK");
                            return;
                        }
//                        String savePath = new StringBuilder(Environment
//                                .getExternalStorageDirectory().getAbsolutePath())
//                                .append(File.separator).append(folderName).append(File.separator)
//                                .append(fileName).toString();
                        MediaStoreUtils.scanImageFile(mContext, savePath);
                        ViewHub.showLongToast(mContext, "图片已保存至: " + savePath);
                    }
                });
                builder.show();
                return false;
            }
        });
    }

}
