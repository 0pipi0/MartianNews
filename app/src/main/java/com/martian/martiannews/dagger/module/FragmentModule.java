package com.martian.martiannews.dagger.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangpei on 2016/12/9.
 */
@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @PerFragment
    @ContextLife("Activity")
    public Context provideActivityContext() {
        return fragment.getActivity();
    }

    @Provides
    @PerFragment
    public Activity provideActivity() {
        return fragment.getActivity();
    }

    @Provides
    @PerFragment
    public Fragment provideFragment() {
        return fragment;
    }

}
