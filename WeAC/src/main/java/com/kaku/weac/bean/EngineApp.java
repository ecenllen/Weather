package com.kaku.weac.bean;

/**
 * Created by yingyongduoduo on 2018/4/11.
 */

public class EngineApp {
    private String versionName;
    private int versionCode;
    private String downloadUrl;
    private String packageName;
    private boolean showYQ;

    public boolean isShowYQ() {
        return showYQ;
    }

    public void setShowYQ(boolean showYQ) {
        this.showYQ = showYQ;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
