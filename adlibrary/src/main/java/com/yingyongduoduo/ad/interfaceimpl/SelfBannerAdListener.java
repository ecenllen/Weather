package com.yingyongduoduo.ad.interfaceimpl;


import com.yingyongduoduo.ad.bean.ADBean;

/**
 * Created by yuminer on 2017/5/26.
 */
public interface SelfBannerAdListener {
    void onAdClick(ADBean adBean);
    void onAdFailed();
    void onADReceiv(ADBean adBean);
}
