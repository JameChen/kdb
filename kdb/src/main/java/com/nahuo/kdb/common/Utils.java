package com.nahuo.kdb.common;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class Utils {

    private static DecimalFormat mMoneyDecimalFormat;

    /**
     * @description 获取版本号
     * @created 2015-4-2 下午2:19:02
     * @author ZZB
     */
    public static String getAppVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "无法获取到版本号";
        }
        return versionName;
    }

    /**
     * @description bitmap转为数组
     * @created 2014-11-27 下午2:06:21
     * @author ZZB
     */
    public static byte[] bitmapToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @description 添加到剪切版
     * @created 2014-11-14 下午4:52:53
     * @author ZZB
     */
    public static void addToClipboard(Context context, String text) {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        cmb.setPrimaryClip(clip);
    }

    /**
     * Description:两个数组合并 2014-7-22 下午3:48:51
     * 
     * @author ZZB
     */
    public static int[] joinArr(int[] a, int[] b) {

        int[] newArr = new int[b.length + a.length];
        System.arraycopy(a, 0, newArr, 0, a.length);
        // src ,start, dest, start, dest len
        System.arraycopy(b, 0, newArr, a.length, b.length);
        return newArr;

    }

    /**
     * @description 判断是否是当前activity
     * @created 2014-9-30 上午10:35:59
     * @author ZZB
     */
    public static boolean isCurrentActivity(Context context, Class<?> cls) {
        if(context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cls.getSimpleName().equals(cn.getClassName());
    }

    public static String moneyFormat(double money) {
        try {
            if (mMoneyDecimalFormat == null) {
                mMoneyDecimalFormat = new DecimalFormat("#0.00");
            }
            return mMoneyDecimalFormat.format(money);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
