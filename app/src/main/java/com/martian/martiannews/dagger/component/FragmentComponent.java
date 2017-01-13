package com.martian.martiannews.dagger.component;

import android.app.Activity;
import android.content.Context;

import com.martian.martiannews.dagger.module.ApplicationModule;
import com.martian.martiannews.dagger.module.FragmentModule;
import com.martian.martiannews.dagger.scope.ContextLife;
import com.martian.martiannews.dagger.scope.PerFragment;
import com.martian.martiannews.mvp.ui.fragment.NewsListFragment;

import dagger.Component;

/**
 * Created by yangpei on 2016/12/9.
 */
@PerFragment
@Component(dependencies = ApplicationComponent.class,modules = FragmentModule.class)
public interface FragmentComponent {
    @ContextLife("Application")
    Context getApplicatonContext();
    @ContextLife("Activity")
    Context getActivityContext();

    Activity getActivity();

    void inject(NewsListFragment newsListFragment);

}
