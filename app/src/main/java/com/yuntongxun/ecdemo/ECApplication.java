/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.yuntongxun.ecdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.load.model.GlideUrl;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.FileAccessor;
import com.yuntongxun.ecdemo.common.utils.GlideCacheUtil;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.huawei.PustDemoActivity;
import com.yuntongxun.ecdemo.ui.xiaomi.DemoMessageReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.List;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Jorstin on 2015/3/17.
 */
public class ECApplication extends Application {

    public static final String TAG = "ECApplication";
    private static ECApplication instance;

    private static PustDemoActivity mPustTestActivity = null;

    private static DemoMessageReceiver.DemoHandler handler = null;

    public static boolean NEW_VERSION = false;

    public static int version = 0;
    public static  String  nick;
    public static String sign;

    public static String URL ="";

    public static String photoUrl = "";



    /**
     * 单例，返回一个实例
     * @return
     */
    public static ECApplication getInstance() {
        if (instance == null) {
            LogUtil.w("[ECApplication] instance is null.");
        }
        return instance;
    }

    public static final String APP_ID = "2882303761517618690";
    // user your appid the key.
    public static final String APP_KEY = "5561761832690";

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        initBugly();//初始化Bugly

        CCPAppManager.setContext(instance);
        FileAccessor.initFileAccess();
        setChattingContactId();
        initImageLoader();
        if (shouldInit()) {
//            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        try {
//            SDKInitializer.initialize(instance);
            clearImageCache();
        } catch (Exception e) {
            e.printStackTrace();
        }



        if (handler == null) {
            handler = new DemoMessageReceiver.DemoHandler(getApplicationContext());
        }


    }
    /**
     * 初始化Bugly
     */
    private void initBugly() {
//        Context context = getApplicationContext();
//        // 获取当前包名
//        String packageName = context.getPackageName();
//        // 获取当前进程名
//        String processName = getProcessName(android.os.Process.myPid());
//        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), "4f396a8bbb", true);
//        Bugly.init(getApplicationContext(), "200d5fbb61", false);
    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    private boolean shouldInit(){
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static DemoMessageReceiver.DemoHandler getHandler() {
        return handler;
    }





    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * 保存当前的聊天界面所对应的联系人、方便来消息屏蔽通知
     */
    private void setChattingContactId() {
        try {
            ECPreferences.savePreference(ECPreferenceSettings.SETTING_CHATTING_CONTACTID, "", true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }
    }

    public void setMainActivity(PustDemoActivity activity) {
        mPustTestActivity = activity;
    }

    public PustDemoActivity getMainActivity() {
        return mPustTestActivity;
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "ECSDK_Demo2/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .threadPoolSize(1)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                // .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(CCPAppManager.md5FileNameGenerator)
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir ,null ,CCPAppManager.md5FileNameGenerator))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);
    }

    private void clearImageCache(){
        GlideCacheUtil.getInstance().clearImageAllCache(this);
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * 返回配置文件的日志开关
     * @return
     */
    public boolean getLoggingSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("LOGGING");
            LogUtil.w("[ECApplication - getLogging] logging is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean getAlphaSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("ALPHA");
            LogUtil.w("[ECApplication - getAlpha] Alpha is: " + b);
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
