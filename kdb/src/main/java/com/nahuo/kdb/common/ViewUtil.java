package com.nahuo.kdb.common;

import android.graphics.Bitmap;
import android.view.View;

/***
 * @description View相关小工具
 * @created 2015年5月5日 下午3:42:33
 * @author JorsonWong
 */
public class ViewUtil {

    /***
     * @description 获取View的Bitmap
     * @created 2015年5月5日 下午3:42:29
     * @author JorsonWong
     */
    public static Bitmap shotView(View view) {

        view.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        if (bmp != null) {
            return bmp;
        }
        return null;
    }
}
