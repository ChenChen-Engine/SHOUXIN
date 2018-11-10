package com.yuntongxun.ecdemo.bean;

public class GetGroupLevelBean {
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
        /**
         * group_level : 1
         */

        private int group_level;

        public int getGroup_level() {
            return group_level;
        }

        public void setGroup_level(int group_level) {
            this.group_level = group_level;
        }
    }
}
