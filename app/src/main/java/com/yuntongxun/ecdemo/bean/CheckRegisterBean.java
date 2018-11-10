package com.yuntongxun.ecdemo.bean;

public class CheckRegisterBean {

    private int statusCode;
    private int isRegister;
    private String statusMsg;
    private String shouxin;

    public String getShouxin() {
        return shouxin;
    }

    public void setShouxin(String shouxin) {
        this.shouxin = shouxin;
    }

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

    public int getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(int isRegister) {
        this.isRegister = isRegister;
    }
}
