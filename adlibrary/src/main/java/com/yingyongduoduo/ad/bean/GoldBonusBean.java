package com.yingyongduoduo.ad.bean;

import java.io.Serializable;

/**
 * Created by dkli on 2018/1/26.
 */

public class GoldBonusBean implements Serializable{

    private String img;
    private String name;
    private String desc;
    private String needGold;
    private String needMonney;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNeedGold() {
        return needGold;
    }

    public void setNeedGold(String needGold) {
        this.needGold = needGold;
    }

    public String getNeedMonney() {
        return needMonney;
    }

    public void setNeedMonney(String needMonney) {
        this.needMonney = needMonney;
    }
}
