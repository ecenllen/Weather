package com.kaku.weac.util;

import android.text.TextUtils;

/**
 * Created by yingyongduoduo on 2018/5/15.
 */

public class UrlUtil {

    /**
     * 检查URL前缀是否包含http、https、file://
     * @param url
     * @return
     */
    public static boolean checkUrlPrefix(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("http") || url.startsWith("file://")) {
                return true;
            }
        }

        return false;
    }
}
