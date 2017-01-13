package com.martian.martiannews.mvp.ui.adapter.PagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangpei on 2016/12/10.
 */

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    private final List<String> mTitles;
    private List<Fragment> mNewsFragmentList = new ArrayList<>();

    public NewsFragmentPagerAdapter(FragmentManager fm, List<String> mTitles, List<Fragment> mNewsFragmentList) {
        super(fm);
        this.mTitles = mTitles;
        this.mNewsFragmentList = mNewsFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mNewsFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mNewsFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
