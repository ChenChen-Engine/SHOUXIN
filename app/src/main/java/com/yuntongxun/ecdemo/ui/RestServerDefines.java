package com.yuntongxun.ecdemo.ui;

import com.yuntongxun.ecdemo.BuildConfig;
import com.yuntongxun.ecdemo.R;

/**
 * Created by luhuashan on 17/6/9.
 */
public class RestServerDefines {

    public static int APP_VERSION = 541;

    public static  final String SERVER = "https://imapp.yuntongxun.com:443";
        public static  final String Friend = "https://imapp.yuntongxun.com:443";

    public static final String VERSION = BuildConfig.VERSION_NAME;


    public static final boolean QR_APK = false;

    public static final boolean IM = false;
    public static final String APPKER = RestServerDefines.QR_APK ? RestServerDefines.APPKER_CODE : RestServerDefines.APPKER_WEB;

    public static final String APPKER_WEB = "8aaf0708656141010165661ae9a703db";//demo appkey

    //小军
    public static final String APPKER_CODE = "8aaf0708656141010165661ae9a703db";
    public static final String TOKEN = "893e949aba5c5211ab1df04be773fd76";


    public static final int[] arr = new int[]{R.drawable.def_usericon, R.drawable.def_usericon_two, R.drawable.def_usericon_three, R.drawable.def_usericon_four, R.drawable.def_usericon_five, R.drawable.def_usericon_six, R.drawable.def_usericon_seven, R.drawable.def_usericon_eight};

    public static final String FILE_ASSISTANT = "~ytxfa";
    public static  String ROBOT = "";
}
