package com.yuntongxun.ecdemo.net;

import com.yuntongxun.ecdemo.bean.CheckRegisterBean;
import com.yuntongxun.ecdemo.bean.CreateGroupBean;
import com.yuntongxun.ecdemo.bean.ForgetBean;
import com.yuntongxun.ecdemo.bean.GetGroupLevelBean;
import com.yuntongxun.ecdemo.bean.LoginBean;
import com.yuntongxun.ecdemo.bean.RegisterBean;
import com.yuntongxun.ecdemo.bean.ResetNickNameBean;
import com.yuntongxun.ecdemo.bean.VerifyCodeBean;
import com.yuntongxun.ecdemo.utils.MD5Util;

import java.util.Map;

public class Net {

    public static void getVerifyMsg(String phone, String type, BaseObserver<VerifyCodeBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("phone", phone)
                .put("type", type)
                .put("sign", sign(phone))
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).getVerifyMsg(build), observer);
    }

    public static void login(String phone, String password, BaseObserver<LoginBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("phone", phone)
                .put("password", password)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).login(build), observer);
    }

    public static void register(String sxNum,String phone, String pwd, String code, BaseObserver<RegisterBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("shouxin", sxNum)
                .put("phone", phone)
                .put("username", phone)
                .put("password", pwd)
                .put("msgcode", code)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).register(build), observer);
    }

    public static void forget(String phone, String newPwd, String code, BaseObserver<ForgetBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("phone", phone)
                .put("new_password", newPwd)
                .put("msgcode", code)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).forget(build), observer);
    }

    public static void resetNickName(String phone, String username, BaseObserver<ResetNickNameBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("phone", phone)
                .put("username", username)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).resetNickName(build), observer);
    }

    public static void checkRegister(String phone, BaseObserver<CheckRegisterBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("phone", phone)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).checkRegister(build), observer);
    }

    public static void createGroup(String groupID, BaseObserver<CreateGroupBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("group_id", groupID)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).createGroup(build), observer);
    }

    public static void getGroupLevel(String groupID, BaseObserver<GetGroupLevelBean> observer) {
        Map<String, Object> build = new MapUtils()
                .put("group_id", groupID)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).getGroupLevel(build), observer);
    }

    public static void isOpenGroup(String groupID , int isopen,BaseObserver<GetGroupLevelBean> observer){
        Map<String, Object> build = new MapUtils()
                .put("group_id", groupID)
                /*0 = Close 1 = Open*/
                .put("isopen", isopen)
                .build();
        RetrofitClient.request(RetrofitClient.create(Api.class).getGroupLevel(build), observer);
    }

    private static String sign(String phone) {
        return MD5Util.crypt("yunmsg:" + phone).toUpperCase();
    }

}
