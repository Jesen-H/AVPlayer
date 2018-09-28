package com.hgeson.avplayer;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.hgeson.avplayer.base.BaseActivity;
import com.hgeson.avplayer.base.BaseFragmentPagerAdapter;
import com.hgeson.avplayer.utils.EventUtil;
import com.hgeson.avplayer.view.CustomViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tl_title_bar)
    TabLayout tlTitleBar;
    @BindView(R.id.vp_title_bar)
    CustomViewPager vpTitleBar;
    @BindView(R.id.question_to_top_ll)
    LinearLayout questionTop;

    private String[] mTitle = new String[]{"视频", "音频"};
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
        vpTitleBar.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), mTitle, fragments));
        vpTitleBar.setScanScroll(false);
        tlTitleBar.setupWithViewPager(vpTitleBar);

//        tlTitleBar.getTabAt(0).setText("123");
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(getString(R.string.full_show))) {
            tlTitleBar.setVisibility(View.GONE);
            questionTop.setVisibility(View.GONE);
        } else if (action.equals(getString(R.string.small_show))) {
            tlTitleBar.setVisibility(View.VISIBLE);
            questionTop.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.question_to_top_ll)
    public void onViewClicked() {
        EventUtil.post(getString(R.string.back_top));
    }
}
