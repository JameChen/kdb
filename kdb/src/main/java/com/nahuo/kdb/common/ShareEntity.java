package com.nahuo.kdb.common;
import android.graphics.Bitmap;

import com.nahuo.library.helper.ImageUrlExtends;

import java.io.Serializable;
/**
 * @description 分享实体
 * @created 2014-11-21 下午5:08:00
 * @author ZZB
 */
public class ShareEntity implements Serializable {
    private String            title;
    private String            summary;
    private String            targetUrl;
    private String            imgUrl;
    private String            appName;
    private String            extra;
    private byte[]            thumData;
    private String miniAppUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public byte[] getThumData() {
        return thumData;
    }

    public void setThumData(Bitmap bitmap) {
        try {
            this.thumData = Utils.bitmapToByteArray(bitmap, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getMiniAppUrl() {
        return miniAppUrl;
    }

    public void setMiniAppUrl(String miniAppUrl) {
        this.miniAppUrl = miniAppUrl;
    }


    public ShareEntity() {
        setTitle("扮窝-附近的服装店");
        setSummary("扮窝-附近的服装店");
        String tmpImgUrl = "http://pic.58pic.com/58pic/14/27/45/71r58PICmDM_1024.jpg";//分享图片配置大小
        String imageUrl = ImageUrlExtends.getImageUrl(tmpImgUrl, 1);
        setImgUrl(imageUrl);
        setTargetUrl("https://www.baidu.com/");
       // setMiniAppUrl("pages/pinhuo/itemdetail?id=" + String.valueOf(10001));
        setMiniAppUrl("pages/shop/shop?id=" + String.valueOf(33306));
    }
}
