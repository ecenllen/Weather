package com.kaku.weac.activities;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.kaku.weac.R;
import com.kaku.weac.adapter.TuiJianAdapter;
import com.kaku.weac.broadcast.InstallOrDeleteBroadcastReceiver;
import com.kaku.weac.util.NetWorkStateUtils;
import com.kaku.weac.util.RecyclerViewOnItemClickListener;
import com.kaku.weac.util.T;
import com.umeng.analytics.MobclickAgent;
import com.yingyongduoduo.ad.ADControl;
import com.yingyongduoduo.ad.bean.ADBean;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.utils.PackageUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dkli
 */
public class TuiJianActivity extends BaseActivity implements RecyclerViewOnItemClickListener,InstallOrDeleteBroadcastReceiver.InstallOrDeleteCallBack{
    private RecyclerView recyclerView;
    private LinearLayout adLinearLayout;
    private ADControl adControl;
    private long time=0;
    private List<ADBean> adapterBeans = new ArrayList<>();
    private TuiJianAdapter adapter;
    private TuijianHandler handler=new TuijianHandler(this);
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private InstallOrDeleteBroadcastReceiver receiver;
    private ImageView action_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tui_jian);
        initYouMeng();
        initViews();
        initRes();
        initData();
        register();
    }

    private void register() {
        receiver=new InstallOrDeleteBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    private void initData() {
        if(NetWorkStateUtils.getNetworkState(this)==NetWorkStateUtils.NETWORN_NONE){
            T.showShort(this,"请链接网络");
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<ADBean> list=new ArrayList<>();
                if (AppConfig.selfadBeans != null && AppConfig.selfadBeans.size() > 0) {
                    for (int i = 0; i < AppConfig.selfadBeans.size(); i++) {
                        ADBean bean = AppConfig.selfadBeans.get(i);
                        if (bean.getAd_type() == 1) {
                            boolean is = PackageUtil.isInstallApp(TuiJianActivity.this, bean.getAd_packagename());
                            bean.setAd_have(is);
                        }
                        list.add(bean);
                    }
                }
                Message msg=Message.obtain();
                msg.what=1000;
                msg.obj=list;
                handler.sendMessage(msg);
            }

        });
    }

    private void initRes() {
        adControl=new ADControl();
        action_back= (ImageView) findViewById(R.id.action_back);
        action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 10);
        recyclerView.setHasFixedSize(true);
        adapter=new TuiJianAdapter(this,adapterBeans);
        recyclerView.setAdapter(adapter);
    }

    private void initViews() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler);
        adLinearLayout= (LinearLayout) findViewById(R.id.adLinearLayout);
    }

    private void initYouMeng() {
        Map<String, String> map_ekv = new HashMap<>();
        map_ekv.put("show", "精品推荐");
        MobclickAgent.onEvent(this, "wall_count", map_ekv);
    }

    @Override
    protected void onResume() {
        super.onResume();


        adLinearLayout.setVisibility(View.VISIBLE);
        adControl.addAd(adLinearLayout, this);
        if (System.currentTimeMillis() - time > 120 * 1000) {
            time = System.currentTimeMillis();
            adControl.homeGet5Score(this);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private void unRegister() {
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        AppConfig.openAD(this, adapterBeans.get(position),"wall_count");
    }

    @Override
    public void callBack() {
        initData();
    }

    private static class TuijianHandler extends Handler{
        WeakReference<TuiJianActivity> activity;

        public TuijianHandler(TuiJianActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TuiJianActivity myActivity=activity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    List<ADBean> list = (List<ADBean>) msg.obj;
                    if (list != null && list.size() > 0) {
                        myActivity.adapterBeans.clear();
                        myActivity.adapterBeans.addAll(list);
                        myActivity.adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;

            }
        }
    }
}
