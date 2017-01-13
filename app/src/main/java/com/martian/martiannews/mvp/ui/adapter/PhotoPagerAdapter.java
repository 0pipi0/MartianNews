package com.martian.martiannews.mvp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.martian.martiannews.mvp.ui.fragment.PhotoDetailFragment;

import java.util.List;

/**
 * Created by yangpei on 2016/12/22.
 */

public class PhotoPagerAdapter extends FragmentStatePagerAdapter {

    private List<PhotoDetailFragment> mFragmentList;

    public PhotoPagerAdapter(FragmentManager fm, List<PhotoDetailFragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
