package com.nahuo.kdb.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.nahuo.kdb.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ALAN on 2017/5/4 0004.
 */
public class WeChatShareUtil {
    //分享微信小程序
    public static SendMessageToWX.Req shareMiNiAppToWX(Context context, String defaultUrl, String miniAppId, String appPath, String title, String desc, ShareEntity share) throws Exception {
        WXMiniProgramObject miniProgramObject = new WXMiniProgramObject();
        miniProgramObject.webpageUrl = defaultUrl;
        miniProgramObject.userName = miniAppId;
        miniProgramObject.path = appPath;
        WXMediaMessage msg = new WXMediaMessage(miniProgramObject);
        msg.description = desc;

        msg.title = title;
        Bitmap bmp = null;
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.banwo_logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 50, 50, true);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        // msg.thumbData=getImageFromURL(share.getImgUrl());
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return req;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                // F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }


    /**
     * 根据图片网络地址获取图片的byte[]类型数据
     *
     * @param urlPath 图片网络地址
     * @return 图片数据
     */

    //Java代码
    public static byte[] getImageFromURL(String urlPath) {
        byte[] data = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            // conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            is = conn.getInputStream();
            if (conn.getResponseCode() == 200) {
                data = readInputStream(is);
            } else {
                data = null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return data;
    }


    public static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


}
