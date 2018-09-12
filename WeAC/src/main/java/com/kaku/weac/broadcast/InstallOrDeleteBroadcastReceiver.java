package com.kaku.weac.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by dkli on 2017/12/6.
 * @author dkli
 */

public class InstallOrDeleteBroadcastReceiver extends BroadcastReceiver {
    private InstallOrDeleteCallBack callBack;

    public InstallOrDeleteBroadcastReceiver(InstallOrDeleteCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
         if (intent == null) {
            return;
        }
        String url= String.valueOf(intent.getData());
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())||Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            callBack.callBack();
        }
    }
    public interface InstallOrDeleteCallBack{
        void callBack();
    }
}
