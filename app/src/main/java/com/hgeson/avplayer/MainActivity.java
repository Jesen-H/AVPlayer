package com.hgeson.avplayer;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hgeson.avplayer.base.BaseActivity;
import com.hgeson.avplayer.base.BaseFragmentPagerAdapter;
import com.hgeson.avplayer.view.CustomViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tl_title_bar)
    TabLayout tlTitleBar;
    @BindView(R.id.vp_title_bar)
    CustomViewPager vpTitleBar;

    private String[] mTitle = new String[]{"视频","音频"};
    private BaseFragmentPagerAdapter adapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected int setContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        fragments.add(new VideoFragment());
        fragments.add(new AudioFragment());
        vpTitleBar.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(),mTitle,fragments));
        tlTitleBar.setupWithViewPager(vpTitleBar);

//        tlTitleBar.getTabAt(0).setText("123");
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (action.equals("全屏显示")){
            tlTitleBar.setVisibility(View.GONE);
        }else if (action.equals("缩小显示")){
            tlTitleBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
