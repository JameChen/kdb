package com.nahuo.kdb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.baidu.mobstat.StatService;
import com.nahuo.kdb.api.HttpRequestHelper;
import com.nahuo.kdb.api.HttpRequestListener;
import com.nahuo.kdb.mvp.MvpPresenter;
import com.nahuo.kdb.mvp.MvpView;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity1 extends BaseSlideBackActivity implements OnTitleClickListener, HttpRequestListener, MvpView {

    protected LoadingDialog mLoadingDialog;
    private AbstractActivity mAbsActivity;
    private List<CharSequence> mTitles;
    private boolean mBackClickNotFinish;
    protected HttpRequestHelper mRequestHelper = new HttpRequestHelper();
    private OnClickListener leftClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackClick(v);
            if (!mBackClickNotFinish)
                finish();
        }
    };
    private OnClickListener rightClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onRightClick(v);
        }
    };
    private OnClickListener searchClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onSearchClick(v);
        }
    };

    protected List<MvpPresenter> mPresenters = new ArrayList<MvpPresenter>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        for (MvpPresenter p : mPresenters) {
            p.attachView(this);
        }
        mAbsActivity = getAbsActivity();
        mAbsActivity.createContent(new View(this), this);
        mTitles = new ArrayList<CharSequence>();
        if (!mAbsActivity.isNoTitle()) {
            mAbsActivity.setLeftClickListener(leftClickListener);
            mAbsActivity.setRightClickListener(rightClickListener);
            mAbsActivity.setSearchClickListener(searchClickListener);
        }
        super.onCreate(savedInstanceState);
    }

    protected abstract AbstractActivity getAbsActivity();

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getArguments() != null
                && fragment.getArguments().getString(AbstractActivity.EXTRA_TITLE_DATA) != null) {
            onBindTitle(fragment.getArguments().getString(AbstractActivity.EXTRA_TITLE_DATA));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int index = getSupportFragmentManager().getBackStackEntryCount();
        if (mTitles.size() > index) {
            // mAbsActivity.setCustomeTitle(mTitles.get(index));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    public void onBindTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (mTitles != null) {
                mTitles.add(title);
            }
        }
    }

    public void showLeft(boolean show) {
        if (mAbsActivity != null) {
            mAbsActivity.showLeft(show);
        }
    }

    public void setLeftText(CharSequence text) {
        if (mAbsActivity != null) {
            mAbsActivity.setLeftText(text);
            mAbsActivity.showLeft(true);
        }
    }

    public void setLeftText(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setLeftText(resId);
            mAbsActivity.showLeft(true);
        }
    }

    public void setLeftIcon(int resid) {
        if (mAbsActivity != null) {
            mAbsActivity.setLeftIcon(resid);
        }
    }

    public void showRight(boolean show) {
        if (mAbsActivity != null) {
            mAbsActivity.showRight(show);
        }
    }

    public void setRightText(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightText(resId);
            mAbsActivity.showRight(true);
        }
    }

    public void setRightText(CharSequence text) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightText(text);
            mAbsActivity.showRight(true);
        }
    }

    public void setRightIcon01(int resid) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightIcon01(resid);
        }
    }
    public void setRightIcon02(int resid) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightIcon02(resid);
        }
    }
    public void showProgress(boolean show) {
        if (mAbsActivity != null) {
            mAbsActivity.showProgress(show);
        }
    }

    public void setLeftClickListener(View.OnClickListener listener) {
        if (mAbsActivity != null) {
            mAbsActivity.setLeftClickListener(listener);
        }
    }

    public void setRightClickListener(View.OnClickListener listener) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightClickListener(listener);
        }
    }
    public void setRight01ClickListener(View.OnClickListener listener) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightClick01Listener(listener);
        }
    }
    public void setRight02ClickListener(View.OnClickListener listener) {
        if (mAbsActivity != null) {
            mAbsActivity.setRightClick02Listener(listener);
        }
    }
    public void setTitle(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setTitleText(resId);
        }
    }
    public void setRightIcon(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setTitleText(resId);
        }
    }
    public void setTitle(CharSequence title) {
        if (mAbsActivity != null) {
            mAbsActivity.setTitleText(title);
        }
    }

    public void setTitleText(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setTitleText(resId);
        }
    }

    public void setTitleText(CharSequence text) {
        if (mAbsActivity != null) {
            mAbsActivity.setTitleText(text);
        }
    }

    public void showSearch(boolean show) {
        if (mAbsActivity != null) {
            mAbsActivity.showSearch(show);
        }
    }

    public void setSearchIcon(int resId) {
        if (mAbsActivity != null) {
            mAbsActivity.setSearchIcon(resId);
        }
    }

    public void showBackIcon(boolean show) {
        if (mAbsActivity != null) {
            mAbsActivity.showBackIcon(show);
        }
    }

    public void setBackClickNotFinishActivity() {
        this.mBackClickNotFinish = true;
    }

    // public HttpRequestHelper initRequest(String method) {
    // HttpRequestHelper httpRequest = new HttpRequestHelper(getApplicationContext(), method, this);
    // return httpRequest;
    // }

    @Override
    public void onBackClick(View v) {
    }

    @Override
    public void onRightClick(View v) {
    }

    @Override
    public void onSearchClick(View v) {
    }

    @Override
    public void onRequestStart(String method) {
    }

    @Override
    public void onRequestSuccess(String method, Object object) {
        // 子类如果不需要弹窗消失，不要调用super.onRequestSuccess
        hideDialog();
    }

    @Override
    public void onRequestFail(String method, int statusCode, String msg) {
        hideDialog();// 子类如果不需要弹窗消失，不要调用super.onRequestFail
    }

    @Override
    public void onRequestExp(String method, String msg) {
        hideDialog();// 子类如果不需要弹窗消失，不要调用super.onRequestExp
    }

    protected boolean isDialogShowing() {
        return (mLoadingDialog != null && mLoadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mLoadingDialog.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mRequestHelper.cancelRequests();//自己在子类实现
        for (MvpPresenter p : mPresenters) {
            p.detachView(false);
        }
    }
}
