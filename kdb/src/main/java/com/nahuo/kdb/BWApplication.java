package com.nahuo.kdb;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nahuo.kdb.api.HttpUtils;
import com.nahuo.kdb.common.Debug;
import com.nahuo.kdb.hx.DemoHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BWApplication extends Application {

    public static  int PrinterId=0;
    private static BWApplication instance;

    public static BWApplication getInstance() {
        return instance;
    }
    public static List<Activity> pList = new ArrayList<>();
    private HttpClient httpClient;
    public static void addActivity(Activity activity) {
        if (!pList.contains(activity)) {
            pList.add(activity);
        }
    }
    public static void reBackFirst() {
        try {
            for (Activity activity : pList) {
                activity.finish();
            }
            pList.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")) {
            // workaround for baidu location sdk
            // 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
            // 创建新的进程。
            // 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
            // processName，
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        instance = this;
        Context context = getApplicationContext();
        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(5)
                // default 3
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // defaul
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .memoryCacheSize(20 * 1024 * 1024)
                .memoryCacheSizePercentage(30)
                // default
                .discCache(new UnlimitedDiscCache(cacheDir))
                // default
                .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
                .discCacheFileNameGenerator(new Md5FileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();

        // Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(config);

        httpClient = this.createHttpClient();        // 获取到EMChatOptions对象
//        EMOptions options = new EMOptions();
//        // set if accept the invitation automatically
//        options.setAcceptInvitationAlways(false);
//        // set if you need read ack
//        options.setRequireAck(true);
//        // set if you need delivery ack
//        options.setRequireDeliveryAck(false);
//        EaseUI.getInstance().init(this, options);
        if (HttpUtils.ECC_OPEN) {
            DemoHelper.getInstance().init(instance);
        }
        Debug.init(this);

    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = i.next();
            try {
                if (info.pid == pID) {
                    pm.getApplicationLabel(pm.getApplicationInfo(
                            info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("BWApplication", e.toString());
            }
        }
        return processName;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.shutdownHttpClient();
    }

    // 创建HttpClient实例
    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
                params, schReg);

        return new DefaultHttpClient(connMgr, params);
    }

    // 关闭连接管理器并释放资源
    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    // 对外提供HttpClient实例
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
