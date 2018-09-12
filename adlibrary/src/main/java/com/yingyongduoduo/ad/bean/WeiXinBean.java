package com.yingyongduoduo.ad.bean;

/**
 * Created by dkli on 2017/12/28.
 */

public class WeiXinBean {

    private String parentid;
    private String nameid;
    private String messageid;
    private String timeid;
    private String senderid;
    private String moneyid;

    public String getMoneyid() {
        return moneyid;
    }

    public void setMoneyid(String moneyid) {
        this.moneyid = moneyid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getNameid() {
        return nameid;
    }

    public void setNameid(String nameid) {
        this.nameid = nameid;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getTimeid() {
        return timeid;
    }

    public void setTimeid(String timeid) {
        this.timeid = timeid;
    }
}
