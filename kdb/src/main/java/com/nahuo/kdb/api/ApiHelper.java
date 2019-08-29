package com.nahuo.kdb.api;

import android.app.Activity;
import android.content.Intent;

import com.nahuo.kdb.LoginActivity;
import com.nahuo.kdb.common.BaiduStats;
import com.nahuo.kdb.common.Utils;

public class ApiHelper {

	/**
	 * 判断api调用后的返回值的，出现一些指定问题进行某些跳转操作
	 * 
	 * @author peng jun
	 * 
	 * */
	public static void checkResult(Object result, Activity activity) {
	    if(activity == null) {
	        return;
	    }
	    try {
	        String pkgName = activity.getPackageName();
            String version = activity.getPackageManager().getPackageInfo(pkgName,0).versionName;
            BaiduStats.log(activity, BaiduStats.EventId.AUTH_EXPIRED, "版本号是：" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
		if (result instanceof String) {
			if (((String) result).startsWith("401") || ((String) result).startsWith("not_registered")) {// 进入登录页
			    if(Utils.isCurrentActivity(activity, LoginActivity.class)){
			        return;
			    }
			    Intent intent = new Intent(activity, LoginActivity.class);
	            activity.startActivity(intent);
	            activity.finish();
			}
			
		}
	}
	
}
