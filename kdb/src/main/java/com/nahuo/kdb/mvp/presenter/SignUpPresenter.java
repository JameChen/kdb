package com.nahuo.kdb.mvp.presenter;

import android.content.Context;

import com.nahuo.kdb.api.AccountAPI;
import com.nahuo.kdb.api.HttpRequestHelper;
import com.nahuo.kdb.api.HttpRequestListener;
import com.nahuo.kdb.api.RequestMethod;
import com.nahuo.kdb.mvp.MvpBasePresenter;
import com.nahuo.kdb.mvp.RequestData;
import com.nahuo.kdb.mvp.RequestError;
import com.nahuo.kdb.mvp.view.SignUpView;

/**
 * Created by ZZB on 2015/8/10.
 */
public class SignUpPresenter extends MvpBasePresenter<SignUpView> {
    private HttpRequestHelper mRequestHelper = new HttpRequestHelper();
    public SignUpPresenter(Context context){
        super(context);
    }
    /**
     * 获取验证码
     *@author ZZB
     *created at 2015/8/10 14:54
     */
    public void getVerifyCode(String phoneNo,String username){
        Context context = null;

        if(isViewAttached() && isContextNotNull()){
            context = getContext();
        }else{
            return;
        }
        AccountAPI.getSignUpVerifyCode(context, mRequestHelper, new HttpRequestListener() {
            @Override
            public void onRequestStart(String method) {
                if(isViewAttached()){
                    getView().showLoading(new RequestData(RequestMethod.UserMethod.GET_SIGN_UP_VERIFY_CODE));
                }
            }

            @Override
            public void onRequestSuccess(String method, Object object) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onGetVerifyCodeSuccess();
                }
            }

            @Override
            public void onRequestFail(String method, int statusCode, String msg) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onGetVerifyCodeFailed(new RequestError(method, statusCode, msg, true));
                }
            }

            @Override
            public void onRequestExp(String method, String msg) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onGetVerifyCodeFailed(new RequestError(method, msg));
                }
            }
        }, phoneNo,username);
    }

    /**
     * 提交验证码，进行校验
     *@author ZZB
     *created at 2015/8/10 15:08
     */
    public void submitVerifyCode(String phoneNo, String verifyCode) {
        Context context = null;

        if(isViewAttached() && isContextNotNull()){
            context = getContext();
        }else{
            return;
        }
        AccountAPI.validateSignUpVerifyCode(context, mRequestHelper, new HttpRequestListener() {
            @Override
            public void onRequestStart(String method) {
                if(isViewAttached()){
                    getView().showLoading(null);
                }
            }

            @Override
            public void onRequestSuccess(String method, Object object) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onValidateVerifyCodeSuccess();
                }
            }

            @Override
            public void onRequestFail(String method, int statusCode, String msg) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onValidateVerifyCodeFailed(new RequestError(method, statusCode, msg, true));
                }
            }

            @Override
            public void onRequestExp(String method, String msg) {
                if(isViewAttached()){
                    getView().hideLoading(null);
                    getView().onValidateVerifyCodeFailed(new RequestError(method, msg));
                }
            }
        }, phoneNo, verifyCode);
    }
}
