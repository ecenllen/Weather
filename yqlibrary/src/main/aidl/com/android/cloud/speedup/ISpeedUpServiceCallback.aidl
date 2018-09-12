// ISpeedUpServiceCallback.aidl
package com.android.cloud.speedup;

// Declare any non-default types here with import statements

interface ISpeedUpServiceCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onSpeedUpServiceMessage(String json);
    /**
      * 广告剩余时间
      */
    void onSpeedUpServiceLessTime(long time);

    void onSpeedUpServiceDismissed();

    void onSpeedUpServicePresent();

    void onSpeedUpServiceError(String message);
}
