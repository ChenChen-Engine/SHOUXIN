package com.yuntongxun.ecdemo.net;

import com.yuntongxun.ecdemo.bean.CheckRegisterBean;
import com.yuntongxun.ecdemo.bean.CreateGroupBean;
import com.yuntongxun.ecdemo.bean.ForgetBean;
import com.yuntongxun.ecdemo.bean.GetGroupLevelBean;
import com.yuntongxun.ecdemo.bean.LoginBean;
import com.yuntongxun.ecdemo.bean.RegisterBean;
import com.yuntongxun.ecdemo.bean.ResetNickNameBean;
import com.yuntongxun.ecdemo.bean.UpdateHeadBean;
import com.yuntongxun.ecdemo.bean.VerifyCodeBean;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface Api {
    String BASE_URL = "http://admin.sxinim.com/yun/";

    //通过速讯号搜索用户
    @POST("api-login")
    @FormUrlEncoded
    Observable<LoginBean> login(@FieldMap Map<String, Object> map);

    /*获取验证码*/
    @POST("msg-register")
    @FormUrlEncoded
    Observable<VerifyCodeBean> getVerifyMsg(@FieldMap Map<String, Object> map);

    /*获取验证码*/
    @POST("register")
    @FormUrlEncoded
    Observable<RegisterBean> register(@FieldMap Map<String, Object> map);

    /*忘记密码*/
    @POST("forget-password")
    @FormUrlEncoded
    Observable<ForgetBean> forget(@FieldMap Map<String, Object> map);

    /*设置昵称*/
    @POST("reset-name")
    @FormUrlEncoded
    Observable<ResetNickNameBean> resetNickName(@FieldMap Map<String, Object> map);

    /*判断用户是否注册*/
    @POST("check-register")
    @FormUrlEncoded
    Observable<CheckRegisterBean> checkRegister(@FieldMap Map<String, Object> map);

    /*创建群组*/
    @POST("create-group")
    @FormUrlEncoded
    Observable<CreateGroupBean> createGroup(@FieldMap Map<String, Object> map);/*创建群组*/

    /*获取群等级*/
    @POST("get-group-level")
    @FormUrlEncoded
    Observable<GetGroupLevelBean> getGroupLevel(@FieldMap Map<String, Object> map);

    /*设置是否公开群*/
    @POST("group-open")
    @FormUrlEncoded
    Observable<GetGroupLevelBean> isOpenGroup(@FieldMap Map<String, Object> map);

    /*设置是否公开群*/
    @Multipart
    @POST("upload-litpic")
    Observable<UpdateHeadBean> uploadGroupPicture(@QueryMap Map<String,Object> map , @Part MultipartBody.Part file);

}
