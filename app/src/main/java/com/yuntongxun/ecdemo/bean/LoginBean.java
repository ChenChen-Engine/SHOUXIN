package com.yuntongxun.ecdemo.bean;

public class LoginBean {

    private int statusCode;
    private String statusMsg;
    private DataBean data;


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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String username;
        private String phone;
        private String shouxin;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getShouxin() {
            return shouxin;
        }

        public void setShouxin(String shouxin) {
            this.shouxin = shouxin;
        }
    }
}
