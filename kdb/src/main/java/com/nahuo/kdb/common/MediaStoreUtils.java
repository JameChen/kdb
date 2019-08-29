package com.nahuo.kdb.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.nahuo.kdb.model.MediaStoreImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZB on 2015/7/9 16:23
 */
public class MediaStoreUtils {

    /**
     *@author ZZB
     *@desc 获取最近的图片
     */
    public static List<MediaStoreImage> getRecentImages(Context context, int count){
        List<MediaStoreImage> imgs = new ArrayList<>();
        Cursor cursor = null;
        int counter = 0;
        try {
            String[] projections = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT};
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");
            int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int wIndex = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
            int hIndex = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
            while (cursor.moveToNext()) {
                if(counter < count){
                    String data = cursor.getString(pathIndex);
                    MediaStoreImage img = new MediaStoreImage(cursor.getString(pathIndex), cursor.getInt(wIndex), cursor.getInt(hIndex));
                    imgs.add(img);
                }else{
                    break;
                }
                counter ++;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imgs;
    }
    /**
     *@author ZZB
     *@desc 把图片文件加入到系统数据库，使类似于相册等应用可以看到
     */
    public static void scanImageFile(Context context, String path){
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.getApplicationContext().sendBroadcast(scanFileIntent);
    }
}
