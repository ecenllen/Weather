package com.yingyongduoduo.ad.interfaceimpl;

/**
 * Created by yuminer on 2017/5/26.
 */
public interface KPAdListener {
    void onAdPresent();

    void onAdDismissed();

    void onAdFailed(String var1);

    void onAdClick();
}
