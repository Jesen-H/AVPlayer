package com.hgeson.avplayer.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 轻吻旧时光 on 2017/5/11.
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;
    private ArrayList<Fragment> fragmentList;

    public BaseFragmentPagerAdapter(FragmentManager fm, String[] mTitles, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.mTitles = mTitles;
        this.fragmentList = fragmentList;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}
