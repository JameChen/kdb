package com.nahuo.kdb.mvp.view;

import com.nahuo.kdb.mvp.MvpView;
import com.nahuo.kdb.mvp.RequestData;
import com.nahuo.kdb.mvp.RequestError;

/**
 * Created by ZZB on 2015/8/10.
 */
public interface SignUpView extends MvpView{

    public void showLoading(RequestData requestData);
    public void hideLoading(RequestData requestData);
    public void onGetVerifyCodeSuccess();
    public void onGetVerifyCodeFailed(RequestError requestError);
    public void onValidateVerifyCodeSuccess();
    public void onValidateVerifyCodeFailed(RequestError requestError);
    public void onSignUpUserSuccess();
    public void onSignUpUserFailed(RequestError requestError);
    public void onSignUpShopSuccess();
    public void onSignUpShopFailed(RequestError requestError);


}
