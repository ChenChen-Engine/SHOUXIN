package com.yuntongxun.ecdemo.bean;

public class RegisterBean {

    /**
     * statusCode : 1
     * statusMsg : 短信验证码错误或失效
     */

    private int statusCode;
    private String statusMsg;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }
}
