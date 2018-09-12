package com.kaku.weac.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.kaku.weac.R;
import com.kaku.weac.cache.IMemoryCache;
import com.kaku.weac.cache.VideoMemoryCache;
import com.kaku.weac.fragment.CpuwebFrag;
import com.viewpagerindicator.TabPageIndicator;
import com.yingyongduoduo.ad.ADControl;
import com.yingyongduoduo.ad.bean.WXGZHBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索界面
 *
 * @author duoduoapp
 */
public class CpuWebActivity extends BaseActivity{

    private ViewPager pager;
    private TabPageIndicator indicator;
    private FragmentManager fManager;
    private FragmentPagerAdapter adapter;
    private IMemoryCache<Fragment> iMemoryCache = new VideoMemoryCache<Fragment>();
    private int currentPage = 0;
    private ImageView action_back;
    private ADControl adControl;
    private LinearLayout adLinearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.cpuwebactivity);
        CONTENT.add(new SpinnerItem("娱乐", "1001"));
        CONTENT.add(new SpinnerItem("体育", "1002"));
        CONTENT.add(new SpinnerItem("图片", "1003"));
        CONTENT.add(new SpinnerItem("手机", "1005"));
        CONTENT.add(new SpinnerItem("财经", "1006"));
        CONTENT.add(new SpinnerItem("汽车", "1007"));
        CONTENT.add(new SpinnerItem("房产", "1008"));
        CONTENT.add(new SpinnerItem("时尚", "1009"));
        CONTENT.add(new SpinnerItem("军事", "1012"));
        CONTENT.add(new SpinnerItem("科技", "1013"));
        CONTENT.add(new SpinnerItem("热点", "1021"));
        CONTENT.add(new SpinnerItem("推荐", "1022"));
        CONTENT.add(new SpinnerItem("美女", "1024"));
        CONTENT.add(new SpinnerItem("搞笑", "1025"));
        CONTENT.add(new SpinnerItem("聚合", "1032"));
        CONTENT.add(new SpinnerItem("视频", "1033"));
        CONTENT.add(new SpinnerItem("女人", "1034"));
        CONTENT.add(new SpinnerItem("生活", "1035"));
        CONTENT.add(new SpinnerItem("文化", "1036"));
        CONTENT.add(new SpinnerItem("游戏", "1040"));
        CONTENT.add(new SpinnerItem("母婴", "1042"));
        CONTENT.add(new SpinnerItem("看点", "1047"));
        CONTENT.add(new SpinnerItem("动漫", "1055"));
        CONTENT.add(new SpinnerItem("音乐-视频频道", "1058"));
        CONTENT.add(new SpinnerItem("搞笑-视频频道", "1059"));
        CONTENT.add(new SpinnerItem("影视-视频频道", "1060"));
        CONTENT.add(new SpinnerItem("娱乐-视频频道", "1061"));
        CONTENT.add(new SpinnerItem("小品-视频频道", "1062"));
        CONTENT.add(new SpinnerItem("萌萌哒-视频频道", "1065"));
        CONTENT.add(new SpinnerItem("生活-视频频道", "1066"));
        CONTENT.add(new SpinnerItem("游戏-视频频道", "1067"));
        CONTENT.add(new SpinnerItem("本地化", "1080"));
        initView();

    }

    private void initView() {
        adLinearLayout= (LinearLayout) findViewById(R.id.adLinearLayout);
        adControl=new ADControl();
        pager = (ViewPager) findViewById(R.id.pager);
        action_back= (ImageView) findViewById(R.id.action_back);
        action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CpuwebFrag currentFrag = (CpuwebFrag) getFragment(currentPage);
                if (currentFrag != null && currentFrag.canGoBack()) {
                    currentFrag.goBack();
                } else {
                    finish();
                }
            }
        });
        pager.setOffscreenPageLimit(1);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        fManager = getSupportFragmentManager();
        adapter = new GoogleMusicAdapter(fManager);
        pager.setAdapter(adapter);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicator.setViewPager(pager);
        indicator.setCurrentItem(0);

    }


    /**
     * 标题
     */
    private List<SpinnerItem> CONTENT = new ArrayList<SpinnerItem>();

    class SpinnerItem {
        /**
         * 频道名称
         */
        String name;
        /**
         * 频道id
         */
        String id;

        public SpinnerItem(String name, String id) {
            this.name = name;
            this.id = id;
        }

    }

    /**
     * 滑动
     *
     * @author Administrator
     */
    class GoogleMusicAdapter extends FragmentPagerAdapter {

        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT.get(position).name;
        }

        @Override
        public int getCount() {
            return CONTENT.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private Fragment getFragment(int position) {
        Fragment re = null;
        if (iMemoryCache.containsKey(position) && iMemoryCache.get(position) != null) {
            System.out.println("页面来自缓存");
            return iMemoryCache.get(position);
        }

        re = new CpuwebFrag(CONTENT.get(position).id);

        iMemoryCache.put(position, re);
        return re;
    }




    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        adControl.addAd(adLinearLayout,this);
        if (System.currentTimeMillis() - BaseActivity.time > 120 * 1000) {
            BaseActivity.time = System.currentTimeMillis();
            adControl.homeGet5Score(this);
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }



    @Override
    public void onBackPressed() {
        CpuwebFrag currentFrag = (CpuwebFrag) getFragment(currentPage);
        if (currentFrag != null && currentFrag.canGoBack()) {
            currentFrag.goBack();
        } else {
            finish();
        }
    }


}
