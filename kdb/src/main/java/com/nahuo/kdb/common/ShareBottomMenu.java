package com.nahuo.kdb.common;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nahuo.kdb.R;
import com.nahuo.kdb.ViewHub;
import com.nahuo.kdb.adapter.ShareSimpleAdapter;
import com.nahuo.kdb.model.ImageItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALAN on 2017/5/17 0017.
 */
public class ShareBottomMenu extends PopupWindow{
private static final String TAG=ShareBottomMenu.class.getSimpleName();
    private View mRootView;

    private Activity mActivity;

    private RecyclerView recyclerView;

    private TextView cancelView;

    private ShareSimpleAdapter adapter;

    private LinearLayout mListViewBg;

    private View mContent;

    private ShareEntity shareEntity;

    private ShareHelper mShareHelper;

    private  static final int ZERO=0,ONE=1,TWO=2,THREE=3,FOUR=4,FIVE=5;

    public ShareEntity getShareEntity() {
        return shareEntity;
    }

    public void setShareEntity(ShareEntity shareEntity) {
        this.shareEntity = shareEntity;
    }

    public ShareBottomMenu(Activity mActivity){
        super();
        this.mActivity=mActivity;
        initViews();
    }

    public View getmContent() {
        return mContent;
    }

    private void initViews(){
        mShareHelper = new ShareHelper(mActivity);
        mShareHelper.setShareType(NahuoShare.SHARE_TYPE_WEBPAGE);

        mRootView=mActivity.getLayoutInflater().inflate(R.layout.share_popupwindow,null);

        recyclerView=(RecyclerView) mRootView.findViewById(R.id.id_share_recyclerview);

        cancelView=(TextView) mRootView.findViewById(R.id.id_share_cancel);

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mListViewBg = (LinearLayout) mRootView
                .findViewById(R.id.content);

        mContent=mRootView.findViewById(R.id.content);

        mRootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int top = getmContent().getTop();
                int bottom = getmContent().getBottom();

                int y = (int)event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < top || y > bottom) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    private ShareBottomMenu create() {
        adapter = new ShareSimpleAdapter();
        List<ImageItemModel> list=new ArrayList<ImageItemModel>();
        list.add(new ImageItemModel(R.drawable.ic_login_wechat,"微信好友"));
        list.add(new ImageItemModel(R.drawable.ic_friend,"朋友圈"));
        list.add(new ImageItemModel(R.drawable.ic_qq2,"QQ好友"));
        list.add(new ImageItemModel(R.drawable.ic_qq_space,"QQ空间"));
        list.add(new ImageItemModel(R.drawable.ic_login_sina,"新浪微博"));
        list.add(new ImageItemModel(R.drawable.ic_link,"复制链接"));
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManage = new GridLayoutManager(mActivity, 4);
        recyclerView.setLayoutManager(layoutManage);
        adapter.setListener(new ShareSimpleAdapter.Listener() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case ZERO:
                        mShareHelper.share(NahuoShare.PLATFORM_WX_FRIEND, new ShareEntity());
                        break;
                    case ONE:
                        mShareHelper.share(NahuoShare.PLATFORM_WX_CIRCLE, new ShareEntity());
                        break;
                    default:
                        ViewHub.showLongToast(mActivity,"功能暂未开放");
                }
            }
        });
        return this;
    }


    /**
     * @description 显示菜单栏
     * @created 上午11:22:23
     * @author ALAN
     */
    public void show(View view) {

        create();

        int[] location = new int[2];
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        location[0]=screenWidth;
        location[1]=screenHeigh;
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(location[1] - getStatusHeight());
        this.setContentView(mRootView);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);

        showAtLocation(view, Gravity.NO_GRAVITY, location[0],
                view.getTop());
        mListViewBg.setVisibility(View.VISIBLE);
        mListViewBg.startAnimation(AnimationUtils.loadAnimation(mActivity,
                com.nahuo.library.R.anim.bottom_menu_appear));


    }

    public int getStatusHeight() {
        Rect frame = new Rect();
        mActivity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        int statusHeight = frame.top;
        return statusHeight;
    }

}
