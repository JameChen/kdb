package com.nahuo.kdb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jame on 2018/9/18.
 */

public class CodeBean {

    /**
     * QrPayCode : https://pay.weixin.qq.com/wiki/doc/api/img/chapter6_5_2.png
     */
    @Expose
    @SerializedName("QrPayCode")
    private String QrPayCode = "";

    public String getQrPayCode() {
        return QrPayCode;
    }

    public void setQrPayCode(String QrPayCode) {
        this.QrPayCode = QrPayCode;
    }
}
