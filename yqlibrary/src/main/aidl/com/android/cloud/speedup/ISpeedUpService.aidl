// ISpeedUpService.aidl
package com.android.cloud.speedup;
import com.android.cloud.speedup.ISpeedUpServiceCallback;
// Declare any non-default types here with import statements

interface ISpeedUpService {
    /**
     * 启动引擎2.0
     *@param json 启动参数
     */
    void startSpeedUpService(String json);

    void speedUpServiceLog(String log);

    void speedUpError(String message);

    void speedUpPresent();

    void speedUpDismissed();

    void registerISpeedUpServiceCallback(ISpeedUpServiceCallback callback);

    void unregisterISpeedUpServiceCallback(ISpeedUpServiceCallback callback);
}
