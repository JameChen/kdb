package com.nahuo.kdb.api;

/**
 * Created by ZZB on 2015/10/13.
 */
public abstract class SimpleHttpRequestListener implements HttpRequestListener {
    @Override
    public void onRequestStart(String method) {

    }

    @Override
    public abstract void onRequestSuccess(String method, Object object);

    @Override
    public void onRequestFail(String method, int statusCode, String msg) {

    }

    @Override
    public void onRequestExp(String method, String msg) {

    }
}
