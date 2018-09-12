
package com.kaku.weac.activities;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaku.weac.R;
import com.kaku.weac.util.GetVersionCodeUtils;

import com.kaku.weac.util.MyUtil;
import com.yingyongduoduo.ad.ADControl;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.interfaceimpl.KPAdListener;
import com.yingyongduoduo.ad.utils.IData;

import java.io.File;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 咖枯
 * @version 1.0 2016/2/22
 */

public class SplashActivity extends BaseActivity {
    private static final String LOG_TAG = "SplashActivity";
    private ADControl adControl;
    private TextView txtversiton;
    private RelativeLayout adLayout;
    public boolean waitingOnRestart = false;
    private KPAdListener listener = new KPAdListener() {
        @Override
        public void onAdDismissed() {
            jumpWhenCanClick();// 跳转至您的应用主界面
        }

        @Override
        public void onAdFailed(String arg0) {
            adLayout.setVisibility(View.GONE);
            jump();
        }

        @Override
        public void onAdPresent() {

        }

        @Override
        public void onAdClick() {

            // 设置开屏可接受点击时，该回调可用
        }
    };
    private ThreadPoolExecutor executorService=new ThreadPoolExecutor(3,5,1L, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 解决初次安装后打开后按home返回后重新打开重启问题。。。
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }

        // 禁止滑动后退
        setSwipeBackEnable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        MyUtil.setStatusBarTranslucent(this);
        initConfig();
        initView();
    }

    private void initView() {
        adLayout=(RelativeLayout) findViewById(R.id.ad_layout);
    }

    private void initConfig() {
        adControl=new ADControl();
        ApplicationInfo appInfo;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.versioncode= GetVersionCodeUtils.getVersionCode(this.getApplicationContext());
            AppConfig.APPKEY = appInfo.metaData.getString("UMENG_APPKEY");
            AppConfig.Channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // 初始化引擎存放位置
        AppConfig.youkulibPath = getCacheDir() + File.separator + "videoparse.jar";
        AppConfig.appstorePath=getCacheDir()+File.separator+"appstore.jar";
        // 公众号的目录不能用缓存目录
        AppConfig.GZHPath = IData.DEFAULT_GZH_CACHE ;
        AppConfig.InitLocal(this);
        adControl.initAll(SplashActivity.this);
        ADControl.lastshowadTime = 0;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppConfig.Init(SplashActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (AppConfig.isShowKP()) {

                            adControl.ShowKp(SplashActivity.this, adLayout, listener);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitingOnRestart) {
            jumpWhenCanClick();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void jump() {
        adControl.initAll(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 1000);
    }
    private void jumpWhenCanClick() {
        Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (this.hasWindowFocus() || waitingOnRestart) {
            adControl.initAll(SplashActivity.this);
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            SplashActivity.this.finish();
        } else {
            waitingOnRestart = true;
        }

    }


}
