package com.yuntongxun.ecdemo.bean;

public class VerifyCodeBean {

    /**
     * statusCode : 0
     * data : 短信验证码29733发送成功,请查收
     */

    private int statusCode;
    private String data;
    private String statusMsg;

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
